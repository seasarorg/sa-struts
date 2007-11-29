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

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.struts.config.S2ActionMapping;

/**
 * @author higa
 * 
 */
public class ActionFromWrapperTest extends S2TestCase {

    /**
     * @throws Exception
     */
    public void testGetProperty() throws Exception {
        register(BbbAction.class, "bbbAction");
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hoge");
        S2ActionMapping actionMapping = new S2ActionMapping();
        S2DynaProperty property = new S2DynaProperty("hoge", String.class, pd);
        actionMapping.addDynaProperty(property);
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass,
                actionMapping);
        assertNotNull(formWrapper.getProperty("hoge"));
    }

    /**
     * @throws Exception
     */
    public void testGetProperty_propertyNotFound() throws Exception {
        register(BbbAction.class, "bbbAction");
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hoge");
        S2ActionMapping actionMapping = new S2ActionMapping();
        S2DynaProperty property = new S2DynaProperty("hoge", String.class, pd);
        actionMapping.addDynaProperty(property);
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass,
                actionMapping);
        try {
            formWrapper.getProperty("xxx");
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
        register(BbbAction.class, "bbbAction");
        BbbAction action = (BbbAction) getComponent("bbbAction");
        action.hoge = "aaa";
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hoge");
        S2ActionMapping actionMapping = new S2ActionMapping();
        S2DynaProperty property = new S2DynaProperty("hoge", String.class, pd);
        actionMapping.addDynaProperty(property);
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass,
                actionMapping);
        assertEquals("aaa", formWrapper.get("hoge"));
    }

    /**
     * @throws Exception
     */
    public void testSet() throws Exception {
        register(BbbAction.class, "bbbAction");
        BbbAction action = (BbbAction) getComponent("bbbAction");
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BbbAction.class);
        PropertyDesc pd = beanDesc.getPropertyDesc("hoge");
        S2ActionMapping actionMapping = new S2ActionMapping();
        S2DynaProperty property = new S2DynaProperty("hoge", String.class, pd);
        actionMapping.addDynaProperty(property);
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass,
                actionMapping);
        formWrapper.set("hoge", "aaa");
        assertEquals("aaa", action.hoge);
    }

    /**
     * 
     */
    public static class BbbAction {

        /**
         * 
         */
        public String hoge;
    }
}