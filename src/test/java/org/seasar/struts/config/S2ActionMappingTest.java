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
package org.seasar.struts.config;

import java.lang.reflect.Method;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForward;
import org.apache.struts.util.MessageResourcesFactory;
import org.apache.struts.validator.ValidatorPlugIn;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.impl.ComponentDefImpl;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.customizer.ActionCustomizer;
import org.seasar.struts.util.S2ExecuteConfigUtil;
import org.seasar.struts.util.S2PropertyMessageResourcesFactory;
import org.seasar.struts.validator.S2ValidatorResources;

/**
 * @author higa
 * 
 */
public class S2ActionMappingTest extends S2TestCase {

    /**
     * 
     */
    public String hoge;

    private ActionCustomizer customizer = new ActionCustomizer();

    private S2ModuleConfig moduleConfig = new S2ModuleConfig("");

    private S2ValidatorResources validatorResources = new S2ValidatorResources();

    /**
     * @return
     */
    public String index() {
        return "index.jsp";
    }

    @Override
    public void setUpAfterContainerInit() {
        getServletContext().setAttribute(Globals.SERVLET_KEY, "/*");
        getServletContext().setAttribute(Globals.MODULE_KEY, moduleConfig);
        MessageResourcesFactory mrf = new S2PropertyMessageResourcesFactory();
        getServletContext().setAttribute(Globals.MESSAGES_KEY,
                mrf.createResources("SASMessages"));
        getServletContext().setAttribute(ValidatorPlugIn.VALIDATOR_KEY,
                validatorResources);
        register(MyAction.class, "aaaAction");
        customizer.customize(getComponentDef("aaaAction"));
    }

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
    public void testGetQueryString_paramPathEmpty() throws Exception {
        String actionPath = "/aaa";
        String paramPath = "";
        S2ActionMapping actionMapping = new S2ActionMapping();
        S2ExecuteConfig executeConfig = S2ExecuteConfigUtil.findExecuteConfig(
                actionPath, paramPath);
        actionMapping.addExecuteConfig(executeConfig);
        assertEquals("?aaa=1&SAStruts.method=index", actionMapping
                .getQueryString("?aaa=1", actionPath, paramPath, null));
    }

    /**
     * @throws Exception
     */
    public void testGetQueryString_paramPath() throws Exception {
        String actionPath = "/aaa";
        String paramPath = "submit/2";
        S2ActionMapping actionMapping = new S2ActionMapping();
        S2ExecuteConfig executeConfig = S2ExecuteConfigUtil.findExecuteConfig(
                actionPath, paramPath);
        actionMapping.addExecuteConfig(executeConfig);
        assertEquals("?aaa=1&id=2&SAStruts.method=submit", actionMapping
                .getQueryString("?aaa=1", actionPath, paramPath, executeConfig));
    }

    /**
     * @throws Exception
     */
    public void testGetQueryString_paramPath_queryStringEmpty()
            throws Exception {
        String actionPath = "/aaa";
        String paramPath = "submit/2";
        S2ActionMapping actionMapping = new S2ActionMapping();
        S2ExecuteConfig executeConfig = S2ExecuteConfigUtil.findExecuteConfig(
                actionPath, paramPath);
        actionMapping.addExecuteConfig(executeConfig);
        assertEquals("?id=2&SAStruts.method=submit", actionMapping
                .getQueryString("", "/aaa", "submit/2", executeConfig));
    }

    /**
     * @throws Exception
     */
    public void testGetQueryString_paramPathEmpty_queryStringEmpty()
            throws Exception {
        String actionPath = "/aaa";
        String paramPath = "";
        S2ActionMapping actionMapping = new S2ActionMapping();
        S2ExecuteConfig executeConfig = S2ExecuteConfigUtil.findExecuteConfig(
                actionPath, paramPath);
        actionMapping.addExecuteConfig(executeConfig);
        assertEquals("?SAStruts.method=index", actionMapping.getQueryString("",
                "/aaa", "", null));
    }

