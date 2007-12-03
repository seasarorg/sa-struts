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

import junit.framework.TestCase;

import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * @author higa
 * 
 */
public class S2DynaArrayPropertyTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testContentType() throws Exception {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeArray");
        S2DynaArrayProperty property = new S2DynaArrayProperty(pd);
        assertEquals(String.class, property.getContentType());
    }

    /**
     * @throws Exception
     */
    public void testGetValue() throws Exception {
        BbbAction action = new BbbAction();
        action.hogeArray = new String[] { "aaa", "bbb" };
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeArray");
        S2DynaArrayProperty property = new S2DynaArrayProperty(pd);
        assertEquals("aaa", property.getValue(action, 0));
    }

    /**
     * @throws Exception
     */
    public void testSetValue_arrayNull() throws Exception {
        BbbAction action = new BbbAction();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeArray");
        S2DynaArrayProperty property = new S2DynaArrayProperty(pd);
        property.setValue(action, 0, "aaa");
        assertEquals("aaa", action.hogeArray[0]);
    }

    /**
     * @throws Exception
     */
    public void testSetValue_arrayNotNull_sizeZero() throws Exception {
        BbbAction action = new BbbAction();
        action.hogeArray = new String[0];
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeArray");
        S2DynaArrayProperty property = new S2DynaArrayProperty(pd);
        property.setValue(action, 0, "aaa");
        assertEquals("aaa", action.hogeArray[0]);
    }

    /**
     * @throws Exception
     */
    public void testSetValue_listNotNull_sizeGtZero() throws Exception {
        BbbAction action = new BbbAction();
        action.hogeArray = new String[] { "xxx" };
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeArray");
        S2DynaArrayProperty property = new S2DynaArrayProperty(pd);
        property.setValue(action, 0, "aaa");
        assertEquals("aaa", action.hogeArray[0]);
    }

    /**
     * 
     */
    public static class BbbAction {

        /**
         * 
         */
        public String[] hogeArray;
    }
}