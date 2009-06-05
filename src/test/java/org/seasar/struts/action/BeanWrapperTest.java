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
package org.seasar.struts.action;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.TestCase;

/**
 * @author higa
 * 
 */
public class BeanWrapperTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testGet() throws Exception {
        MyBean bean = new MyBean();
        BeanWrapper wrapper = new BeanWrapper(bean);
        bean.aaa = "111";
        assertEquals("111", wrapper.get("aaa"));
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testGet_nest() throws Exception {
        MyBean bean = new MyBean();
        MyBean bean2 = new MyBean();
        bean2.aaa = "hoge";
        bean.bbb = bean2;
        BeanWrapper wrapper = new BeanWrapper(bean);
        Map bbb = (Map) wrapper.get("bbb");
        assertNotNull(bbb);
        assertEquals("hoge", bbb.get("aaa"));
    }

    /**
     * @throws Exception
     */
    public void testGet_writeOnly() throws Exception {
        MyBean2 bean2 = new MyBean2();
        bean2.aaa = "1";
        BeanWrapper wrapper = new BeanWrapper(bean2);
        assertNull(wrapper.get("aaa"));
    }

    /**
     * @throws Exception
     */
    public void testContainsKey() throws Exception {
        MyBean bean = new MyBean();
        BeanWrapper wrapper = new BeanWrapper(bean);
        assertTrue(wrapper.containsKey("aaa"));
        assertFalse(wrapper.containsKey("xxx"));
        assertFalse(wrapper.containsKey(null));
    }

    /**
     * @throws Exception
     */
    public void testSize() throws Exception {
        MyBean bean = new MyBean();
        BeanWrapper wrapper = new BeanWrapper(bean);
        assertEquals(2, wrapper.size());
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testKeySet() throws Exception {
        MyBean bean = new MyBean();
        BeanWrapper wrapper = new BeanWrapper(bean);
        Set keySet = wrapper.keySet();
        assertEquals(2, keySet.size());
        Iterator i = keySet.iterator();
        assertEquals("aaa", i.next());
        assertEquals("bbb", i.next());
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testEntrySet() throws Exception {
        MyBean bean = new MyBean();
        bean.aaa = "111";
        BeanWrapper wrapper = new BeanWrapper(bean);
        Set<Entry> set = wrapper.entrySet();
        assertEquals(2, set.size());
        for (Iterator<Entry> i = set.iterator(); i.hasNext();) {
            Entry<String, Object> e = i.next();
            if (e.getKey().equals("aaa")) {
                assertEquals("111", e.getValue());
            }
        }
    }

    /**
     * @throws Exception
     */
    public void testToString() throws Exception {
        MyBean bean = new MyBean();
        MyBean bean2 = new MyBean();
        bean2.aaa = "hoge";
        bean.bbb = bean2;
        bean2.bbb = bean;
        BeanWrapper wrapper = new BeanWrapper(bean);
        assertEquals("MyBean", wrapper.toString());
    }

    /**
     * 
     */
    public static class MyBean {

        /**
         * 
         */
        public String aaa;

        /**
         * 
         */
        public MyBean bbb;

        @Override
        public String toString() {
            return "MyBean";
        }
    }

    /**
     * 
     */
    public static class MyBean2 {

        /**
         * 
         */
        @SuppressWarnings("unused")
        private String aaa;

        /**
         * @param aaa
         * @return
         */
        public void setAaa(String aaa) {
            this.aaa = aaa;
        }
    }
}