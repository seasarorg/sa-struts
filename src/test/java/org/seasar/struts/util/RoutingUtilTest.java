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
package org.seasar.struts.util;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.StringUtil;

/**
 * @author higa
 * 
 */
public class RoutingUtilTest extends S2TestCase {

    /**
     * @throws Exception
     */
    public void testGetActionPath() throws Exception {
        String[] names = StringUtil.split("/aaa", "/");
        assertEquals("/aaa", RoutingUtil.getActionPath(names, 0));
    }

    /**
     * @throws Exception
     */
    public void testGetActionPath_method() throws Exception {
        String[] names = StringUtil.split("/aaa/hoge", "/");
        assertEquals("/aaa", RoutingUtil.getActionPath(names, 0));
    }

    /**
     * @throws Exception
     */
    public void testGetParamPath() throws Exception {
        String[] names = StringUtil.split("/aaa", "/");
        assertEquals("", RoutingUtil.getParamPath(names, 1));
    }

    /**
     * @throws Exception
     */
    public void testGetParamPath_method() throws Exception {
        String[] names = StringUtil.split("/aaa/hoge", "/");
        assertEquals("hoge", RoutingUtil.getParamPath(names, 1));
    }

    /**
     * @throws Exception
     */
    public void testGetParamPath_index() throws Exception {
        String[] names = StringUtil.split("/higayasuo/edit", "/");
        assertEquals("higayasuo/edit", RoutingUtil.getParamPath(names, 0));
    }
}