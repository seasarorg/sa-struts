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

import junit.framework.TestCase;

import org.apache.struts.Globals;

/**
 * @author higa
 * 
 */
public class S2ModuleConfigTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testServletMapping() throws Exception {
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put(Globals.SERVLET_KEY, "*.do");
        S2ModuleConfig moduleConfig = new S2ModuleConfig(applicationScope);
        assertEquals("*.do", moduleConfig.servletMapping);
    }

    /**
     * @throws Exception
     */
    public void testFromPathToActionName_extension() throws Exception {
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put(Globals.SERVLET_KEY, "*.do");
        S2ModuleConfig moduleConfig = new S2ModuleConfig(applicationScope);
        assertEquals("aaa_bbbAction", moduleConfig
                .fromPathToActionName("/aaa/bbb.do"));
    }

    /**
     * @throws Exception
     */
    public void testFromPathToActionName_directory() throws Exception {
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put(Globals.SERVLET_KEY, "/do/*");
        S2ModuleConfig moduleConfig = new S2ModuleConfig(applicationScope);
        assertEquals("aaa_bbbAction", moduleConfig
                .fromPathToActionName("/do/aaa/bbb"));
    }

    /**
     * @throws Exception
     */
    public void testFromPathToActionName_slash() throws Exception {
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put(Globals.SERVLET_KEY, "/");
        S2ModuleConfig moduleConfig = new S2ModuleConfig(applicationScope);
        assertEquals("aaa_bbbAction", moduleConfig
                .fromPathToActionName("/aaa/bbb"));
    }

    /**
     * @throws Exception
     */
    public void testFromPathToActionName_slash_asta() throws Exception {
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put(Globals.SERVLET_KEY, "/*");
        S2ModuleConfig moduleConfig = new S2ModuleConfig(applicationScope);
        assertEquals("aaa_bbbAction", moduleConfig
                .fromPathToActionName("/aaa/bbb"));
    }
}