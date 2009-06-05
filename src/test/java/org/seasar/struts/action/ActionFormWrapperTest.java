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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.annotation.Required;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.util.S2ExecuteConfigUtil;

/**
 * @author higa
 * 
 */
public class ActionFormWrapperTest extends S2TestCase {

    @Override
    public void setUp() {
        register(BbbAction.class, "bbbAction");
    }

    /**
     * @throws Exception
     */
    public void testGetProperty() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        S2DynaProperty property = new S2DynaProperty(actionMapping
                .getActionFormBeanDesc().getPropertyDesc("hoge"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        wrapperClass.addDynaProperty(property);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass);
        assertSame(property, formWrapper.getProperty("hoge"));
    }

    /**
     * @throws Exception
     */
    public void testGetProperty_notFound() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass);
        try {
            formWrapper.getProperty("hoge");
            fail();
        } catch (PropertyNotFoundRuntimeException e) {
            System.out.println(e);
            assertEquals(BbbAction.class, e.getTargetClass());
        }
    }

    /**
     * @throws Exception
     */
    public void testGet() throws Exception {
        BbbAction actionForm = (BbbAction) getComponent("bbbAction");
        actionForm.hoge = "aaa";
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        S2DynaProperty property = new S2DynaProperty(actionMapping
                .getActionFormBeanDesc().getPropertyDesc("hoge"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        wrapperClass.addDynaProperty(property);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass);
        assertEquals("aaa", formWrapper.get("hoge"));
    }

    /**
     * @throws Exception
     */
    public void testIndexedGetForList() throws Exception {
        BbbAction actionForm = (BbbAction) getComponent("bbbAction");
        actionForm.list = Arrays.asList("123");
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        S2DynaProperty property = new S2DynaProperty(actionMapping
                .getActionFormBeanDesc().getPropertyDesc("list"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        wrapperClass.addDynaProperty(property);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass);
        assertEquals("123", formWrapper.get("list", 0));
    }

    /**
     * @throws Exception
     */
    public void testIndexedGetForNullList() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        S2DynaProperty property = new S2DynaProperty(actionMapping
                .getActionFormBeanDesc().getPropertyDesc("list"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        wrapperClass.addDynaProperty(property);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass);
        try {
            formWrapper.get("list", 0);
            fail();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @throws Exception
     */
    public void testIndexedGetForArray() throws Exception {
        BbbAction actionForm = (BbbAction) getComponent("bbbAction");
        actionForm.array = new String[] { "123" };
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        S2DynaProperty property = new S2DynaProperty(actionMapping
                .getActionFormBeanDesc().getPropertyDesc("array"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        wrapperClass.addDynaProperty(property);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass);
        assertEquals("123", formWrapper.get("array", 0));
    }

    /**
     * @throws Exception
     */
    public void testIndexedGetForNullArray() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        S2DynaProperty property = new S2DynaProperty(actionMapping
                .getActionFormBeanDesc().getPropertyDesc("array"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        wrapperClass.addDynaProperty(property);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass);
        try {
            formWrapper.get("array", 0);
            fail();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @throws Exception
     */
    public void testIndexedGetForNoIndexed() throws Exception {
        BbbAction actionForm = (BbbAction) getComponent("bbbAction");
        actionForm.hoge = "123";
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        S2DynaProperty property = new S2DynaProperty(actionMapping
                .getActionFormBeanDesc().getPropertyDesc("hoge"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        wrapperClass.addDynaProperty(property);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass);
        try {
            formWrapper.get("hoge", 0);
            fail();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @throws Exception
     */
    public void testMappedGet() throws Exception {
        BbbAction actionForm = (BbbAction) getComponent("bbbAction");
        actionForm.map = new HashMap<String, Object>();
        actionForm.map.put("aaa", "111");
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        S2DynaProperty property = new S2DynaProperty(actionMapping
                .getActionFormBeanDesc().getPropertyDesc("map"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        wrapperClass.addDynaProperty(property);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass);
        assertEquals("111", formWrapper.get("map", "aaa"));
    }

    /**
     * @throws Exception
     */
    public void testReset() throws Exception {
        BbbAction actionForm = (BbbAction) getComponent("bbbAction");
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setResetMethod(BbbAction.class.getMethod("reset"));
        S2ExecuteConfigUtil.setExecuteConfig(executeConfig);
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass);
        formWrapper.reset(actionMapping, getRequest());
        assertEquals("reset", actionForm.hoge);
    }

    /**
     * 
     */
    public static class BbbAction {

        /**
         * 
         */
        @Required
        public String hoge;

        /**
         * 
         */
        public List<String> list;

        /**
         * 
         */
        public String[] array;

        /**
         * 
         */
        public Map<String, Object> map;

        /**
         * @return
         */
        @Execute
        public String execute() {
            hoge = "111";
            return "/aaa/bbb.jsp";
        }

        /**
         * 
         */
        public void reset() {
            hoge = "reset";
        }
    }
}