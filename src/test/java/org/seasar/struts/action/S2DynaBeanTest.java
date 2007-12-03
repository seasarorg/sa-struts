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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.seasar.framework.aop.Aspect;
import org.seasar.framework.aop.impl.AspectImpl;
import org.seasar.framework.aop.interceptors.TraceInterceptor;
import org.seasar.framework.aop.proxy.AopProxy;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.tiger.Maps;

/**
 * @author higa
 * 
 */
public class S2DynaBeanTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testGetBeanClass() throws Exception {
        Aspect aspect = new AspectImpl(new TraceInterceptor());
        AopProxy aopProxy = new AopProxy(BbbAction.class,
                new Aspect[] { aspect });
        BbbAction action = (BbbAction) aopProxy.create();
        S2DynaClass dynaClass = new S2DynaClass();
        S2DynaBean dynaBean = new S2DynaBean(dynaClass, action);
        assertEquals(BbbAction.class, dynaBean.getBeanClass());
    }

    /**
     * @throws Exception
     */
    public void testGetProperty() throws Exception {
        BbbAction action = new BbbAction();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hoge");
        S2DynaClass dynaClass = new S2DynaClass();
        S2DynaProperty property = new S2DynaProperty(pd);
        dynaClass.addDynaProperty(property);
        S2DynaBean dynaBean = new S2DynaBean(dynaClass, action);
        assertNotNull(dynaBean.getProperty("hoge"));
    }

    /**
     * @throws Exception
     */
    public void testGetProperty_propertyNotFound() throws Exception {
        BbbAction action = new BbbAction();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hoge");
        S2DynaClass dynaClass = new S2DynaClass();
        S2DynaProperty property = new S2DynaProperty(pd);
        dynaClass.addDynaProperty(property);
        S2DynaBean dynaBean = new S2DynaBean(dynaClass, action);
        try {
            dynaBean.getProperty("xxx");
            fail();
        } catch (PropertyNotFoundRuntimeException e) {
            System.out.println(e);
            assertEquals(BbbAction.class, e.getTargetClass());
            assertEquals("xxx", e.getPropertyName());
        }
    }

    /**
     * @throws Exception
     */
    public void testGet() throws Exception {
        BbbAction action = new BbbAction();
        action.hoge = "aaa";
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hoge");
        S2DynaClass dynaClass = new S2DynaClass();
        S2DynaProperty property = new S2DynaProperty(pd);
        dynaClass.addDynaProperty(property);
        S2DynaBean dynaBean = new S2DynaBean(dynaClass, action);
        assertEquals("aaa", dynaBean.get("hoge"));
    }

    /**
     * @throws Exception
     */
    public void testSet() throws Exception {
        BbbAction action = new BbbAction();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hoge");
        S2DynaClass dynaClass = new S2DynaClass();
        S2DynaProperty property = new S2DynaProperty(pd);
        dynaClass.addDynaProperty(property);
        S2DynaBean dynaBean = new S2DynaBean(dynaClass, action);
        dynaBean.set("hoge", "aaa");
        assertEquals("aaa", action.hoge);
    }

    /**
     * @throws Exception
     */
    public void testGetForList() throws Exception {
        BbbAction action = new BbbAction();
        action.hogeList = Arrays.asList("aaa", "bbb");
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeList");
        S2DynaClass dynaClass = new S2DynaClass();
        S2DynaListProperty property = new S2DynaListProperty(pd);
        dynaClass.addDynaProperty(property);
        S2DynaBean dynaBean = new S2DynaBean(dynaClass, action);
        assertEquals("aaa", dynaBean.get("hogeList", 0));
    }

    /**
     * @throws Exception
     */
    public void testSetForList() throws Exception {
        BbbAction action = new BbbAction();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeList");
        S2DynaClass dynaClass = new S2DynaClass();
        S2DynaListProperty property = new S2DynaListProperty(pd);
        dynaClass.addDynaProperty(property);
        S2DynaBean dynaBean = new S2DynaBean(dynaClass, action);
        dynaBean.set("hogeList", 0, "aaa");
        assertEquals("aaa", action.hogeList.get(0));
    }

    /**
     * @throws Exception
     */
    public void testGetForMap() throws Exception {
        BbbAction action = new BbbAction();
        action.hogeMap = Maps.map("hoge", "aaa").$();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeMap");
        S2DynaClass dynaClass = new S2DynaClass();
        S2DynaMappedProperty property = new S2DynaMappedProperty(pd);
        dynaClass.addDynaProperty(property);
        S2DynaBean dynaBean = new S2DynaBean(dynaClass, action);
        assertEquals("aaa", dynaBean.get("hogeMap", "hoge"));
    }

    /**
     * @throws Exception
     */
    public void testContains() throws Exception {
        BbbAction action = new BbbAction();
        action.hogeMap = Maps.map("hoge", "aaa").$();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeMap");
        S2DynaClass dynaClass = new S2DynaClass();
        S2DynaMappedProperty property = new S2DynaMappedProperty(pd);
        dynaClass.addDynaProperty(property);
        S2DynaBean dynaBean = new S2DynaBean(dynaClass, action);
        assertTrue(dynaBean.contains("hogeMap", "hoge"));
        assertFalse(dynaBean.contains("hogeMap", "hoge2"));
    }

    /**
     * @throws Exception
     */
    public void testSetForMap() throws Exception {
        BbbAction action = new BbbAction();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hogeMap");
        S2DynaClass dynaClass = new S2DynaClass();
        S2DynaMappedProperty property = new S2DynaMappedProperty(pd);
        dynaClass.addDynaProperty(property);
        S2DynaBean dynaBean = new S2DynaBean(dynaClass, action);
        dynaBean.set("hogeMap", "hoge", "aaa");
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
        S2DynaClass dynaClass = new S2DynaClass();
        S2DynaMappedProperty property = new S2DynaMappedProperty(pd);
        dynaClass.addDynaProperty(property);
        S2DynaBean dynaBean = new S2DynaBean(dynaClass, action);
        dynaBean.remove("hogeMap", "hoge");
        assertFalse(action.hogeMap.containsKey("hoge"));
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

        /**
         * 
         */
        public Map<String, String> hogeMap;
    }
}