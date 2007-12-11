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
package org.seasar.struts.customizer;

import java.util.List;

import org.apache.commons.beanutils.DynaClass;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.config.ForwardConfig;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.beans.MethodNotFoundRuntimeException;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.annotation.Input;
import org.seasar.struts.annotation.Result;
import org.seasar.struts.annotation.Results;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.config.S2FormBeanConfig;
import org.seasar.struts.config.S2ModuleConfig;
import org.seasar.struts.enums.SaveType;
import org.seasar.struts.exception.ExecuteMethodNotFoundRuntimeException;
import org.seasar.struts.exception.IllegalExecuteMethodRuntimeException;
import org.seasar.struts.exception.IllegalValidateMethodRuntimeException;
import org.seasar.struts.exception.InputNotDefinedRuntimeException;

/**
 * @author higa
 * 
 */
public class ActionCustomizerTest extends S2TestCase {

    private ActionCustomizer customizer = new ActionCustomizer();

    private S2ModuleConfig moduleConfig = new S2ModuleConfig("");

    @Override
    public void setUp() {
        getServletContext().setAttribute(Globals.SERVLET_KEY, "/*");
        getServletContext().setAttribute(Globals.MODULE_KEY, moduleConfig);
        register(BbbAction.class, "aaa_bbbAction");
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
    public void testSetupInput() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        assertEquals("input", actionMapping.getInput());
        ForwardConfig forwardConfig = actionMapping.findForwardConfig("input");
        assertEquals("/aaa/input.jsp", forwardConfig.getPath());
        assertFalse(forwardConfig.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testSetupResult() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        ForwardConfig forwardConfig = actionMapping
                .findForwardConfig("success");
        assertNotNull(forwardConfig);
        assertEquals("/aaa/bbb.jsp", forwardConfig.getPath());
        assertFalse(forwardConfig.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testSetupResult_results() throws Exception {
        register(CccAction.class, "aaa_cccAction");
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_cccAction"));
        ForwardConfig forwardConfig = actionMapping
                .findForwardConfig("success");
        assertNotNull(forwardConfig);
        assertEquals("/aaa/bbb.jsp", forwardConfig.getPath());
        assertFalse(forwardConfig.getRedirect());
        forwardConfig = actionMapping.findForwardConfig("success2");
        assertNotNull(forwardConfig);
        assertEquals("/aaa/bbb2.jsp", forwardConfig.getPath());
        assertFalse(forwardConfig.getRedirect());
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
        assertNotNull(executeConfig.getValidateMethod());
        assertEquals(SaveType.REQUEST, executeConfig.getSaveErrors());
        assertEquals(1, actionMapping.getExecuteConfigSize());
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
        } catch (InputNotDefinedRuntimeException e) {
            System.out.println(e);
            assertEquals(FffAction.class, e.getActionClass());
            assertEquals("validate", e.getValidateMethodName());
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
    public void testSetupActionForm() throws Exception {
        register(CccAction.class, "aaa_cccAction");
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_cccAction"));
        assertNotNull(actionMapping.getActionFormPropertyDesc());
    }

    /**
     * @throws Exception
     */
    public void testSetupReset_action() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        assertNotNull(actionMapping.getResetMethod());
    }

    /**
     * @throws Exception
     */
    public void testSetupReset_actionForm() throws Exception {
        register(CccAction.class, "aaa_cccAction");
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_cccAction"));
        assertNotNull(actionMapping.getResetMethod());
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
     * 
     */
    @Input(path = "/aaa/input.jsp")
    @Result(path = "/aaa/bbb.jsp")
    public static class BbbAction {

        /**
         * 
         */
        public String hoge;

        /**
         * 
         */
        public boolean hoge2;

        /**
         * 
         */
        public List<String> hoge3;

        /**
         * @return
         */
        @Execute(validator = false, validate = "validate")
        public String execute() {
            return "success";
        }

        /**
         * @return
         */
        public ActionMessages validate() {
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
    @Input(path = "/aaa/input.jsp")
    @Results( { @Result(name = "success", path = "/aaa/bbb.jsp"),
            @Result(name = "success2", path = "/aaa/bbb2.jsp") })
    public static class CccAction {
        /**
         * 
         */
        @ActionForm
        public CccActionForm cccActionForm;

        /**
         * @return
         */
        @Execute
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
    @Input(path = "/aaa/input.jsp")
    @Result(path = "/aaa/bbb.jsp")
    public static class HhhAction {
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
        public ActionMessages validate2() {
            return null;
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
    }
}