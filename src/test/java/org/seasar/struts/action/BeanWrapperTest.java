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
package org.seasar.struts.action;

import java.util.Map;

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
    @SuppressWarnings("unchecked")
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
}