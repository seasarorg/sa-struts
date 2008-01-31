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
package org.seasar.struts.taglib;

import junit.framework.TestCase;

/**
 * @author higa
 * 
 */
public class S2FunctionsTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testDate() throws Exception {
        assertEquals("2008/01/31", S2Functions.date("20080131", "yyyyMMdd",
                "yyyy/MM/dd"));
    }

    /**
     * @throws Exception
     */
    public void testDate_null() throws Exception {
        assertEquals("", S2Functions.date(null, "yyyyMMdd", "yyyy/MM/dd"));
    }

    /**
     * @throws Exception
     */
    public void testNumber() throws Exception {
        assertEquals("1,000", S2Functions.number("1000", "####", "#,###"));
    }

    /**
     * @throws Exception
     */
    public void testNumber_null() throws Exception {
        assertEquals("", S2Functions.number(null, "####", "#,###"));
    }
}