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

import org.apache.struts.Globals;
import org.apache.struts.config.ForwardConfig;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.struts.annotation.Input;
import org.seasar.struts.annotation.Result;
import org.seasar.struts.annotation.Results;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ModuleConfig;

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
        register(CccAction.class, "aaa_cccAction");
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
    public void testCreateActionMapping_type() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        assertEquals(BbbAction.class.getName(), actionMapping.getType());
    }

    /**
     * @throws Exception
     */
    public void testCreateActionMapping_actionName() throws Exception {
        S2ActionMapping actionMapping = customizer
                .createActionMapping(getComponentDef("aaa_bbbAction"));
        assertEquals("aaa_bbbAction", actionMapping.getActionName());
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
     * 
     */
    @Input(path = "/aaa/input.jsp")
    @Result(path = "/aaa/bbb.jsp")
    public static class BbbAction {

    }

    /**
     * 
     */
    @Input(path = "/aaa/input.jsp")
    @Results( { @Result(name = "success", path = "/aaa/bbb.jsp"),
            @Result(name = "success2", path = "/aaa/bbb2.jsp") })
    public static class CccAction {

    }
}