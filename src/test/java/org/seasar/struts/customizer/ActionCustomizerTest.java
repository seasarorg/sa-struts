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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.Var;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResourcesFactory;
import org.apache.struts.validator.ValidatorPlugIn;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.beans.MethodNotFoundRuntimeException;
import org.seasar.framework.util.tiger.AnnotationUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.annotation.Msg;
import org.seasar.struts.annotation.Required;
import org.seasar.struts.annotation.Validator;
import org.seasar.struts.annotation.Validwhen;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.config.S2FormBeanConfig;
import org.seasar.struts.config.S2ModuleConfig;
import org.seasar.struts.config.S2ValidationConfig;
import org.seasar.struts.enums.SaveType;
import org.seasar.struts.exception.DuplicateExecuteMethodAndPropertyRuntimeException;
import org.seasar.struts.exception.ExecuteMethodNotFoundRuntimeException;
import org.seasar.struts.exception.IllegalExecuteMethodRuntimeException;
import org.seasar.struts.exception.IllegalValidateMethodRuntimeException;
import org.seasar.struts.exception.IllegalValidatorOfExecuteMethodRuntimeException;
import org.seasar.struts.exception.MultipleAllSelectedUrlPatternRuntimeException;
import org.seasar.struts.exception.UnmatchValidatorAndValidateRuntimeException;
import org.seasar.struts.util.S2PropertyMessageResourcesFactory;
import org.seasar.struts.util.ValidatorResourcesUtil;
import org.seasar.struts.validator.S2ValidatorResources;

/**
 * @author higa
 * 
 */
public class ActionCustomizerTest extends S2TestCase {

    private ActionCustomizer customizer = new ActionCustomizer();

    private S2ModuleConfig moduleConfig = new S2ModuleConfig("");

    private S2ValidatorResources validatorResources = new S2ValidatorResources();

    @Override
    public void setUp() {
        getServletContext().setAttribute(Globals.SERVLET_KEY, "/*");
        getServletContext().setAttribute(Globals.MODULE_KEY, moduleConfig);
        MessageResourcesFactory mrf = new S2PropertyMessageResourcesFactory();
        getServletContext().setAttribute(Globals.MESSAGES_KEY,
                mrf.createResources("SASMessages"));
        register(BbbAction.class, "aaa_bbbAction");
        getServletContext().setAttribute(ValidatorPlugIn.VALIDATOR_KEY,
                validatorResources);
    }

    /**
     * @throws Exception
     */
    public void testCustomize_actionConfig() throws Exception {
        customizer.customize(getComponentDef("aaa_bbbAction"));
        assertNotNull(moduleConfig.findActionConfig("/aaa/bbb"));
    }

    /**
     * @throws Exception
     */
    public void testCustomize_formBeanConfig() throws Exception {
        customizer.customize(getComponentDef("aaa_bbbAction"));
        assertNotNull(moduleConfig.findFormBeanConfig("aaa_bbbActionForm"));
    }

    /**
     * @throws Exception
     */
    public void testCustomize_formSet() throws Exception {
        customizer.customize(getComponentDef("aaa_bbbAction"));
        assertNotNull(ValidatorResourcesUtil.getValidatorResources().getForm(
                Locale.getDefault(), "aaa_bbbActionForm_execute2"));
    }

