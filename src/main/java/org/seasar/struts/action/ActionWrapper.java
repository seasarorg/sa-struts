/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.struts.action;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorException;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.Resources;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.util.MethodUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.enums.SaveType;
import org.seasar.struts.util.ActionFormUtil;
import org.seasar.struts.util.ServletContextUtil;

/**
 * POJO Actionのラッパーです。
 * 
 * @author higa
 * 
 */
public class ActionWrapper extends Action {

    /**
     * アクションマッピングです。
     */
    protected S2ActionMapping actionMapping;

    /**
     * アクションです。
     */
    protected Object action;

    /**
     * アクションフォームです。
     */
    protected Object actionForm;

    /**
     * インスタンスを構築します。
     * 
     * @param actionMapping
     *            アクションマッピング
     */
    public ActionWrapper(S2ActionMapping actionMapping) {
        this.actionMapping = actionMapping;
        action = actionMapping.getAction();
        actionForm = actionMapping.getActionForm();
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String[] names = actionMapping.getExecuteMethodNames();
        if (names.length == 1) {
            return execute(names[0], request);
        }
        for (String name : names) {
            if (!StringUtil.isEmpty(request.getParameter(name))) {
                return execute(name, request);
            }
        }
        if (actionMapping.getExecuteConfig("execute") != null) {
            return execute("execute", request);
        }
        return null;
    }

    /**
     * Actionを実行します。
     * 
     * @param methodName
     *            メソッド名
     * @param request
     *            リクエスト
     * @return アクションフォワード
     */
    protected ActionForward execute(String methodName,
            HttpServletRequest request) {
        S2ExecuteConfig executeConfig = actionMapping
                .getExecuteConfig(methodName);
        if (executeConfig.isValidator()) {
            ActionMessages errors = validate(methodName, request);
            if (errors != null && !errors.isEmpty()) {
                return processErrors(errors, request, executeConfig);
            }
        }
        Method validateMethod = executeConfig.getValidateMethod();
        if (validateMethod != null) {
            ActionMessages errors = (ActionMessages) MethodUtil.invoke(
                    validateMethod, action, null);
            if (errors != null && !errors.isEmpty()) {
                return processErrors(errors, request, executeConfig);
            }
        }
        String next = (String) MethodUtil.invoke(executeConfig.getMethod(),
                action, null);
        exportPropertiesToRequest(request);
        return actionMapping.findForward(next);
    }

    /**
     * バリデータによる検証を行います。
     * 
     * @param methodName
     *            メソッド名
     * @param request
     *            リクエスト
     * @return エラーメッセージ
     */
    protected ActionMessages validate(String methodName,
            HttpServletRequest request) {
        ServletContext application = ServletContextUtil.getServletContext();
        ActionMessages errors = new ActionMessages();
        String validationKey = actionMapping.getName() + "_" + methodName;
        Validator validator = Resources.initValidator(validationKey,
                ActionFormUtil.getActionForm(request, actionMapping),
                application, request, errors, 0);
        try {
            validator.validate();
        } catch (ValidatorException e) {
            throw new RuntimeException(e);
        }
        return errors;
    }

    /**
     * プロパティをリクエストに設定します。 *
     * 
     * @param request
     *            リクエスト
     */
    protected void exportPropertiesToRequest(HttpServletRequest request) {
        BeanDesc beanDesc = actionMapping.getActionFormBeanDesc();
        for (int i = 0; i < beanDesc.getPropertyDescSize(); i++) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            Object value = WrapperUtil.convert(pd.getValue(actionForm));
            request.setAttribute(pd.getPropertyName(), value);
        }

    }

    /**
     * 検証エラーの処理を行います。
     * 
     * @param errors
     *            エラーメッセージ
     * @param request
     *            リクエスト
     * @param executeConfig
     *            実行設定
     * @return アクションフォワード
     */
    protected ActionForward processErrors(ActionMessages errors,
            HttpServletRequest request, S2ExecuteConfig executeConfig) {
        if (executeConfig.getSaveErrors() == SaveType.REQUEST) {
            request.setAttribute(Globals.ERROR_KEY, errors);
        } else {
            request.getSession().setAttribute(Globals.ERROR_KEY, errors);
        }
        exportPropertiesToRequest(request);
        return actionMapping.findForward(actionMapping.getInput());
    }
}