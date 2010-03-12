/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.struts.customizer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.Form;
import org.apache.commons.validator.Var;
import org.apache.struts.action.ActionMessages;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.container.ComponentCustomizer;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.MethodUtil;
import org.seasar.framework.util.ModifierUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.tiger.AnnotationUtil;
import org.seasar.struts.action.ActionFormWrapperClass;
import org.seasar.struts.action.S2DynaProperty;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Arg;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.annotation.Msg;
import org.seasar.struts.annotation.Validator;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.config.S2FormBeanConfig;
import org.seasar.struts.config.S2ModuleConfig;
import org.seasar.struts.config.S2ValidationConfig;
import org.seasar.struts.exception.DuplicateExecuteMethodAndPropertyRuntimeException;
import org.seasar.struts.exception.ExecuteMethodNotFoundRuntimeException;
import org.seasar.struts.exception.IllegalExecuteMethodRuntimeException;
import org.seasar.struts.exception.IllegalValidateMethodRuntimeException;
import org.seasar.struts.exception.IllegalValidatorOfExecuteMethodRuntimeException;
import org.seasar.struts.exception.MultipleAllSelectedUrlPatternRuntimeException;
import org.seasar.struts.exception.UnmatchValidatorAndValidateRuntimeException;
import org.seasar.struts.util.ActionUtil;
import org.seasar.struts.util.MessageResourcesUtil;
import org.seasar.struts.util.S2ModuleConfigUtil;
import org.seasar.struts.util.ValidatorResourcesUtil;
import org.seasar.struts.validator.S2ValidatorResources;

/**
 * Actionのカスタマイザです。
 * 
 * @author higa
 * 
 */
public class ActionCustomizer implements ComponentCustomizer {

    /**
     * バリデータをあらわします。
     */
    protected static final String VALIDATOR = "@";

    public void customize(ComponentDef componentDef) {
        S2ActionMapping actionMapping = createActionMapping(componentDef);
        S2FormBeanConfig formConfig = createFormBeanConfig(actionMapping);
        S2ModuleConfig moduleConfig = S2ModuleConfigUtil.getModuleConfig();
        moduleConfig.addActionConfig(actionMapping);
        moduleConfig.addFormBeanConfig(formConfig);
        S2ValidatorResources validatorResources = ValidatorResourcesUtil
                .getValidatorResources();
        setupValidator(actionMapping, validatorResources);
    }

    /**
     * アクションマッピングを作成します。
     * 
     * @param componentDef
     *            コンポーネント定義
     * @return アクションマッピング
     */
    protected S2ActionMapping createActionMapping(ComponentDef componentDef) {
        S2ActionMapping actionMapping = createActionMapping();
        actionMapping.setPath(ActionUtil.fromActionNameToPath(componentDef
                .getComponentName()));
        actionMapping.setComponentDef(componentDef);
        actionMapping.setName(componentDef.getComponentName() + "Form");
        Class<?> actionClass = componentDef.getComponentClass();
        setupActionForm(actionMapping, actionClass);
        setupMethod(actionMapping, actionClass);
        return actionMapping;
    }

    /**
     * アクションマッピングを作成します。
     * 
     * @return アクションマッピング
     */
    protected S2ActionMapping createActionMapping() {
        return new S2ActionMapping();
    }