    /**
     * @throws Exception
     */
    public void testCreateActionMapping_path() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        assertNotNull(actionMapping);
        assertEquals("/aaa/bbb", actionMapping.getPath());
    }

    /**
     * @throws Exception
     */
    public void testCreateActionMapping_componentDef() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        assertNotNull(actionMapping.getComponentDef());
    }

    /**
     * @throws Exception
     */
    public void testCreateActionMapping_name() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        assertEquals("aaa_bbbActionForm", actionMapping.getName());
    }

    /**
     * @throws Exception
     */
    public void testCreateActionMapping_type() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        assertEquals(BbbAction.class.getName(), actionMapping.getType());
    }

    /**
     * @throws Exception
     */
    public void testSetupMethod() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        S2ExecuteConfig executeConfig = actionMapping
                .getExecuteConfig("execute");
        assertNotNull(executeConfig);
        assertNotNull(executeConfig.getMethod());
        assertFalse(executeConfig.isValidator());
        assertEquals(1, executeConfig.getValidationConfigs().size());
        assertEquals(SaveType.REQUEST, executeConfig.getSaveErrors());
        assertEquals("/aaa/input2.jsp", executeConfig.getInput());
        String[] roles = executeConfig.getRoles();
        assertNotNull(roles);
        assertEquals(2, roles.length);
        assertEquals("admin", roles[0]);
        assertEquals("user", roles[1]);
        assertEquals(3, actionMapping.getExecuteConfigSize());
        assertFalse(executeConfig.isStopOnValidationError());
        assertTrue(executeConfig.isRemoveActionForm());
        assertEquals("reset", executeConfig.getResetMethod().getName());
        assertTrue(executeConfig.isRedirect());
    }

    /**
     * @throws Exception
     */
    public void testSetupMethod_multiValidation() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        S2ExecuteConfig executeConfig = actionMapping
                .getExecuteConfig("execute3");
        assertNotNull(executeConfig);
        assertTrue(executeConfig.isValidator());
        List<S2ValidationConfig> configs = executeConfig.getValidationConfigs();
        assertEquals(3, configs.size());
        assertFalse(configs.get(0).isValidator());
        assertNotNull(configs.get(0).getValidateMethod());
        assertTrue(configs.get(1).isValidator());
        assertNull(configs.get(1).getValidateMethod());
        assertFalse(configs.get(2).isValidator());
        assertNotNull(configs.get(2).getValidateMethod());
    }

    /**
     * @throws Exception
     */
    public void testSetupMethod_validateMethod_actionForm() throws Exception {
        register(CccAction.class, "cccAction");
        register(CccActionForm.class, "cccActionForm");
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("cccAction"));
        S2ExecuteConfig executeConfig = actionMapping
                .getExecuteConfig("execute");
        assertNotNull(executeConfig);
        assertTrue(executeConfig.isValidator());
        List<S2ValidationConfig> configs = executeConfig.getValidationConfigs();
        assertEquals(2, configs.size());
        assertTrue(configs.get(0).isValidator());
        assertNull(configs.get(0).getValidateMethod());
        assertFalse(configs.get(1).isValidator());
        Method m = configs.get(1).getValidateMethod();
        assertNotNull(m);
        assertEquals(CccActionForm.class, m.getDeclaringClass());
    }

    /**
     * @throws Exception
     */
    public void testSetupMethod_inherit() throws Exception {
        register(KkkAction.class, "kkkAction");
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("kkkAction"));
        assertNotNull(actionMapping.getExecuteConfig("index"));
        assertNotNull(actionMapping.getExecuteConfig("execute"));
        assertNotNull(actionMapping.getExecuteConfig("execute2"));
    }

    /**
     * @throws Exception
     */
    public void testSetupMethod_allSelectedUrlPattern() throws Exception {
        register(IiiAction.class, "iiiAction");
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("iiiAction"));
        S2ExecuteConfig executeConfig = actionMapping
                .findExecuteConfig("execute");
        assertNotNull(executeConfig);
        assertEquals("execute", executeConfig.getMethod().getName());
    }

    /**
     * @throws Exception
     */
    public void testSetupMethod_multipleAllSelectedUrlPattern()
            throws Exception {
        register(JjjAction.class, "jjjAction");
        try {
            customizer.createActionMapping(getComponentDef("jjjAction"));
            fail();
        } catch (MultipleAllSelectedUrlPatternRuntimeException e) {
            System.out.println(e);
        }
    }

    /**
     * @throws Exception
     */
    public void testSetupMethod_illegalExecuteMethod() throws Exception {
        register(DddAction.class, "aaa_dddAction");
        try {
            customizer.createActionMapping(getComponentDef("aaa_dddAction"));
            fail();
        } catch (IllegalExecuteMethodRuntimeException e) {
            System.out.println(e);
            assertEquals(DddAction.class, e.getActionClass());
            assertEquals("execute", e.getExecuteMethodName());
        }
    }

    /**
     * @throws Exception
     */
    public void testSetupMethod_executeMethodEmpty() throws Exception {
        register(EeeAction.class, "aaa_eeeAction");
        try {
            customizer.createActionMapping(getComponentDef("aaa_eeeAction"));
            fail();
        } catch (ExecuteMethodNotFoundRuntimeException e) {
            System.out.println(e);
            assertEquals(EeeAction.class, e.getTargetClass());
        }
    }

    /**
     * @throws Exception
     */
    public void testSetupMethod_inputNotDefined() throws Exception {
        register(FffAction.class, "aaa_fffAction");
        try {
            customizer.createActionMapping(getComponentDef("aaa_fffAction"));
            fail();
        } catch (IllegalValidatorOfExecuteMethodRuntimeException e) {
            System.out.println(e);
            assertEquals(FffAction.class, e.getActionClass());
            assertEquals("execute", e.getExecuteMethodName());
        }
    }

    /**
     * @throws Exception
     */
    public void testSetupMethod_illegalValidateMethod() throws Exception {
        register(GggAction.class, "aaa_gggAction");
        try {
            customizer.createActionMapping(getComponentDef("aaa_gggAction"));
            fail();
        } catch (IllegalValidateMethodRuntimeException e) {
            System.out.println(e);
            assertEquals(GggAction.class, e.getActionClass());
            assertEquals("validate", e.getValidateMethodName());
        }
    }

    /**
     * @throws Exception
     */
    public void testSetupMethod_validateNotFound() throws Exception {
        register(HhhAction.class, "aaa_hhhAction");
        try {
            customizer.createActionMapping(getComponentDef("aaa_hhhAction"));
            fail();
        } catch (MethodNotFoundRuntimeException e) {
            System.out.println(e);
            assertEquals(HhhAction.class, e.getTargetClass());
            assertEquals("validate", e.getMethodName());
        }
    }

    /**
     * @throws Exception
     */
    public void testSetupMethod_unmatchValidatorAndValidate() throws Exception {
        register(LllAction.class, "lllAction");
        try {
            customizer.createActionMapping(getComponentDef("lllAction"));
            fail();
        } catch (UnmatchValidatorAndValidateRuntimeException e) {
            System.out.println(e);
            assertEquals(LllAction.class, e.getActionClass());
            assertEquals("execute", e.getExecuteMethodName());
        }
    }

    /**
     * @throws Exception
     */
    public void testSetupMethod_duplicateExecuteMethodAndProperty()
            throws Exception {
        register(MmmAction.class, "mmmAction");
        try {
            customizer.createActionMapping(getComponentDef("mmmAction"));
            fail();
        } catch (DuplicateExecuteMethodAndPropertyRuntimeException e) {
            System.out.println(e);
            assertEquals(MmmAction.class, e.getActionClass());
            assertEquals("index", e.getExecuteMethodName());
        }
    }

    /**
     * @throws Exception
     */
    public void testSetupMethod_inheritedMethod() throws Exception {
        register(NnnAction.class, "nnnAction");
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("nnnAction"));
        S2ExecuteConfig executeConfig = actionMapping
                .findExecuteConfig("index");
        assertNotNull(executeConfig);
        assertEquals(NnnAction.class, executeConfig.getMethod()
                .getDeclaringClass());
    }

    /**
     * @throws Exception
     */
    public void testSetupActionForm() throws Exception {
        register(CccAction.class, "cccAction");
        register(CccActionForm.class, "cccActionForm");
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("cccAction"));
        assertNotNull(actionMapping.getActionFormField());
    }

    /**
     * @throws Exception
     */
    public void testCreateFormBeanConfig_name() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        S2FormBeanConfig formConfig = customizer
                .createFormBeanConfig(actionMapping);
        assertNotNull(formConfig);
        assertEquals("aaa_bbbActionForm", formConfig.getName());
    }

    /**
     * @throws Exception
     */
    public void testCreateFormBeanConfig_dynaClass() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        S2FormBeanConfig formConfig = customizer
                .createFormBeanConfig(actionMapping);
        DynaClass dynaClass = formConfig.getDynaClass();
        assertNotNull(dynaClass);
        assertNotNull(dynaClass.getDynaProperty("hoge"));
    }

    /**
     * @throws Exception
     */
    public void testGetValidatorName() throws Exception {
        Field field = BbbAction.class.getDeclaredField("hoge");
        Required r = field.getAnnotation(Required.class);
        Validator v = r.annotationType().getAnnotation(Validator.class);
        assertEquals("required", customizer.getValidatorName(v));
    }

    /**
     * @throws Exception
     */
    public void testIsTarget() throws Exception {
        assertTrue(customizer.isTarget("hoge", ""));
        assertTrue(customizer.isTarget("hoge", " hoge, foo"));
        assertFalse(customizer.isTarget("bar", "hoge, foo"));
    }

    /**
     * @throws Exception
     */
    public void testCreateField() throws Exception {
        Field field = BbbAction.class.getDeclaredField("hoge");
        Required r = field.getAnnotation(Required.class);
        Map<String, Object> props = AnnotationUtil.getProperties(r);
        org.apache.commons.validator.Field f = customizer.createField("hoge",
                "required", props, validatorResources);
        assertEquals("hoge", f.getProperty());
        assertEquals("required", f.getDepends());
        org.apache.commons.validator.Msg m = f.getMessage("required");
        assertNotNull(m);
        assertEquals("errors.required", m.getKey());
        assertEquals("required", m.getName());
        assertTrue(m.isResource());
        assertNull(m.getBundle());
        org.apache.commons.validator.Arg a = f.getArg("required", 0);
        assertNotNull(a);
        assertEquals("labels.hoge", a.getKey());
        assertNull(a.getName());
        assertTrue(a.isResource());
        assertNull(a.getBundle());
    }

    /**
     * @throws Exception
     */
    public void testCreateField_var() throws Exception {
        Field field = BbbAction.class.getDeclaredField("hoge2");
        Validwhen v = field.getAnnotation(Validwhen.class);
        Map<String, Object> props = AnnotationUtil.getProperties(v);
        org.apache.commons.validator.Field f = customizer.createField("hoge2",
                "validwhen", props, validatorResources);
        org.apache.commons.validator.Var var = f.getVar("test");
        assertNotNull(var);
        assertEquals("test", var.getName());
        assertEquals("true", var.getValue());
        assertEquals(Var.JSTYPE_STRING, var.getJsType());
    }

    /**
     * @throws Exception
     */
    public void testRegisterValidator() throws Exception {
        Map<String, Form> forms = new HashMap<String, Form>();
        Form form = new Form();
        forms.put("execute", form);
        Form form2 = new Form();
        forms.put("execute2", form2);
        Field field = BbbAction.class.getDeclaredField("hoge");
        Required r = field.getAnnotation(Required.class);
        Map<String, Object> props = AnnotationUtil.getProperties(r);
        customizer.registerValidator("hoge", "required", props,
                validatorResources, forms);
        assertNotNull(form.getField("hoge"));
        assertNotNull(form2.getField("hoge"));
    }

    /**
     * @throws Exception
     */
    public void testRegisterValidator_target() throws Exception {
        Map<String, Form> forms = new HashMap<String, Form>();
        Form form = new Form();
        forms.put("execute", form);
        Form form2 = new Form();
        forms.put("execute2", form2);
        Field field = BbbAction.class.getDeclaredField("hoge2");
        Validwhen v = field.getAnnotation(Validwhen.class);
        Map<String, Object> props = AnnotationUtil.getProperties(v);
        customizer.registerValidator("hoge2", "validwhen", props,
                validatorResources, forms);
        assertNotNull(form.getField("hoge2"));
        assertNull(form2.getField("hoge2"));
    }

    /**
     * @throws Exception
     */
    public void testProcessAnnotation() throws Exception {
        Map<String, Form> forms = new HashMap<String, Form>();
        Form form = new Form();
        forms.put("execute", form);
        Field field = BbbAction.class.getDeclaredField("hoge");
        Required r = field.getAnnotation(Required.class);
        customizer.processAnnotation("hoge", r, validatorResources, forms);
        assertNotNull(form.getField("hoge"));
    }

    /**
     * @throws Exception
     */
    public void testSetupValidator() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        customizer.setupValidator(actionMapping, validatorResources);
        Form form = validatorResources.getForm(Locale.getDefault(),
                "aaa_bbbActionForm_execute2");
        assertNotNull(form);
        org.apache.commons.validator.Field f = form.getField("hoge");
        assertEquals("hoge", f.getProperty());
        assertEquals("required", f.getDepends());
    }

    /**
     * @throws Exception
     */
    public void testResolveKey() throws Exception {
        assertEquals("hoge", customizer.resolveKey("hoge", true, null, null));
        assertEquals("hoge", customizer.resolveKey("hoge", false, null, null));
        Map<String, Object> props = new HashMap<String, Object>();
        assertEquals("null", customizer.resolveKey("${var:hoge}", false, props,
                validatorResources));
        props.put("hoge", "aaa");
        assertEquals("aaa", customizer.resolveKey("${var:hoge}", false, props,
                validatorResources));
        validatorResources.addConstant("hoge", "bbb");
        assertEquals("bbb", customizer.resolveKey("${hoge}", false, props,
                validatorResources));
    }

    /**
     * 
     */
    public static class BbbAction {

        /**
         * 
         */
        @Required
        public String hoge;

        /**
         * 
         */
        @Validwhen(test = "true", msg = @Msg(key = "errors.validwhen"), target = "execute")
        public boolean hoge2;

        /**
         * 
         */
        public List<String> hoge3;

        /**
         * @return
         */
        @Execute(validator = false, validate = "validate", input = "/aaa/input2.jsp", roles = "admin,user", stopOnValidationError = false, removeActionForm = true, reset = "reset", redirect = true)
        public String execute() {
            return "input2.jsp";
        }

        /**
         * @return
         */
        @Execute(input = "/aaa/input.jsp")
        public String execute2() {
            return "input.jsp";
        }

        /**
         * @return
         */
        @Execute(validator = true, validate = "validate, @, validate2", input = "/aaa/input.jsp")
        public String execute3() {
            return "input.jsp";
        }

        /**
         * @return
         */
        public ActionMessages validate() {
            return null;
        }

        /**
         * @return
         */
        public ActionMessages validate2() {
            return null;
        }

        /**
         * 
         */
        public void reset() {
        }
    }

    /**
     * 
     */
    public static class CccAction {
        /**
         * 
         */
        @ActionForm
        @Resource
        protected CccActionForm cccActionForm;

        /**
         * @return
         */
        @Execute(validator = true, validate = "validate", input = "index.jsp")
        public String execute() {
            return "success";
        }
    }

    /**
     * 
     */
    public static class DddAction {
        /**
         * @return
         */
        @Execute
        public void execute() {
        }
    }

    /**
     * 
     */
    public static class EeeAction {
        /**
         * @return
         */
        public void execute() {
        }
    }

    /**
     * 
     */
    public static class FffAction {
        /**
         * @return
         */
        @Execute(validate = "validate")
        public String execute() {
            return "success";
        }

        /**
         * @return
         */
        public ActionMessages validate() {
            return null;
        }
    }

    /**
     * 
     */
    public static class GggAction {
        /**
         * @return
         */
        @Execute(validate = "validate")
        public String execute() {
            return "success";
        }

        /**
         * @return
         */
        public String validate() {
            return null;
        }
    }

    /**
     * 
     */
    public static class HhhAction {
        /**
         * @return
         */
        @Execute(validate = "validate", input = "/aaa/input.jsp")
        public String execute() {
            return "success";
        }

        /**
         * @return
         */
        public ActionMessages validate2() {
            return null;
        }
    }

    /**
     * 
     */
    public static class IiiAction {
        /**
         * @return
         */
        @Execute(validator = false, urlPattern = "{id}")
        public String index() {
            return "start.jsp";
        }

        /**
         * @return
         */
        @Execute(validator = false)
        public String execute() {
            return "execute.jsp";
        }
    }

    /**
     * 
     */
    public static class JjjAction {
        /**
         * @return
         */
        @Execute(validator = false, urlPattern = "{id}")
        public String index() {
            return "start.jsp";
        }

        /**
         * @return
         */
        @Execute(validator = false, urlPattern = "{id2}")
        public String execute() {
            return "execute.jsp";
        }
    }

    /**
     * 
     */
    public static class KkkAction extends IiiAction {

        /**
         * @return
         */
        @Execute(validator = false)
        public String execute2() {
            return "execute.jsp";
        }
    }

    /**
     * 
     */
    public static class LllAction {

        /**
         * @return
         */
        @Execute(validator = false, validate = "@")
        public String execute() {
            return "execute.jsp";
        }
    }

    /**
     * 
     */
    public static class MmmAction {

        /**
         * 
         */
        public String index;

        /**
         * @return
         */
        @Execute(validator = false)
        public String index() {
            return "index.jsp";
        }
    }

    /**
     * 
     */
    public static class NnnBaseAction {

        /**
         * @return
         */
        @Execute(validator = false)
        public String index() {
            return "index.jsp";
        }
    }

    /**
     * 
     */
    public static class NnnAction extends NnnBaseAction {

        /**
         * @return
         */
        @Override
        @Execute(validator = false)
        public String index() {
            return "index.jsp";
        }
    }

    /**
     * 
     */
    public static class CccActionForm {
        /**
         * 
         */
        public void reset() {
        }

        /**
         * @return
         * 
         */
        public ActionMessages validate() {
            return null;
        }
    }
}