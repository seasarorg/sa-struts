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

import org.apache.commons.beanutils.DynaProperty;
import org.apache.struts.Globals;
import org.apache.struts.config.ForwardConfig;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.annotation.Input;
import org.seasar.struts.annotation.Result;
import org.seasar.struts.annotation.Results;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.config.S2ModuleConfig;
import org.seasar.struts.exception.FieldNotFoundRuntimeException;
import org.seasar.struts.exception.GenericsNotSpecifiedRuntimeException;
import org.seasar.struts.exception.IllegalExecuteMethodRuntimeException;

/**
 * @author higa
 * 
 */
public class ActionCustomizerTest extends S2TestCase {

    private ActionCustomizer customizer = new ActionCustomizer();

    private S2ModuleConfig moduleConfig = new S2ModuleConfig("");

    public void setUp() {
        getServletContext().setAttribute(Globals.SERVLET_KEY, "/*");
        getServletContext().setAttribute(Globals.MODULE_KEY, moduleConfig);
        register(BbbAction.class, "aaa_bbbAction");
    }

    /**
     * @throws Exception
     */
    public void testCustomize() throws Exception {
        customizer.customize(getComponentDef("aaa_bbbAction"));
        assertNotNull(moduleConfig.findActionConfig("/aaa/bbb"));
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
    public void testCreateActionMapping_scope() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        assertEquals("request", actionMapping.getScope());
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
    }

    /**
     * @throws Exception
     */
    public void testSetupMethod_illegalExecuteMethod() throws Exception {
        register(DddAction.class, "aaa_dddAction");
        try {
            customizer.createActionMapping(getComponentDef("aaa_dddAction"));
        } catch (IllegalExecuteMethodRuntimeException e) {
            System.out.println(e);
            assertEquals(DddAction.class, e.getActionClass());
            assertEquals(DddAction.class.getMethod("execute"), e
                    .getExecuteMethod());
        }
    }

    /**
     * @throws Exception
     */
    public void testSetupDynaProperty_string() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        DynaProperty property = actionMapping.getDynaProperty("hoge");
        assertNotNull(property);
        assertEquals(String.class, property.getType());
    }

    /**
     * @throws Exception
     */
    public void testSetupDynaProperty_boolean() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        DynaProperty property = actionMapping.getDynaProperty("hoge2");
        assertNotNull(property);
        assertEquals(boolean.class, property.getType());
    }

    /**
     * @throws Exception
     */
    public void testSetupDynaProperty_list() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        DynaProperty property = actionMapping.getDynaProperty("hoge3");
        assertNotNull(property);
        assertEquals(List.class, property.getType());
        assertEquals(String.class, property.getContentType());
    }

    /**
     * @throws Exception
     */
    public void testSetupDynaProperty_list_notGenerics() throws Exception {
        register(EeeAction.class, "aaa_eeeAction");
        try {
            customizer.createActionMapping(getComponentDef("aaa_eeeAction"));
        } catch (GenericsNotSpecifiedRuntimeException e) {
            System.out.println(e);
            assertEquals(EeeAction.class, e.getTargetClass());
            assertEquals("hoge", e.getPropertyName());
        }
    }

    /**
     * @throws Exception
     */
    public void testSetupDynaProperty_list_notField() throws Exception {
        register(FffAction.class, "aaa_fffAction");
        try {
            customizer.createActionMapping(getComponentDef("aaa_fffAction"));
            fail();
        } catch (FieldNotFoundRuntimeException e) {
            System.out.println(e);
            assertEquals(FffAction.class, e.getTargetClass());
            assertEquals("hoge", e.getPropertyName());
        }
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
        @Execute(validator = false)
        public String execute() {
            return "success";
        }
    }

    /**
     * 
     */
    @Input(path = "/aaa/input.jsp")
    @Results( { @Result(name = "success", path = "/aaa/bbb.jsp"),
            @Result(name = "success2", path = "/aaa/bbb2.jsp") })
    public static class CccAction {

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
         * 
         */
        @SuppressWarnings("unchecked")
        public List hoge;

        private String _hoge2;

        /**
         * @return
         */
        public String getHoge2() {
            return _hoge2;
        }
    }

    /**
     * 
     */
    public static class FffAction {

        private List<String> _hoge;

        /**
         * @return
         */
        public List<String> getHoge() {
            return _hoge;
        }
    }
}