    /**
     * メソッドの情報をセットアップします。
     * 
     * @param actionMapping
     *            アクションマッピング
     * @param actionClass
     *            アクションクラス
     */
    protected void setupMethod(S2ActionMapping actionMapping,
            Class<?> actionClass) {
        S2ExecuteConfig allSelectedExecuteConfig = null;
        for (Class<?> clazz = actionClass; clazz != Object.class; clazz = clazz
                .getSuperclass()) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (!ModifierUtil.isPublic(m)) {
                    continue;
                }
                Execute execute = m.getAnnotation(Execute.class);
                if (execute == null) {
                    continue;
                }
                if (actionMapping.getExecuteConfig(m.getName()) != null) {
                    continue;
                }
                if (m.getParameterTypes().length > 0
                        || m.getReturnType() != String.class) {
                    throw new IllegalExecuteMethodRuntimeException(actionClass,
                            m.getName());
                }
                if (actionMapping.getActionFormBeanDesc().hasPropertyDesc(
                        m.getName())) {
                    throw new DuplicateExecuteMethodAndPropertyRuntimeException(
                            actionClass, m.getName());
                }
                String input = !StringUtil.isEmpty(execute.input()) ? execute
                        .input() : null;
                S2ExecuteConfig executeConfig = createExecuteConfig();
                executeConfig.setMethod(m);
                executeConfig.setSaveErrors(execute.saveErrors());
                executeConfig.setInput(input);
                List<S2ValidationConfig> validationConfigs = new ArrayList<S2ValidationConfig>();
                String validate = execute.validate();
                boolean validator = false;
                if (!StringUtil.isEmpty(validate)) {
                    BeanDesc actionBeanDesc = actionMapping.getActionBeanDesc();
                    BeanDesc actionFormBeanDesc = actionMapping
                            .getActionFormBeanDesc();
                    for (String name : StringUtil.split(validate, ", ")) {
                        if (VALIDATOR.equals(name)) {
                            if (!execute.validator()) {
                                throw new UnmatchValidatorAndValidateRuntimeException(
                                        actionClass, m.getName());
                            }
                            validationConfigs.add(new S2ValidationConfig());
                            validator = true;
                        } else if (actionFormBeanDesc.hasMethod(name)) {
                            Method validateMethod = actionFormBeanDesc
                                    .getMethod(name);
                            if (validateMethod.getParameterTypes().length > 0
                                    || !ActionMessages.class
                                            .isAssignableFrom(validateMethod
                                                    .getReturnType())) {
                                throw new IllegalValidateMethodRuntimeException(
                                        actionClass, validateMethod.getName());

                            }
                            validationConfigs.add(new S2ValidationConfig(
                                    validateMethod));
                        } else {
                            Method validateMethod = actionBeanDesc
                                    .getMethod(name);
                            if (validateMethod.getParameterTypes().length > 0
                                    || !ActionMessages.class
                                            .isAssignableFrom(validateMethod
                                                    .getReturnType())) {
                                throw new IllegalValidateMethodRuntimeException(
                                        actionClass, validateMethod.getName());

                            }
                            validationConfigs.add(new S2ValidationConfig(
                                    validateMethod));
                        }
                    }
                }
                if (!validator && execute.validator()) {
                    validationConfigs.add(0, new S2ValidationConfig());
                }
                if (!validationConfigs.isEmpty() && input == null) {
                    throw new IllegalValidatorOfExecuteMethodRuntimeException(
                            actionClass, m.getName());
                }
                executeConfig.setValidationConfigs(validationConfigs);
                executeConfig.setUrlPattern(execute.urlPattern());
                String roles = execute.roles().trim();
                if (!StringUtil.isEmpty(roles)) {
                    executeConfig.setRoles(StringUtil.split(roles, ", "));
                }
                executeConfig.setStopOnValidationError(execute
                        .stopOnValidationError());
                executeConfig.setRemoveActionForm(execute.removeActionForm());
                String reset = execute.reset();
                if (!StringUtil.isEmpty(reset)) {
                    Method resetMethod = null;
                    if ("reset".equals(reset)) {
                        resetMethod = actionMapping.getActionFormBeanDesc()
                                .getMethodNoException(reset);
                    } else {
                        resetMethod = actionMapping.getActionFormBeanDesc()
                                .getMethod(reset);
                    }
                    if (resetMethod != null) {
                        executeConfig.setResetMethod(resetMethod);
                    }
                }
                executeConfig.setRedirect(execute.redirect());
                if (executeConfig.isUrlPatternAllSelected()) {
                    if (allSelectedExecuteConfig != null) {
                        throw new MultipleAllSelectedUrlPatternRuntimeException(
                                allSelectedExecuteConfig.getUrlPattern(),
                                executeConfig.getUrlPattern());
                    }
                    allSelectedExecuteConfig = executeConfig;
                } else {
                    actionMapping.addExecuteConfig(executeConfig);
                }
            }
        }
        if (allSelectedExecuteConfig != null) {
            actionMapping.addExecuteConfig(allSelectedExecuteConfig);
        }
        if (actionMapping.getExecuteConfigSize() == 0) {
            throw new ExecuteMethodNotFoundRuntimeException(actionClass);
        }
    }

    /**
     * 実行設定を作成します。
     * 
     * @return 実行設定
     */
    protected S2ExecuteConfig createExecuteConfig() {
        return new S2ExecuteConfig();
    }

    /**
     * アクションフォームの情報をセットアップします。
     * 
     * @param actionMapping
     *            アクションマッピング
     * @param actionClass
     *            アクションクラス
     */
    protected void setupActionForm(S2ActionMapping actionMapping,
            Class<?> actionClass) {
        int size = actionMapping.getActionBeanDesc().getFieldSize();
        BeanDesc beanDesc = actionMapping.getActionBeanDesc();
        for (int i = 0; i < size; i++) {
            Field f = beanDesc.getField(i);
            if (f.getAnnotation(ActionForm.class) != null) {
                actionMapping.setActionFormField(f);
                return;
            }

        }
    }

    /**
     * アクションフォーム設定を作成します。
     * 
     * @param actionMapping
     *            アクションマッピング
     * @return アクションフォーム設定
     */
    protected S2FormBeanConfig createFormBeanConfig(
            S2ActionMapping actionMapping) {

        S2FormBeanConfig formConfig = createFormBeanConfig();
        formConfig.setName(actionMapping.getName());
        ActionFormWrapperClass wrapperClass = createActionFormWrapperClass(actionMapping);
        BeanDesc beanDesc = actionMapping.getActionFormBeanDesc();
        for (int i = 0; i < beanDesc.getPropertyDescSize(); i++) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            if (pd.isReadable()) {
                S2DynaProperty property = createDynaProperty(pd);
                wrapperClass.addDynaProperty(property);
            }
        }
        formConfig.setDynaClass(wrapperClass);
        return formConfig;
    }

    /**
     * フォームBean設定を作成します。
     * 
     * @return フォームBean設定
     * 
     */
    protected S2FormBeanConfig createFormBeanConfig() {
        return new S2FormBeanConfig();
    }

    /**
     * アクションフォームラッパーを作成します。
     * 
     * @param actionMapping
     *            アクションマッピング
     * @return アクションフォームラッパー
     */
    protected ActionFormWrapperClass createActionFormWrapperClass(
            S2ActionMapping actionMapping) {
        return new ActionFormWrapperClass(actionMapping);
    }

    /**
     * 動的プロパティを作成します。
     * 
     * @param pd
     *            プロパティ記述
     * @return 動的プロパティ
     */
    protected S2DynaProperty createDynaProperty(PropertyDesc pd) {
        return new S2DynaProperty(pd);
    }

    /**
     * バリデータをセットアップします。
     * 
     * 
     * @param actionMapping
     *            アクションマッピング
     * @param validatorResources
     *            検証リソース
     */
    protected void setupValidator(S2ActionMapping actionMapping,
            S2ValidatorResources validatorResources) {
        Map<String, Form> forms = new HashMap<String, Form>();
        for (String methodName : actionMapping.getExecuteMethodNames()) {
            if (actionMapping.getExecuteConfig(methodName).isValidator()) {
                Form form = new Form();
                form.setName(actionMapping.getName() + "_" + methodName);
                forms.put(methodName, form);
            }
        }
        for (Class<?> clazz = actionMapping.getActionFormBeanDesc()
                .getBeanClass(); clazz != null && clazz != Object.class; clazz = clazz
                .getSuperclass()) {
            for (Field field : ClassUtil.getDeclaredFields(clazz)) {
                for (Annotation anno : field.getDeclaredAnnotations()) {
                    processAnnotation(field.getName(), anno,
                            validatorResources, forms);
                }
            }
        }
        for (Iterator<Form> i = forms.values().iterator(); i.hasNext();) {
            validatorResources.addForm(i.next());
        }

    }

    /**
     * アノテーションを処理します。
     * 
     * 
     * @param propertyName
     *            プロパティ名
     * @param annotation
     *            アノテーション
     * @param validatorResources
     *            検証リソース
     * @param forms
     *            メソッド名をキーにしたフォームのマップ
     */
    protected void processAnnotation(String propertyName,
            Annotation annotation, S2ValidatorResources validatorResources,
            Map<String, Form> forms) {
        Class<? extends Annotation> annotationType = annotation
                .annotationType();
        Annotation metaAnnotation = annotationType
                .getAnnotation(Validator.class);
        if (metaAnnotation == null) {
            return;
        }
        String validatorName = getValidatorName(metaAnnotation);
        Map<String, Object> props = AnnotationUtil.getProperties(annotation);
        registerValidator(propertyName, validatorName, props,
                validatorResources, forms);
    }

    /**
     * バリデータ名を返します。
     * 
     * @param annotation
     *            検証アノテーション
     * @return バリデータ名
     */
    protected String getValidatorName(Annotation annotation) {
        Class<? extends Annotation> annoType = annotation.annotationType();
        Method m = ClassUtil.getMethod(annoType, "value", null);
        return (String) MethodUtil.invoke(m, annotation, null);
    }

    /**
     * バリデータを登録します。
     * 
     * 
     * @param propertyName
     *            プロパティ名
     * @param validatorName
     *            バリデータ名
     * @param props
     *            バリデータのプロパティ
     * @param validatorResources
     *            検証リソース
     * @param forms
     *            メソッド名をキーにしたフォームのマップ
     */
    protected void registerValidator(String propertyName, String validatorName,
            Map<String, Object> props, S2ValidatorResources validatorResources,
            Map<String, Form> forms) {
        org.apache.commons.validator.Field field = createField(propertyName,
                validatorName, props, validatorResources);
        for (Iterator<String> i = forms.keySet().iterator(); i.hasNext();) {
            String methodName = i.next();
            if (!isTarget(methodName, (String) props.get("target"))) {
                continue;
            }
            Form form = forms.get(methodName);
            form.addField(field);
        }
    }

    /**
     * 検証用のフィールドを作成します。
     * 
     * @param propertyName
     *            プロパティ名
     * @param validatorName
     *            バリデータ名
     * @param props
     *            バリデータのプロパティ
     * @param validatorResources
     *            検証リソース
     * @return 検証用のフィールド
     */
    protected org.apache.commons.validator.Field createField(
            String propertyName, String validatorName,
            Map<String, Object> props, S2ValidatorResources validatorResources) {
        org.apache.commons.validator.Field field = new org.apache.commons.validator.Field();
        field.setDepends(validatorName);
        field.setProperty(propertyName);
        Msg msg = (Msg) props.remove("msg");
        if (msg != null) {
            org.apache.commons.validator.Msg m = new org.apache.commons.validator.Msg();
            m.setName(validatorName);
            m.setKey(msg.key());
            String bundle = msg.bundle();
            if (!StringUtil.isEmpty(bundle)) {
                m.setBundle(bundle);
            }
            m.setResource(msg.resource());
            field.addMsg(m);
        }
        Arg[] args = (Arg[]) props.remove("args");
        if (args != null && args.length > 0) {
            for (Arg arg : args) {
                org.apache.commons.validator.Arg a = new org.apache.commons.validator.Arg();
                a.setKey(resolveKey(arg.key(), arg.resource(), props,
                        validatorResources));
                String bundle = arg.bundle();
                if (!StringUtil.isEmpty(bundle)) {
                    a.setBundle(bundle);
                }
                a.setResource(arg.resource());
                a.setPosition(arg.position());
                field.addArg(a);
            }
        }
        for (int i = 0; i < 5; i++) {
            Arg arg = (Arg) props.remove("arg" + i);
            if (arg != null && !StringUtil.isEmpty(arg.key())) {
                org.apache.commons.validator.Arg a = new org.apache.commons.validator.Arg();
                a.setKey(resolveKey(arg.key(), arg.resource(), props,
                        validatorResources));
                String bundle = arg.bundle();
                if (!StringUtil.isEmpty(bundle)) {
                    a.setBundle(bundle);
                }
                a.setResource(arg.resource());
                a.setPosition(i);
                field.addArg(a);
            } else if (i == 0) {
                org.apache.commons.validator.Arg a = new org.apache.commons.validator.Arg();
                String key = "labels." + propertyName;
                String message = MessageResourcesUtil.getMessage(key);
                if (!StringUtil.isEmpty(message)) {
                    a.setKey(key);
                } else {
                    a.setKey(propertyName);
                    a.setResource(false);
                }
                a.setPosition(0);
                field.addArg(a);
            }
        }

        for (Iterator<String> i = props.keySet().iterator(); i.hasNext();) {
            String name = i.next();
            if (name.equals("target")) {
                continue;
            }
            Object value = props.get(name);
            String jsType = Var.JSTYPE_STRING;
            if (value instanceof Number) {
                jsType = Var.JSTYPE_INT;
            } else if (name.equals("mask")) {
                jsType = Var.JSTYPE_REGEXP;
            }
            field.addVar(name, value.toString(), jsType);
        }
        return field;
    }

    /**
     * キーに変数が使われていた場合にその変数を解決します。
     * 
     * @param key
     *            キー
     * @param resource
     *            リソースを使うかどうか
     * @param props
     *            バリデータのプロパティ
     * @param validatorResources
     *            検証リソース
     * 
     * @return 解決されたキー
     */
    protected String resolveKey(String key, boolean resource,
            Map<String, Object> props, S2ValidatorResources validatorResources) {
        if (resource) {
            return key;
        }
        if (key.startsWith("${") && key.endsWith("}")) {
            String s = key.substring(2, key.length() - 1);
            if (s.startsWith("var:")) {
                s = s.substring(4);
                return String.valueOf(props.get(s));
            }
            return validatorResources.getConstant(s);
        }
        return key;
    }

    /**
     * 対象のメソッドかどうかを返します。
     * 
     * @param methodName
     *            メソッド名
     * @param target
     *            メソッド名がカンマ区切りで指定されたもの
     * @return 対象のメソッドかどうか
     */
    protected boolean isTarget(String methodName, String target) {
        if (StringUtil.isEmpty(target)) {
            return true;
        }
        String[] names = StringUtil.split(target, ", ");
        for (String name : names) {
            if (methodName.equals(name.trim())) {
                return true;
            }
        }
        return false;
    }
}