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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.impl.ComponentDefImpl;
import org.seasar.framework.util.tiger.Maps;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.config.S2ActionMapping;

/**
 * @author higa
 * 
 */
public class WrapperUtilTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testConvert_null() throws Exception {
        assertNull(WrapperUtil.convert(null));
    }

    /**
     * @throws Exception
     */
    public void testConvert_simpleType() throws Exception {
        assertEquals("1", WrapperUtil.convert("1"));
    }

    /**
     * @throws Exception
     */
    public void testConvert_array_string() throws Exception {
        List<String> l = WrapperUtil.convert(new String[] { "1" });
        assertEquals(ArrayWrapper.class, l.getClass());
        assertEquals("1", l.get(0));
    }

    /**
     * @throws Exception
     */
    public void testConvert_array_primitive() throws Exception {
        List<Integer> l = WrapperUtil.convert(new int[] { 1 });
        assertEquals(ArrayWrapper.class, l.getClass());
        assertEquals(Integer.valueOf(1), l.get(0));
    }

    /**
     * @throws Exception
     */
    public void testConvert_actionFormWrapper() throws Exception {
        ComponentDef cd = new ComponentDefImpl(AaaAction.class);
        S2ActionMapping mapping = new S2ActionMapping();
        mapping.setComponentDef(cd);
        ActionFormWrapperClass dynaClass = new ActionFormWrapperClass(mapping);
        ActionFormWrapper form = new ActionFormWrapper(dynaClass);
        assertEquals(form, WrapperUtil.convert(form));
    }

    /**
     * @throws Exception
     */
    public void testConvert_list() throws Exception {
        List<String> c = WrapperUtil.convert(Arrays.asList("1"));
        assertEquals(ListWrapper.class, c.getClass());
        assertEquals("1", c.iterator().next());
    }

    /**
     * @throws Exception
     */
    public void testConvert_map() throws Exception {
        Map<String, String> map = WrapperUtil.convert(Maps.map("aaa", "111")
                .$());
        assertEquals("111", map.get("aaa"));
    }

    /**
     * @throws Exception
     */
    public void testConvert_enum() throws Exception {
        assertEquals(MyEnum.ONE, WrapperUtil.convert(MyEnum.ONE));
    }

    /**
     * @throws Exception
     */
    public void testConvert_bean() throws Exception {
        MyBean bean = new MyBean();
        bean.aaa = "111";
        Map<String, Object> map = WrapperUtil.convert(bean);
        assertEquals("111", map.get("aaa"));
    }

    private static class MyBean {
        /**
         * 
         */
        @SuppressWarnings("unused")
        public String aaa;
    }

    private static enum MyEnum {
        /**
         * 
         */
        ONE {
            @Override
            public String hoge() {
                return name();
            }
        },
        /**
         * 
         */
        TWO {
            @Override
            public String hoge() {
                return name();
            }
        };

        /**
         * @return
         */
        public abstract String hoge();
    }

    /**
     * 
     */
    public static class AaaAction {

        /**
         * @return
         */
        @Execute
        public String execute() {
            return "/aaa/bbb.jsp";
        }
    }
}