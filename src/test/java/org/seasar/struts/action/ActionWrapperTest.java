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

import java.lang.reflect.Method;

import org.apache.struts.action.ActionForward;
import org.apache.struts.config.ForwardConfig;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;

/**
 * @author higa
 * 
 */
public class ActionWrapperTest extends S2TestCase {

    public void setUp() {
        register(BbbAction.class, "bbbAction");
    }

    /**
     * @throws Exception
     */
    public void testExecute() throws Exception {
        BbbAction action = (BbbAction) getComponent("bbbAction");
        ActionWrapper wrapper = new ActionWrapper(action);
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        Method m = BbbAction.class.getDeclaredMethod("execute");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(m, true);
        actionMapping.addExecuteConfig(executeConfig);
        ActionForward fowardConfig = new ActionForward();
        fowardConfig.setName("success");
        fowardConfig.setPath("/aaa/bbb.jsp");
        actionMapping.addForwardConfig(fowardConfig);
        ForwardConfig forward = wrapper.execute(actionMapping, null,
                getRequest(), getResponse());
        assertNotNull(forward);
        assertEquals("/aaa/bbb.jsp", forward.getPath());
    }

    /**
     * @throws Exception
     */
    public void testExecute_results() throws Exception {
        BbbAction action = (BbbAction) getComponent("bbbAction");
        ActionWrapper wrapper = new ActionWrapper(action);
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        Method m = BbbAction.class.getDeclaredMethod("execute");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(m, true);
        actionMapping.addExecuteConfig(executeConfig);
        m = BbbAction.class.getDeclaredMethod("execute2");
        executeConfig = new S2ExecuteConfig(m, true);
        actionMapping.addExecuteConfig(executeConfig);
        ActionForward fowardConfig = new ActionForward();
        fowardConfig.setName("success");
        fowardConfig.setPath("/aaa/bbb.jsp");
        actionMapping.addForwardConfig(fowardConfig);
        fowardConfig = new ActionForward();
        fowardConfig.setName("success2");
        fowardConfig.setPath("/aaa/bbb2.jsp");
        actionMapping.addForwardConfig(fowardConfig);
        getRequest().setParameter("execute2", "hoge");
        ForwardConfig forward = wrapper.execute(actionMapping, null,
                getRequest(), getResponse());
        assertNotNull(forward);
        assertEquals("/aaa/bbb2.jsp", forward.getPath());
    }

    /**
     * @throws Exception
     */
    public void testExportPropertiesToRequest() throws Exception {
        BbbAction action = (BbbAction) getComponent("bbbAction");
        ActionWrapper wrapper = new ActionWrapper(action);
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        Method m = BbbAction.class.getDeclaredMethod("execute");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig(m, true);
        actionMapping.addExecuteConfig(executeConfig);
        ActionForward fowardConfig = new ActionForward();
        fowardConfig.setName("success");
        fowardConfig.setPath("/aaa/bbb.jsp");
        actionMapping.addForwardConfig(fowardConfig);
        wrapper.execute(actionMapping, null, getRequest(), getResponse());
        assertEquals("111", getRequest().getAttribute("hoge"));
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
         * @return
         */
        public String execute() {
            hoge = "111";
            return "success";
        }

        /**
         * @return
         */
        public String execute2() {
            return "success2";
        }
    }
}