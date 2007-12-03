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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.tiger.Maps;

/**
 * @author higa
 * 
 */
public class S2DynaMappedPropertyTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testContentType() throws Exception {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeMap");
        S2DynaMappedProperty property = new S2DynaMappedProperty(pd);
        assertEquals(String.class, property.getContentType());
    }

    /**
     * @throws Exception
     */
    public void testGetValue() throws Exception {
        BbbAction action = new BbbAction();
        action.hogeMap = Maps.map("hoge", "aaa").$();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeMap");
        S2DynaMappedProperty property = new S2DynaMappedProperty(pd);
        assertEquals("aaa", property.getValue(action, "hoge"));
    }

    /**
     * @throws Exception
     */
    public void testContains() throws Exception {
        BbbAction action = new BbbAction();
        action.hogeMap = Maps.map("hoge", "aaa").$();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeMap");
        S2DynaMappedProperty property = new S2DynaMappedProperty(pd);
        assertTrue(property.contains(action, "hoge"));
        assertFalse(property.contains(action, "hoge2"));
    }

    /**
     * @throws Exception
     */
    public void testSetValue_mapNull() throws Exception {
        BbbAction action = new BbbAction();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeMap");
        S2DynaMappedProperty property = new S2DynaMappedProperty(pd);
        property.setValue(action, "hoge", "aaa");
        assertEquals("aaa", action.hogeMap.get("hoge"));
    }

    /**
     * @throws Exception
     */
    public void testSetValue_mapNotNull() throws Exception {
        BbbAction action = new BbbAction();
        action.hogeMap = new HashMap<String, String>();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeMap");
        S2DynaMappedProperty property = new S2DynaMappedProperty(pd);
        property.setValue(action, "hoge", "aaa");
        assertEquals("aaa", action.hogeMap.get("hoge"));
    }

    /**
     * @throws Exception
     */
    public void testRemove() throws Exception {
        BbbAction action = new BbbAction();
        action.hogeMap = Maps.map("hoge", "aaa").$();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeMap");
        S2DynaMappedProperty property = new S2DynaMappedProperty(pd);
        property.remove(action, "hoge");
        assertNull(action.hogeMap.get("hoge"));
    }

    /**
     * 
     */
    private static class BbbAction {

        /**
         * 
         */
        public Map<String, String> hogeMap;
    }
}