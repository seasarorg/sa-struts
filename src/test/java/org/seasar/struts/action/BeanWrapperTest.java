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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;

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
    public void testGet_indexedProperty() throws Exception {
        MyBean bean = new MyBean();
        bean.list.add("aaa");
        BeanWrapper wrapper = new BeanWrapper(bean);
        assertEquals("aaa", wrapper.get("list[0]"));
    }

    /**
     * @throws Exception
     */
    public void testGet_indexedProperty_inBean() throws Exception {
        MyBean2 bean = new MyBean2();
        MyBean subBean = new MyBean();
        subBean.aaa = "aaa";
        bean.beanList.add(subBean );
        BeanWrapper wrapper = new BeanWrapper(bean);
        String aaa = BeanUtils.getProperty(wrapper, "beanList[0].aaa");
        assertEquals("aaa", aaa);
    }

    /**
     * @throws Exception
     */
    public void testGet_indexedProperty_List2() throws Exception {
        MyBean2 bean = new MyBean2();
        List<String> list = new ArrayList<String>();
        list.add("aaa");
        bean.list2.add(list);
        BeanWrapper wrapper = new BeanWrapper(bean);
        String aaa = BeanUtils.getProperty(wrapper, "list2[0][0]");
        assertEquals("aaa", aaa);
    }

    /**
     * @throws Exception
     */
    public void testGet_indexedProperty_Array() throws Exception {
        MyBean2 bean = new MyBean2();
        bean.array[0] = "aaa";
        BeanWrapper wrapper = new BeanWrapper(bean);
        assertEquals("aaa", wrapper.get("array[0]"));
    }
    /**
     * @throws Exception
     */
    public void testGet_indexedProperty_Array2() throws Exception {
        MyBean2 bean = new MyBean2();
        bean.array2[0][1] = "aaa";
        BeanWrapper wrapper = new BeanWrapper(bean);
        assertEquals("aaa", wrapper.get("array2[0][1]"));
    }
    /**
     * @throws Exception
     */
    public void testGet_indexedProperty_Array2Null() throws Exception {
        MyBean2 bean = new MyBean2();
        bean.array2[0][1] = null;
        BeanWrapper wrapper = new BeanWrapper(bean);
        assertNull(wrapper.get("array2[0][1]"));
    }
    /**
     * @throws Exception
     */
    public void testGet_indexedProperty_Array3() throws Exception {
        MyBean2 bean = new MyBean2();
        bean.array3[1][1][1] = "aaa";
        BeanWrapper wrapper = new BeanWrapper(bean);
        assertEquals("aaa", wrapper.get("array3[1][1][1]"));
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
        assertEquals(3, wrapper.size());
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testKeySet() throws Exception {
        MyBean bean = new MyBean();
        BeanWrapper wrapper = new BeanWrapper(bean);
        Set keySet = wrapper.keySet();
        assertEquals(3, keySet.size());
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
        assertEquals(3, set.size());
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

        /**
         * 
         */
        public List<String> list = new ArrayList<String>();
        
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
         * 
         */
        public String[] array = new String[10];
        /**
         * 
         */
        public String[][] array2 = new String[10][10];
        /**
         * 
         */
        public String[][][] array3 = new String[5][5][5];

        /**
         * 
         */
        public List<List<String>> list2 = new ArrayList<List<String>>();

        /**
         * 
         */
        public List<MyBean> beanList = new ArrayList<MyBean>();

        /**
         * @param aaa
         * @return
         */
        public void setAaa(String aaa) {
            this.aaa = aaa;
        }
    }
}