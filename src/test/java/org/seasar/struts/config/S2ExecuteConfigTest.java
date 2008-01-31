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

import java.util.regex.Matcher;

import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 * 
 */
public class S2ExecuteConfigTest extends S2TestCase {

    /**
     * @return
     */
    public String index() {
        return "index.jsp";
    }

    /**
     * @throws Exception
     */
    public void testUrlPattern_empty() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(getClass()
                .getDeclaredMethod("testUrlPattern_empty"), true, null, null,
                null, null);
        assertEquals("testUrlPattern_empty", executeConfig.urlPattern);
    }

    /**
     * @throws Exception
     */
    public void testUrlPattern_urlParamNames() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(getClass()
                .getDeclaredMethod("testUrlPattern_empty"), true, null, null,
                null, "edit/{id}");
        assertEquals(1, executeConfig.urlParamNames.size());
        assertEquals("id", executeConfig.urlParamNames.get(0));
    }

    /**
     * @throws Exception
     */
    public void testUrlPattern_regexp() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(getClass()
                .getDeclaredMethod("testUrlPattern_empty"), true, null, null,
                null, "edit/{id}");
        assertEquals("^edit/([^/]+)$", executeConfig.urlPatternRegexp.pattern());
        Matcher matcher = executeConfig.urlPatternRegexp.matcher("edit/11");
        assertTrue(matcher.find());
        assertEquals("11", matcher.group(1));
    }

    /**
     * @throws Exception
     */
    public void testIsTarget_paramPath() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(getClass()
                .getDeclaredMethod("testUrlPattern_empty"), true, null, null,
                null, "edit/{id}");
        assertTrue(executeConfig.isTarget("edit/11"));
        assertFalse(executeConfig.isTarget("edit2/11"));
        assertFalse(executeConfig.isTarget(""));
    }

    /**
     * @throws Exception
     */
    public void testIsTarget_request() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(getClass()
                .getDeclaredMethod("testUrlPattern_empty"), true, null, null,
                null, "");
        getRequest().setParameter("testUrlPattern_empty", "hoge");
        assertTrue(executeConfig.isTarget(getRequest()));
    }

    /**
     * @throws Exception
     */
    public void testIsTarget_request_ismap() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(getClass()
                .getDeclaredMethod("testUrlPattern_empty"), true, null, null,
                null, "");
        getRequest().setParameter("testUrlPattern_empty.x", "123");
        assertTrue(executeConfig.isTarget(getRequest()));
    }

    /**
     * @throws Exception
     */
    public void testIsTarget_request_methodName() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(getClass()
                .getDeclaredMethod("testUrlPattern_empty"), true, null, null,
                null, "");
        getRequest().setParameter("SAStruts.method", "testUrlPattern_empty");
        assertTrue(executeConfig.isTarget(getRequest()));
    }

    /**
     * @throws Exception
     */
    public void testGetQueryString() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(getClass()
                .getMethod("getClass"), true, null, null, null, "edit/{id}");
        assertEquals("?id=11&SAStruts.method=getClass", executeConfig
                .getQueryString("edit/11"));
    }

    /**
     * @throws Exception
     */
    public void testGetParams_multi() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(getClass()
                .getMethod("getClass"), true, null, null, null,
                "edit/{id}/{id2}");
        assertEquals("?id=11&id2=22&SAStruts.method=getClass", executeConfig
                .getQueryString("edit/11/22"));
    }

    /**
     * @throws Exception
     */
    public void testGetParams_empty() throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(getClass()
                .getDeclaredMethod("index"), true, null, null, null, "index");
        assertEquals("?SAStruts.method=index", executeConfig.getQueryString(""));
    }
}