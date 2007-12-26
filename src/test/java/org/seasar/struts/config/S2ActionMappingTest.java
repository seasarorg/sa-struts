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

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.impl.ComponentDefImpl;

/**
 * @author higa
 * 
 */
public class S2ActionMappingTest extends S2TestCase {

    /**
     * 
     */
    public String hoge;

    /**
     * @throws Exception
     */
    public void testScope() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        assertEquals("request", actionMapping.getScope());
    }

    /**
     * @throws Exception
     */
    public void testExecuteConfig() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        Method m = getClass().getDeclaredMethod("testExecuteConfig");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(m, true, null,
                null, null);
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
    public void testActionBeanDesc() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class);
        actionMapping.setComponentDef(cd);
        assertEquals(MyAction.class, actionMapping.getActionFormBeanDesc()
                .getBeanClass());
    }

    /**
     * @throws Exception
     */
    public void testActionFormBeanDesc_action() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class);
        actionMapping.setComponentDef(cd);
        assertEquals(MyAction.class, actionMapping.getActionFormBeanDesc()
                .getBeanClass());
    }

    /**
     * @throws Exception
     */
    public void testActionFormBeanDesc_actionForm() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class);
        actionMapping.setComponentDef(cd);
        actionMapping.setActionFormPropertyDesc(actionMapping
                .getActionBeanDesc().getPropertyDesc("myActionForm"));
        assertEquals(MyActionForm.class, actionMapping.getActionFormBeanDesc()
                .getBeanClass());
    }

    /**
     * @throws Exception
     */
    public void testAction() throws Exception {
        register(MyAction.class);
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef(MyAction.class));
        assertTrue(actionMapping.getAction() instanceof MyAction);
    }

    /**
     * @throws Exception
     */
    public void testActionForm_action() throws Exception {
        register(MyAction.class);
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef(MyAction.class));
        assertTrue(actionMapping.getActionForm() instanceof MyAction);
    }

    /**
     * @throws Exception
     */
    public void testActionForm_actionForm() throws Exception {
        register(MyAction.class);
        register(MyActionForm.class, "myActionForm");
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef(MyAction.class));
        actionMapping.setActionFormPropertyDesc(actionMapping
                .getActionBeanDesc().getPropertyDesc("myActionForm"));
        assertTrue(actionMapping.getActionForm() instanceof MyActionForm);
    }

    /**
     * 
     */
    public static class MyAction {

        /**
         * 
         */
        public MyActionForm myActionForm;
    }

    /**
     * 
     */
    public static class MyActionForm {

    }
}