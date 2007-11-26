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
package org.seasar.struts.util;

import org.apache.struts.Globals;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 * 
 */
public class ActionUtilTest extends S2TestCase {

    public void setUp() {
    }

    /**
     * @throws Exception
     */
    public void testFromPathToActionName_extension() throws Exception {
        getServletContext().setAttribute(Globals.SERVLET_KEY, "*.do");
        assertEquals("aaa_bbbAction", ActionUtil
                .fromPathToActionName("/aaa/bbb.do"));
    }

    /**
     * @throws Exception
     */
    public void testFromPathToActionName_directory() throws Exception {
        getServletContext().setAttribute(Globals.SERVLET_KEY, "/do/*");
        assertEquals("aaa_bbbAction", ActionUtil
                .fromPathToActionName("/do/aaa/bbb"));
    }

    /**
     * @throws Exception
     */
    public void testFromPathToActionName_slash() throws Exception {
        getServletContext().setAttribute(Globals.SERVLET_KEY, "/");
        assertEquals("aaa_bbbAction", ActionUtil
                .fromPathToActionName("/aaa/bbb"));
    }

    /**
     * @throws Exception
     */
    public void testFromPathToActionName_slash_asterisk() throws Exception {
        getServletContext().setAttribute(Globals.SERVLET_KEY, "/*");
        assertEquals("aaa_bbbAction", ActionUtil
                .fromPathToActionName("/aaa/bbb"));
    }

    /**
     * @throws Exception
     */
    public void testFromActionNameToPath_extension() throws Exception {
        getServletContext().setAttribute(Globals.SERVLET_KEY, "*.do");
        assertEquals("/aaa/bbb.do", ActionUtil
                .fromActionNameToPath("aaa_bbbAction"));
    }

    /**
     * @throws Exception
     */
    public void testFromActionNameToPath_directory() throws Exception {
        getServletContext().setAttribute(Globals.SERVLET_KEY, "/do/*");
        assertEquals("/do/aaa/bbb", ActionUtil
                .fromActionNameToPath("aaa_bbbAction"));
    }

    /**
     * @throws Exception
     */
    public void testFromActionNameToPath_slash() throws Exception {
        getServletContext().setAttribute(Globals.SERVLET_KEY, "/");
        assertEquals("/aaa/bbb", ActionUtil
                .fromActionNameToPath("aaa_bbbAction"));
    }

    /**
     * @throws Exception
     */
    public void testFromActionNameToPath_slash_asterisk() throws Exception {
        getServletContext().setAttribute(Globals.SERVLET_KEY, "/*");
        assertEquals("/aaa/bbb", ActionUtil
                .fromActionNameToPath("aaa_bbbAction"));
    }
}