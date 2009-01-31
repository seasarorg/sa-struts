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
package org.seasar.struts.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.struts.annotation.Execute;

/**
 * @author higa
 * 
 */
public class S2ExecuteConfigTest extends S2TestCase {

    /**
     * @return
     */
    public String index() {
        return null;
    }

    /**
     * @throws Exception
     */
    public void testUrlPattern_empty() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(getClass().getMethod("getClass"));
        assertEquals("getClass", executeConfig.urlPattern);
    }

    /**
     * @throws Exception
     */
    public void testUrlPattern() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(getClass().getMethod("getClass"));
        executeConfig.setUrlPattern("edit/{id}");
        assertEquals(1, executeConfig.urlParamNames.size());
        assertEquals("id", executeConfig.urlParamNames.get(0));
        assertEquals("^edit/([^/]+)$", executeConfig.urlPatternRegexp.pattern());
        Matcher matcher = executeConfig.urlPatternRegexp.matcher("edit/11");
        assertTrue(matcher.find());
        assertEquals("11", matcher.group(1));
    }

    /**
     * @throws Exception
     */
    public void testUrlPatternAllSelected() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(getClass().getMethod("getClass"));
        executeConfig.setUrlPattern("{id}");
        assertTrue(executeConfig.isUrlPatternAllSelected());
    }

    /**
     * @throws Exception
     */
    public void testUrlPatternAllSelected_false() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(getClass().getMethod("getClass"));
        executeConfig.setUrlPattern("{id}/edit");
        assertFalse(executeConfig.isUrlPatternAllSelected());
    }

    /**
     * @throws Exception
     */
    public void testIsTarget_paramPath() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(getClass().getMethod("getClass"));
        executeConfig.setUrlPattern("edit/{id}");
        assertTrue(executeConfig.isTarget("edit/11"));
        assertFalse(executeConfig.isTarget("edit2/11"));
        assertFalse(executeConfig.isTarget(""));
    }

    /**
     * @throws Exception
     */
    public void testIsTarget_paramPath_index() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(getClass().getMethod("index"));
        assertTrue(executeConfig.isTarget(""));
        assertFalse(executeConfig.isTarget("edit2/11"));
    }

    /**
     * @throws Exception
     */
    public void testIsTarget_request() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(getClass().getMethod("getClass"));
        executeConfig.setUrlPattern("");
        getRequest().setParameter("getClass", "hoge");
        assertTrue(executeConfig.isTarget(getRequest()));
    }

    /**
     * @throws Exception
     */
    public void testIsTarget_request_ismap() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(getClass().getMethod("getClass"));
        getRequest().setParameter("getClass.x", "123");
        assertTrue(executeConfig.isTarget(getRequest()));
    }

    /**
     * @throws Exception
     */
    public void testIsTarget_request_methodName() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(getClass().getMethod("getClass"));
        getRequest().setParameter("SAStruts.method", "getClass");
        assertTrue(executeConfig.isTarget(getRequest()));
    }

    /**
     * @throws Exception
     */
    public void testGetQueryString() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(getClass().getMethod("getClass"));
        executeConfig.setUrlPattern("edit/{id}");
        assertEquals("?id=11&SAStruts.method=getClass", executeConfig
                .getQueryString("edit/11"));
    }

    /**
     * @throws Exception
     */
    public void testGetParams_multi() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(getClass().getMethod("getClass"));
        executeConfig.setUrlPattern("edit/{id}/{id2}");
        assertEquals("?id=11&id2=22&SAStruts.method=getClass", executeConfig
                .getQueryString("edit/11/22"));
    }

    /**
     * @throws Exception
     */
    public void testGetParams_empty() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(getClass().getMethod("getClass"));
        assertEquals("?SAStruts.method=getClass", executeConfig
                .getQueryString(""));
    }

    /**
     * @throws Exception
     */
    public void testInputPattern() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setInput("edit/{id}");
        assertEquals(1, executeConfig.inputParamNames.size());
        assertEquals("id", executeConfig.inputParamNames.get(0));
    }

    /**
     * @throws Exception
     */
    public void testResolveInut() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setInput("edit/{id}");
        register(AaaAction.class, "aaaAction");
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("aaaAction"));
        AaaAction action = (AaaAction) getComponent("aaaAction");
        action.id = "111";
        assertEquals("edit/111", executeConfig.resolveInput(actionMapping));
    }

    /**
     * @throws Exception
     */
    public void testResolveInut_null() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setInput("edit/{id}");
        register(AaaAction.class, "aaaAction");
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("aaaAction"));
        assertEquals("edit/null", executeConfig.resolveInput(actionMapping));
    }

    /**
     * @throws Exception
     */
    public void testIsValidator() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        assertFalse(executeConfig.isValidator());
        List<S2ValidationConfig> validationConfigs = new ArrayList<S2ValidationConfig>();
        executeConfig.setValidationConfigs(validationConfigs);
        assertFalse(executeConfig.isValidator());
        validationConfigs.add(new S2ValidationConfig(getClass().getMethod(
                "getClass")));
        assertFalse(executeConfig.isValidator());
        validationConfigs.add(new S2ValidationConfig());
        assertTrue(executeConfig.isValidator());
    }

    /**
     * 
     */
    public static class AaaAction {
        /**
         * 
         */
        public String id;

        /**
         * @return
         */
        @Execute(validator = false)
        public String index() {
            return "index.jsp";
        }
    }
}