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

/**
 * @author higa
 * 
 */
public class ActionUtilTest extends S2TestCase {

    /**
     * @throws Exception
     */
    public void testFromPathToActionName() throws Exception {
        assertEquals("aaa_bbbAction", ActionUtil
                .fromPathToActionName("/aaa/bbb"));
    }

    /**
     * @throws Exception
     */
    public void testFromActionNameToPath() throws Exception {
        assertEquals("/aaa/bbb", ActionUtil
                .fromActionNameToPath("aaa_bbbAction"));
        assertEquals("/aaa", ActionUtil.fromActionNameToPath("aaaAction"));
        assertEquals("/index", ActionUtil.fromActionNameToPath("indexAction"));
    }

    /**
     * @throws Exception
     */
    public void testCalcActionPath() throws Exception {
        getRequest().setPathInfo("/aaa/index.jsp");
        assertEquals("/aaa/", ActionUtil.calcActionPath());
        getRequest().setPathInfo("/aaa/");
        assertEquals("/aaa/", ActionUtil.calcActionPath());
    }
}