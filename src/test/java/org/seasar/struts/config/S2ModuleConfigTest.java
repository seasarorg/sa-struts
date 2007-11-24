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
package org.seasar.struts.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionMapping;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 * 
 */
public class S2ModuleConfigTest extends S2TestCase {

    public void setUp() {
        register(BbbAction.class, "aaa_bbbAction");
    }

    /**
     * @throws Exception
     */
    public void testServletMapping() throws Exception {
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put(Globals.SERVLET_KEY, "*.do");
        S2ModuleConfig moduleConfig = new S2ModuleConfig("", applicationScope);
        assertEquals("*.do", moduleConfig.servletMapping);
    }

    /**
     * @throws Exception
     */
    public void testFromPathToActionName_extension() throws Exception {
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put(Globals.SERVLET_KEY, "*.do");
        S2ModuleConfig moduleConfig = new S2ModuleConfig("", applicationScope);
        assertEquals("aaa_bbbAction", moduleConfig
                .fromPathToActionName("/aaa/bbb.do"));
    }

    /**
     * @throws Exception
     */
    public void testFromPathToActionName_directory() throws Exception {
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put(Globals.SERVLET_KEY, "/do/*");
        S2ModuleConfig moduleConfig = new S2ModuleConfig("", applicationScope);
        assertEquals("aaa_bbbAction", moduleConfig
                .fromPathToActionName("/do/aaa/bbb"));
    }

    /**
     * @throws Exception
     */
    public void testFromPathToActionName_slash() throws Exception {
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put(Globals.SERVLET_KEY, "/");
        S2ModuleConfig moduleConfig = new S2ModuleConfig("", applicationScope);
        assertEquals("aaa_bbbAction", moduleConfig
                .fromPathToActionName("/aaa/bbb"));
    }

    /**
     * @throws Exception
     */
    public void testFromPathToActionName_slash_asterisk() throws Exception {
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put(Globals.SERVLET_KEY, "/*");
        S2ModuleConfig moduleConfig = new S2ModuleConfig("", applicationScope);
        assertEquals("aaa_bbbAction", moduleConfig
                .fromPathToActionName("/aaa/bbb"));
    }

    /**
     * @throws Exception
     */
    public void testCreateActionMapping_path() throws Exception {
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put(Globals.SERVLET_KEY, "/*");
        S2ModuleConfig moduleConfig = new S2ModuleConfig("", applicationScope);
        ActionMapping actionMapping = moduleConfig
                .createActionMapping("/aaa/bbb");
        assertNotNull(actionMapping);
        assertEquals("/aaa/bbb", actionMapping.getPath());
    }

    /**
     * @throws Exception
     */
    public void testCreateActionMapping_moduleConfig() throws Exception {
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put(Globals.SERVLET_KEY, "/*");
        S2ModuleConfig moduleConfig = new S2ModuleConfig("", applicationScope);
        ActionMapping actionMapping = moduleConfig
                .createActionMapping("/aaa/bbb");
        assertSame(moduleConfig, actionMapping.getModuleConfig());
    }

    /**
     * @throws Exception
     */
    public void testCreateActionMapping_type() throws Exception {
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put(Globals.SERVLET_KEY, "/*");
        S2ModuleConfig moduleConfig = new S2ModuleConfig("", applicationScope);
        ActionMapping actionMapping = moduleConfig
                .createActionMapping("/aaa/bbb");
        assertEquals(BbbAction.class.getName(), actionMapping.getType());
    }

    /**
     * @throws Exception
     */
    public void testCreateActionMapping_actionName() throws Exception {
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put(Globals.SERVLET_KEY, "/*");
        S2ModuleConfig moduleConfig = new S2ModuleConfig("", applicationScope);
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .createActionMapping("/aaa/bbb");
        assertEquals("aaa_bbbAction", actionMapping.getActionName());
    }

    /**
     * 
     */
    public static class BbbAction {

    }
}