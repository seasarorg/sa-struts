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

import junit.framework.TestCase;

import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * @author higa
 * 
 */
public class S2DynaPropertyTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testGetValue() throws Exception {
        BbbAction action = new BbbAction();
        action.hoge = "aaa";
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hoge");
        S2DynaProperty property = new S2DynaProperty(pd);
        assertEquals("aaa", property.getValue(action));
    }

    /**
     * @throws Exception
     */
    public void testGetValue_wrapper() throws Exception {
        BbbAction action = new BbbAction();
        action.hogeList = Arrays.asList("1");
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeList");
        S2DynaProperty property = new S2DynaProperty(pd);
        Object value = property.getValue(action);
        assertEquals(ListWrapper.class, value.getClass());
    }

    /**
     * 
     */
    public static class BbbAction {

        /**
         * 
         */
        public String hoge;

        /**
         * 
         */
        public List<String> hogeList;
    }
}