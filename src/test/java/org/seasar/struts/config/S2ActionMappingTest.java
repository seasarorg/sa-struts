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
package org.seasar.struts.config;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.commons.beanutils.DynaProperty;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.impl.ComponentDefImpl;
import org.seasar.struts.action.S2DynaProperty;

/**
 * @author higa
 * 
 */
public class S2ActionMappingTest extends TestCase {

    /**
     * 
     */
    public String hoge;

    /**
     * @throws Exception
     */
    public void testExecuteConfig() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        Method m = getClass().getDeclaredMethod("testExecuteConfig");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(m, true);
        actionMapping.addExecuteConfig(executeConfig);
        assertSame(executeConfig, actionMapping
                .getExecuteConfig("testExecuteConfig"));
        String[] names = actionMapping.getExecuteMethodNames();
        assertEquals(1, names.length);
        assertEquals("testExecuteConfig", names[0]);
    }

    /**
     * @throws Exception
     */
    public void testBeanDesc() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(String.class);
        actionMapping.setComponentDef(cd);
        assertNotNull(actionMapping.getBeanDesc());
    }

    /**
     * @throws Exception
     */
    public void testActionForm() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class);
        actionMapping.setComponentDef(cd);
        assertTrue(actionMapping.getActionForm() instanceof MyAction);
    }

    /**
     * @throws Exception
     */
    public void testActionFormClass() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class);
        actionMapping.setComponentDef(cd);
        assertEquals(MyAction.class, actionMapping.getActionFormClass());
    }

    /**
     * @throws Exception
     */
    public void testDynaProperty() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(getClass());
        PropertyDesc pd = beanDesc.getPropertyDesc("hoge");
        S2DynaProperty property = new S2DynaProperty("hoge", String.class, pd);
        actionMapping.addDynaProperty(property);
        DynaProperty[] properties = actionMapping.getDynaProperties();
        assertEquals(1, properties.length);
        assertSame(property, actionMapping.getDynaProperty("hoge"));
    }

    /**
     * 
     */
    public static class MyAction {

    }
}