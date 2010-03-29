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
package org.seasar.struts.taglib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 * 
 */
public class S2FunctionsTest extends S2TestCase {

    /**
     * @throws Exception
     */
    public void testHForCharArray() throws Exception {
        assertEquals("[1]", S2Functions.h(new char[] { '1' }));
    }

    /**
     * @throws Exception
     */
    public void testHForByteArray() throws Exception {
        assertEquals("[1]", S2Functions.h(new byte[] { 1 }));
    }

    /**
     * @throws Exception
     */
    public void testHForShortArray() throws Exception {
        assertEquals("[1]", S2Functions.h(new short[] { 1 }));
    }

    /**
     * @throws Exception
     */
    public void testHForIntArray() throws Exception {
        assertEquals("[1]", S2Functions.h(new int[] { 1 }));
    }

    /**
     * @throws Exception
     */
    public void testHForFloatArray() throws Exception {
        assertEquals("[1.0]", S2Functions.h(new float[] { 1 }));
    }

    /**
     * @throws Exception
     */
    public void testHForDoubleArray() throws Exception {
        assertEquals("[1.0]", S2Functions.h(new double[] { 1 }));
    }

    /**
     * @throws Exception
     */
    public void testHForBooleanArray() throws Exception {
        assertEquals("[true]", S2Functions.h(new boolean[] { true }));
    }

    /**
     * @throws Exception
     */
    public void testHForStringArray() throws Exception {
        assertEquals("[1]", S2Functions.h(new String[] { "1" }));
    }

    /**
     * @throws Exception
     */
    public void testHForObjectArray() throws Exception {
        assertEquals("[1]", S2Functions.h(new Integer[] { Integer.valueOf(1) }));
    }

    /**
     * @throws Exception
     */
    public void testDate() throws Exception {
        assertNotNull(S2Functions.date("20080131", "yyyyMMdd"));
    }

    /**
     * @throws Exception
     */
    public void testDate_valueIsNull() throws Exception {
        assertNull(S2Functions.date(null, "yyyyMMdd"));
    }

    /**
     * @throws Exception
     */
    public void testDate_patternIsNull() throws Exception {
        try {
            S2Functions.date("20080131", null);
            fail();
        } catch (NullPointerException e) {
            System.out.println(e);
        }
    }

    /**
     * @throws Exception
     */
    public void testNumber() throws Exception {
        assertEquals("1000", S2Functions.number("1000", "####").toString());
    }

    /**
     * @throws Exception
     */
    public void testNumber_valueIsNull() throws Exception {
        assertNull(S2Functions.number(null, "####"));
    }

    /**
     * @throws Exception
     */
    public void testNumber_patternIsNull() throws Exception {
        try {
            S2Functions.number("1000", null);
            fail();
        } catch (NullPointerException e) {
            System.out.println(e);
        }
    }

    /**
     * @throws Exception
     */
    public void testBrForCRLF() throws Exception {
        assertEquals("<br />", S2Functions.br("\r\n"));
    }

    /**
     * @throws Exception
     */
    public void testBrForCR() throws Exception {
        assertEquals("<br />", S2Functions.br("\r"));
    }

    /**
     * @throws Exception
     */
    public void testBrForLF() throws Exception {
        assertEquals("<br />", S2Functions.br("\n"));
    }

    /**
     * @throws Exception
     */
    public void testBrForNull() throws Exception {
        assertEquals("", S2Functions.br(null));
    }

    /**
     * @throws Exception
     */
    public void testNbsp() throws Exception {
        assertEquals("&nbsp;&nbsp;", S2Functions.nbsp("  "));
    }

    /**
     * @throws Exception
     */
    public void testNbspForNull() throws Exception {
        assertEquals("", S2Functions.nbsp(null));
    }

    /**
     * @throws Exception
     */
    public void testUrlForNull() throws Exception {
        getServletContext().setServletContextName("/context");
        getRequest().setPathInfo("/add/index.jsp");
        assertEquals("/context/add/", S2Functions.url(null));
    }

    /**
     * @throws Exception
     */
    public void testUrlForNullAndContextNameNull() throws Exception {
        getRequest().setPathInfo("/add/index.jsp");
        assertEquals("/add/", S2Functions.url(null));
    }

    /**
     * @throws Exception
     */
    public void testUrlForAction() throws Exception {
        register(String.class, "foreachAction");
        getRequest().setPathInfo("/add/index.jsp");
        assertEquals("/foreach/", S2Functions.url("/foreach"));
    }

    /**
     * @throws Exception
     */
    public void testUrlForActionAndParameter() throws Exception {
        register(String.class, "foreachAction");
        getRequest().setPathInfo("/add/index.jsp");
        assertEquals("/add/submit", S2Functions.url("/add/submit"));
    }

    /**
     * @throws Exception
     */
    public void testUrlForParameter() throws Exception {
        getRequest().setPathInfo("/add/index.jsp");
        assertEquals("/add/edit/1", S2Functions.url("edit/1"));
    }

    /**
     * @throws Exception
     */
    public void testLabelUsingMap() throws Exception {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("value", 1);
        m.put("label", "one");
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        dataList.add(m);
        Map<String, Object> m2 = new HashMap<String, Object>();
        m2.put("value", 2);
        m2.put("label", "two");
        dataList.add(m2);
        assertEquals("two", S2Functions.label(2, dataList, "value", "label"));
        assertEquals("", S2Functions.label(0, dataList, "value", "label"));
    }

    /**
     * @throws Exception
     */
    public void testLabelUsingMap_null_null() throws Exception {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("value", null);
        m.put("label", "one");
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        dataList.add(m);
        assertEquals("one", S2Functions.label(null, dataList, "value", "label"));
    }

    /**
     * @throws Exception
     */
    public void testLabelUsingMap_empty_null() throws Exception {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("value", null);
        m.put("label", "one");
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        dataList.add(m);
        assertEquals("one", S2Functions.label("", dataList, "value", "label"));
    }

    /**
     * @throws Exception
     */
    public void testLabelUsingMap_empty_empty() throws Exception {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("value", "");
        m.put("label", "one");
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        dataList.add(m);
        assertEquals("one", S2Functions.label("", dataList, "value", "label"));
    }

    /**
     * @throws Exception
     */
    public void testLabelUsingMap_null_empty() throws Exception {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("value", "");
        m.put("label", "one");
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        dataList.add(m);
        assertEquals("one", S2Functions.label(null, dataList, "value", "label"));
    }

    /**
     * @throws Exception
     */
    public void testLabelUsingMap_string_integer() throws Exception {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("value", 1);
        m.put("label", "one");
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        dataList.add(m);
        assertEquals("one", S2Functions.label("1", dataList, "value", "label"));
    }

    /**
     * @throws Exception
     */
    public void testLabelUsingJavaBeans() throws Exception {
        Foo foo = new Foo();
        foo.id = 1;
        foo.name = "one";
        List<Foo> dataList = new ArrayList<Foo>();
        dataList.add(foo);
        assertEquals("one", S2Functions.label(1, dataList, "id", "name"));
        assertEquals("", S2Functions.label(2, dataList, "id", "name"));
    }

    /**
     * 
     */
    private static class Foo {
        /**
         * 
         */
        @SuppressWarnings("unused")
        public Integer id;

        /**
         * 
         */
        @SuppressWarnings("unused")
        public String name;
    }
}