    /**
     * @throws Exception
     */
    public void testGetQueryString_paramPathEmpty_queryStringEmpty_executeConfig()
            throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(Object.class.getMethod("getClass"));
        S2ActionMapping actionMapping = new S2ActionMapping();
        assertEquals("?SAStruts.method=getClass", actionMapping.getQueryString(
                "", "/aaa", "", executeConfig));
    }

    /**
     * @throws Exception
     */
    public void testGetQueryString_paramPathEmpty_executeConfig()
            throws Exception {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(Object.class.getMethod("getClass"));
        S2ActionMapping actionMapping = new S2ActionMapping();
        assertEquals("?aaa=1&SAStruts.method=getClass", actionMapping
                .getQueryString("?aaa=1", "/aaa", "", executeConfig));
    }

    /**
     * @throws Exception
     */
    public void testGetActionPath() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        assertEquals("/aaa/", actionMapping.getActionPath("aaaAction"));
    }

    /**
     * @throws Exception
     */
    public void testGetActionPath_index() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        assertEquals("/", actionMapping.getActionPath("indexAction"));
    }

    /**
     * @throws Exception
     */
    public void testGetActionPath_nested_index() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        assertEquals("/aaa/", actionMapping.getActionPath("aaa_indexAction"));
    }

    /**
     * @throws Exception
     */
    public void testCreateRoutingPath() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        assertEquals("/aaa.do?hoge=1&id=2&SAStruts.method=submit",
                actionMapping.createRoutingPath("/aaa/submit/2?hoge=1"));
    }

    /**
     * @throws Exception
     */
    public void testCreateRoutingPath_nest() throws Exception {
        register(MySubmit.class, "aaa_submitAction");
        customizer.customize(getComponentDef("aaa_submitAction"));
        S2ActionMapping actionMapping = new S2ActionMapping();
        assertEquals("/aaa/submit.do?aaa=1&id=2&SAStruts.method=hoge",
                actionMapping.createRoutingPath("/aaa/submit/hoge/2?aaa=1"));
    }

    /**
     * @throws Exception
     */
    public void testCreateRoutingPath_index() throws Exception {
        register(MyAction.class, "indexAction");
        ComponentDef cd = getComponentDef("indexAction");
        customizer.customize(cd);
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(cd);
        ActionForward forward = actionMapping.createForward("submit/2?hoge=1");
        assertEquals("/index.do?hoge=1&id=2&SAStruts.method=submit", forward
                .getPath());
    }

    /**
     * @throws Exception
     */
    public void testCreateRoutingPath_nested_index() throws Exception {
        register(MyAction.class, "bbb_indexAction");
        ComponentDef cd = getComponentDef("bbb_indexAction");
        customizer.customize(cd);
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(cd);
        ActionForward forward = actionMapping
                .createForward("/bbb/submit/2?hoge=1");
        assertEquals("/bbb/index.do?hoge=1&id=2&SAStruts.method=submit",
                forward.getPath());
    }

    /**
     * @throws Exception
     */
    public void testCreateForward_jsp() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class, "aaaAction");
        actionMapping.setComponentDef(cd);
        ActionForward forward = actionMapping.createForward("hoge.jsp");
        assertNotNull(forward);
        assertEquals("/aaa/hoge.jsp", forward.getPath());
        assertFalse(forward.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testCreateForward_jsp_viewPrefix() throws Exception {
        getServletContext().setInitParameter("sastruts.VIEW_PREFIX",
                "/WEB-INF/jsp");
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class, "aaaAction");
        actionMapping.setComponentDef(cd);
        ActionForward forward = actionMapping.createForward("hoge.jsp");
        assertNotNull(forward);
        assertEquals("/WEB-INF/jsp/aaa/hoge.jsp", forward.getPath());
        assertFalse(forward.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testCreateForward_jsp2() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class, "aaaAction");
        actionMapping.setComponentDef(cd);
        ActionForward forward = actionMapping.createForward("/hoge.jsp");
        assertNotNull(forward);
        assertEquals("/hoge.jsp", forward.getPath());
        assertFalse(forward.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testCreateForward_jsp2_viewPrefix() throws Exception {
        getServletContext().setInitParameter("sastruts.VIEW_PREFIX",
                "/WEB-INF/jsp");
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class, "aaaAction");
        actionMapping.setComponentDef(cd);
        ActionForward forward = actionMapping.createForward("/hoge.jsp");
        assertNotNull(forward);
        assertEquals("/WEB-INF/jsp/hoge.jsp", forward.getPath());
        assertFalse(forward.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testCreateForward_jsp_nestedIndex() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class,
                "bbb_indexAction");
        actionMapping.setComponentDef(cd);
        ActionForward forward = actionMapping.createForward("hoge.jsp");
        assertNotNull(forward);
        assertEquals("/bbb/hoge.jsp", forward.getPath());
        assertFalse(forward.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testCreateForward_redirect() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class, "aaaAction");
        actionMapping.setComponentDef(cd);
        ActionForward forward = actionMapping
                .createForward("hoge.jsp?redirect=true");
        assertNotNull(forward);
        assertEquals("/aaa/hoge.jsp", forward.getPath());
        assertTrue(forward.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testCreateForward_redirect2() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class, "aaaAction");
        actionMapping.setComponentDef(cd);
        ActionForward forward = actionMapping
                .createForward("hoge.jsp?aaa=1&redirect=true");
        assertNotNull(forward);
        assertEquals("/aaa/hoge.jsp?aaa=1", forward.getPath());
        assertTrue(forward.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testCreateForward_redirect3() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class, "aaaAction");
        actionMapping.setComponentDef(cd);
        ActionForward forward = actionMapping
                .createForward("/aaa?redirect=true");
        assertNotNull(forward);
        assertEquals("/aaa", forward.getPath());
        assertTrue(forward.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testCreateForward_redirect_viewPrefix() throws Exception {
        getServletContext().setInitParameter("sastruts.VIEW_PREFIX",
                "/WEB-INF/jsp");
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class, "aaaAction");
        actionMapping.setComponentDef(cd);
        ActionForward forward = actionMapping
                .createForward("/bbb/ccc?redirect=true");
        assertNotNull(forward);
        assertEquals("view prefix must be ignored on redirection", "/bbb/ccc",
                forward.getPath());
        assertTrue(forward.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testCreateForward_routing() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef(MyAction.class));
        ActionForward forward = actionMapping.createForward("submit/2?hoge=1");
        assertNotNull(forward);
        assertEquals("/aaa.do?hoge=1&id=2&SAStruts.method=submit", forward
                .getPath());
        assertFalse(forward.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testCreateForward_routing2() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef(MyAction.class));
        ActionForward forward = actionMapping.createForward("submit/2");
        assertNotNull(forward);
        assertEquals("/aaa.do?id=2&SAStruts.method=submit", forward.getPath());
        assertFalse(forward.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testCreateForward_routing3() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef(MyAction.class));
        ActionForward forward = actionMapping.createForward("/aaa/submit/2");
        assertNotNull(forward);
        assertEquals("/aaa.do?id=2&SAStruts.method=submit", forward.getPath());
        assertFalse(forward.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testCreateForward_null() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef(MyAction.class));
        assertNull(actionMapping.createForward(null));
    }

    /**
     * @throws Exception
     */
    public void testCreateForward_http() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class, "aaaAction");
        actionMapping.setComponentDef(cd);
        ActionForward forward = actionMapping
                .createForward("http://www.seasar.org?redirect=true");
        assertNotNull(forward);
        assertEquals("http://www.seasar.org", forward.getPath());
        assertTrue(forward.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testGetViewDirectory() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        assertEquals("/login/", actionMapping.getActionPath("loginAction"));
        assertEquals("/aaa/login/", actionMapping
                .getActionPath("aaa_loginAction"));
        assertEquals("/", actionMapping.getActionPath("indexAction"));
        try {
            actionMapping.getActionPath("hoge");
            fail();
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
    }

    /**
     * @throws Exception
     */
    public void testGetExecuteConfig() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        Method m = getClass().getDeclaredMethod("testGetExecuteConfig");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(m);
        actionMapping.addExecuteConfig(executeConfig);
        assertSame(executeConfig, actionMapping
                .getExecuteConfig("testGetExecuteConfig"));
    }

    /**
     * @throws Exception
     */
    public void testGetExecuteMethodNames() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        Method m = getClass().getDeclaredMethod("testGetExecuteMethodNames");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(m);
        actionMapping.addExecuteConfig(executeConfig);
        String[] names = actionMapping.getExecuteMethodNames();
        assertEquals(1, names.length);
        assertEquals("testGetExecuteMethodNames", names[0]);
    }

    /**
     * @throws Exception
     */
    public void testFindExecuteConfig_request() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        Method m = getClass().getDeclaredMethod("testGetExecuteConfig");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(m);
        actionMapping.addExecuteConfig(executeConfig);
        getRequest().setParameter("testGetExecuteConfig", "hoge");
        assertSame(executeConfig, actionMapping.findExecuteConfig(getRequest()));
    }

    /**
     * @throws Exception
     */
    public void testFindExecuteConfig_request_index() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        Method m = getClass().getMethod("index");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(m);
        actionMapping.addExecuteConfig(executeConfig);
        m = getClass().getMethod("getClass");
        executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(m);
        actionMapping.addExecuteConfig(executeConfig);
        assertEquals("index", actionMapping.findExecuteConfig(getRequest())
                .getMethod().getName());
    }

    /**
     * @throws Exception
     */
    public void testFindExecuteConfig_request_onlyone() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        Method m = getClass().getMethod("getClass");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(m);
        actionMapping.addExecuteConfig(executeConfig);
        assertEquals("getClass", actionMapping.findExecuteConfig(getRequest())
                .getMethod().getName());
    }

    /**
     * @throws Exception
     */
    public void testFindExecuteConfig_paramPath() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        Method m = getClass().getMethod("getClass");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(m);
        executeConfig.setUrlPattern("hoge");
        actionMapping.addExecuteConfig(executeConfig);
        assertSame(executeConfig, actionMapping.findExecuteConfig("hoge"));
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
        register(MyActionForm.class, "myActionForm");
        S2ActionMapping actionMapping = new S2ActionMapping();
        ComponentDef cd = new ComponentDefImpl(MyAction.class);
        actionMapping.setComponentDef(cd);
        actionMapping.setActionFormField(actionMapping.getActionBeanDesc()
                .getField("myActionForm"));
        assertEquals(MyActionForm.class, actionMapping.getActionFormBeanDesc()
                .getBeanClass());
    }

    /**
     * @throws Exception
     */
    public void testAction() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef(MyAction.class));
        assertTrue(actionMapping.getAction() instanceof MyAction);
    }

    /**
     * @throws Exception
     */
    public void testGetPropertyAsString() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef(MyAction.class));
        MyAction action = (MyAction) getComponent(MyAction.class);
        action.id = "111";
        assertEquals("111", actionMapping.getPropertyAsString("id"));
    }

    /**
     * @throws Exception
     */
    public void testGetPropertyAsString_actionForm() throws Exception {
        register(MyActionForm.class, "myActionForm");
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef(MyAction.class));
        actionMapping.setActionFormField(actionMapping.getActionBeanDesc()
                .getField("myActionForm"));
        MyActionForm actionForm = (MyActionForm) getComponent(MyActionForm.class);
        actionForm.aaa = "222";
        assertEquals("222", actionMapping.getPropertyAsString("aaa"));
    }

    /**
     * @throws Exception
     */
    public void testGetPropertyAsString_null() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef(MyAction.class));
        assertEquals("null", actionMapping.getPropertyAsString("id"));
    }

    /**
     * @throws Exception
     */
    public void testActionForm_action() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef(MyAction.class));
        assertTrue(actionMapping.getActionForm() instanceof MyAction);
    }

    /**
     * @throws Exception
     */
    public void testActionForm_actionForm() throws Exception {
        register(MyActionForm.class, "myActionForm");
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef(MyAction.class));
        actionMapping.setActionFormField(actionMapping.getActionBeanDesc()
                .getField("myActionForm"));
        assertTrue(actionMapping.getActionForm() instanceof MyActionForm);
    }

    /**
     * 
     */
    public static class MyAction {

        /**
         * 
         */
        public String id;

        /**
         * 
         */
        public MyActionForm myActionForm;

        /**
         * @return
         */
        @Execute(validator = false)
        public String index() {
            return "index.jsp";
        }

        /**
         * @return
         */
        @Execute(validator = false, urlPattern = "submit/{id}")
        public String submit() {
            return "hoge.jsp";
        }
    }

    /**
     * 
     */
    public static class MySubmit {

        /**
         * 
         */
        public String id;

        /**
         * @return
         */
        @Execute(validator = false, urlPattern = "hoge/{id}")
        public String hoge() {
            return "hoge.jsp";
        }
    }

    /**
     * 
     */
    public static class MyActionForm {

        /**
         * 
         */
        public String aaa;
    }
}