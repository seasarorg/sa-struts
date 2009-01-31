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
public class ServletContextUtilTest extends S2TestCase {

    /**
     * @throws Exception
     */
    public void testGetServletContext() throws Exception {
        assertNotNull(ServletContextUtil.getServletContext());
    }

    /**
     * @throws Exception
     */
    public void testGetViewPrefix() throws Exception {
        getServletContext().setInitParameter("sastruts.VIEW_PREFIX",
                "/WEB-INF/view");
        assertEquals("/WEB-INF/view", ServletContextUtil.getViewPrefix());
    }

    /**
     * @throws Exception
     */
    public void testGetViewPrefix_blank() throws Exception {
        getServletContext().setInitParameter("sastruts.VIEW_PREFIX", " ");
        assertNull(ServletContextUtil.getViewPrefix());
    }
}