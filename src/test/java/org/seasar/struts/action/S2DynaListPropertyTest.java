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

import java.util.ArrayList;
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
public class S2DynaListPropertyTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testContentType() throws Exception {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeList");
        S2DynaListProperty property = new S2DynaListProperty(pd);
        assertEquals(String.class, property.getContentType());
    }

    /**
     * @throws Exception
     */
    public void testGetValue() throws Exception {
        BbbAction action = new BbbAction();
        action.hogeList = Arrays.asList("aaa", "bbb");
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeList");
        S2DynaListProperty property = new S2DynaListProperty(pd);
        assertEquals("aaa", property.getValue(action, 0));
    }

    /**
     * @throws Exception
     */
    public void testSetValue_listNull() throws Exception {
        BbbAction action = new BbbAction();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeList");
        S2DynaListProperty property = new S2DynaListProperty(pd);
        property.setValue(action, 0, "aaa");
        assertEquals("aaa", action.hogeList.get(0));
    }

    /**
     * @throws Exception
     */
    public void testSetValue_listNotNull_sizeZero() throws Exception {
        BbbAction action = new BbbAction();
        action.hogeList = new ArrayList<String>();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeList");
        S2DynaListProperty property = new S2DynaListProperty(pd);
        property.setValue(action, 0, "aaa");
        assertEquals("aaa", action.hogeList.get(0));
    }

    /**
     * @throws Exception
     */
    public void testSetValue_listNotNull_sizeGtZero() throws Exception {
        BbbAction action = new BbbAction();
        action.hogeList = Arrays.asList("xxx");
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeList");
        S2DynaListProperty property = new S2DynaListProperty(pd);
        property.setValue(action, 0, "aaa");
        assertEquals("aaa", action.hogeList.get(0));
    }

    /**
     * 
     */
    public static class BbbAction {

        /**
         * 
         */
        public List<String> hogeList;
    }
}