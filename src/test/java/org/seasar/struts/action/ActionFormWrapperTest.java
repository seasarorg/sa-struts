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
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.annotation.Required;
import org.seasar.struts.annotation.Result;
import org.seasar.struts.config.S2ActionMapping;

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
    public void testReset() throws Exception {
        BbbAction actionForm = (BbbAction) getComponent("bbbAction");
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        actionMapping.setResetMethod(BbbAction.class.getMethod("reset"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass);
        formWrapper.reset(actionMapping, getRequest());
        assertEquals("reset", actionForm.hoge);
    }

    /**
     * 
     */
    @Result(path = "/aaa/bbb.jsp")
    public static class BbbAction {

        /**
         * 
         */
        @Required
        public String hoge;

        /**
         * @return
         */
        @Execute
        public String execute() {
            hoge = "111";
            return "success";
        }

        /**
         * 
         */
        public void reset() {
            hoge = "reset";
        }
    }
}