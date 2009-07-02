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

import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ForwardConfig;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.container.annotation.tiger.Component;
import org.seasar.framework.container.annotation.tiger.InstanceType;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.annotation.Required;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.config.S2ModuleConfig;
import org.seasar.struts.customizer.ActionCustomizer;
import org.seasar.struts.enums.SaveType;
import org.seasar.struts.util.ActionMessagesUtil;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.S2ExecuteConfigUtil;
import org.seasar.struts.util.S2PropertyMessageResources;
import org.seasar.struts.util.S2PropertyMessageResourcesFactory;
import org.seasar.struts.validator.S2ValidatorPlugIn;

/**
 * @author higa
 * 
 */
public class ActionWrapperTest extends S2TestCase {

    private S2ModuleConfig moduleConfig = new S2ModuleConfig("");

    @Override
    public void setUpAfterContainerInit() throws Exception {
        getServletContext().setAttribute(Globals.SERVLET_KEY, "/*");
        getServletContext().setAttribute(Globals.MODULE_KEY, moduleConfig);
        S2ValidatorPlugIn plugIn = new S2ValidatorPlugIn();
        plugIn.setPathnames("validator-rules.xml");
        plugIn.init(new MyActionServlet(getServletContext()), moduleConfig);
        S2PropertyMessageResourcesFactory factory = new S2PropertyMessageResourcesFactory();
        S2PropertyMessageResources resources = new S2PropertyMessageResources(
                factory, "application");
        getServletContext().setAttribute(Globals.MESSAGES_KEY, resources);
        register(BbbAction.class, "bbbAction");
    }

    /**
     * @throws Exception
     */
    public void testExecute() throws Exception {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        Method m = BbbAction.class.getDeclaredMethod("index");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(m);
        S2ExecuteConfigUtil.setExecuteConfig(executeConfig);
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ForwardConfig forward = wrapper.execute(actionMapping, null,
                getRequest(), getResponse());
        assertNotNull(forward);
        assertEquals("/bbb/index.jsp", forward.getPath());
    }

    /**
     * @throws Exception
     */
    public void testExecute_validate() throws Exception {
        register(CccAction.class, "cccAction");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("cccAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/ccc");
        getRequest().setParameter("execute", "submit");
        S2ExecuteConfigUtil.setExecuteConfig(actionMapping
                .findExecuteConfig(getRequest()));
        CccAction action = (CccAction) getComponent(CccAction.class);
        action.aaa = "111";
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ForwardConfig forward = wrapper.execute(actionMapping, null,
                getRequest(), getResponse());
        assertNotNull(forward);
        assertEquals("/ccc.do?SAStruts.method=input", forward.getPath());
        assertNotNull(getRequest().getAttribute(Globals.ERROR_KEY));
        assertNull(getRequest().getAttribute("aaa"));
    }

    /**
     * @throws Exception
     */
    public void testExecute_parentActionValidateMethod() throws Exception {
        register(OooAction.class, "oooAction");
        register(MyForm.class, "myForm");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("oooAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/ooo");
        getRequest().setParameter("execute", "submit");
        S2ExecuteConfigUtil.setExecuteConfig(actionMapping
                .findExecuteConfig(getRequest()));
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ForwardConfig forward = wrapper.execute(actionMapping, null,
                getRequest(), getResponse());
        assertNotNull(forward);
        assertEquals("/ooo/index.jsp", forward.getPath());
        assertNotNull(getRequest().getAttribute(Globals.ERROR_KEY));
        assertNull(getRequest().getAttribute("aaa"));
    }

    /**
     * @throws Exception
     */
    public void testExecute_validate_actionForm() throws Exception {
        register(KkkAction.class, "kkkAction");
        register(KkkActionDto.class, "kkkActionDto");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("kkkAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/kkk");
        S2ExecuteConfigUtil.setExecuteConfig(actionMapping
                .findExecuteConfig(getRequest()));
        KkkActionDto dto = (KkkActionDto) getComponent(KkkActionDto.class);
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        wrapper.execute(actionMapping, null, getRequest(), getResponse());
        assertTrue(dto.validated);
    }

    /**
     * @throws Exception
     */
    public void testExecute_validate_newActionForm() throws Exception {
        register(LllAction.class, "lllAction");
        register(LllActionForm.class, "lllActionForm");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("lllAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/lll");
        S2ExecuteConfigUtil.setExecuteConfig(actionMapping
                .findExecuteConfig(getRequest()));
        LllActionForm form = (LllActionForm) getComponent(LllActionForm.class);
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        wrapper.execute(actionMapping, null, getRequest(), getResponse());
        assertTrue(form.validated);
    }

    /**
     * @throws Exception
     */
    public void testExecute_validator() throws Exception {
        register(EeeAction.class, "eeeAction");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("eeeAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/eee");
        S2ExecuteConfigUtil.setExecuteConfig(actionMapping
                .findExecuteConfig(getRequest()));
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ForwardConfig forward = wrapper.execute(actionMapping, null,
                getRequest(), getResponse());
        assertNotNull(forward);
        assertEquals("/eee/input.jsp", forward.getPath());
        assertNotNull(getRequest().getAttribute(Globals.ERROR_KEY));
    }

    /**
     * @throws Exception
     */
    public void testExecute_validate_session() throws Exception {
        register(DddAction.class, "dddAction");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("dddAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/ddd");
        S2ExecuteConfigUtil.setExecuteConfig(actionMapping
                .findExecuteConfig(getRequest()));
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ForwardConfig forward = wrapper.execute(actionMapping, null,
                getRequest(), getResponse());
        assertNotNull(forward);
        assertEquals("/ddd/input.jsp", forward.getPath());
        assertNotNull(getRequest().getSession().getAttribute(Globals.ERROR_KEY));
    }

    /**
     * @throws Exception
     */
    public void testExecute_stopOnValidationError() throws Exception {
        register(HhhAction.class, "hhhAction");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("hhhAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/hhh");
        S2ExecuteConfigUtil.setExecuteConfig(actionMapping
                .findExecuteConfig(getRequest()));
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ForwardConfig forward = wrapper.execute(actionMapping, null,
                getRequest(), getResponse());
        assertNotNull(forward);
        assertEquals("/hhh/input.jsp", forward.getPath());
        ActionMessages errors = (ActionMessages) getRequest().getAttribute(
                Globals.ERROR_KEY);
        assertNotNull(errors);
        assertFalse(errors.isEmpty());
        assertEquals(2, errors.size());
        assertNotNull(errors.get("hoge"));
        assertNotNull(errors.get("hoge2"));
    }

    /**
     * @throws Exception
     */
    public void testExecute_notExportPropertiesToRequest() throws Exception {
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("bbbAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/bbb");
        getRequest().setParameter("execute3", "submit");
        S2ExecuteConfigUtil.setExecuteConfig(actionMapping
                .findExecuteConfig(getRequest()));
        BbbAction action = (BbbAction) getComponent("bbbAction");
        action.hoge = "111";
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ForwardConfig forward = wrapper.execute(actionMapping, null,
                getRequest(), getResponse());
        assertNotNull(forward);
        assertEquals("/bbb/execute", forward.getPath());
        assertNull(getRequest().getAttribute("hoge"));
    }

    /**
     * @throws Exception
     */
    public void testExecute_returnNull() throws Exception {
        ActionCustomizer customizer = new ActionCustomizer();
        register(JjjAction.class, "jjjAction");
        customizer.customize(getComponentDef(JjjAction.class));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/jjj");
        S2ExecuteConfigUtil.setExecuteConfig(actionMapping
                .findExecuteConfig(getRequest()));
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        assertNull(wrapper.execute(actionMapping, null, getRequest(),
                getResponse()));
    }

    /**
     * @throws Exception
     */
    public void testExecute_redirect() throws Exception {
        ActionCustomizer customizer = new ActionCustomizer();
        register(MmmAction.class, "mmmAction");
        customizer.customize(getComponentDef(MmmAction.class));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/mmm");
        S2ExecuteConfigUtil.setExecuteConfig(actionMapping
                .findExecuteConfig(getRequest()));
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ForwardConfig forward = wrapper.execute(actionMapping, null,
                getRequest(), getResponse());
        assertTrue(forward.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testExecute_redirect_hasErrors() throws Exception {
        ActionCustomizer customizer = new ActionCustomizer();
        register(MmmAction.class, "mmmAction");
        customizer.customize(getComponentDef(MmmAction.class));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/mmm");
        S2ExecuteConfigUtil.setExecuteConfig(actionMapping
                .findExecuteConfig(getRequest()));
        ActionMessages errors = new ActionMessages();
        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                "errors.invalid", "hoge"));
        ActionMessagesUtil.addErrors(getRequest(), errors);
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ForwardConfig forward = wrapper.execute(actionMapping, null,
                getRequest(), getResponse());
        assertFalse(forward.getRedirect());
    }

    /**
     * @throws Exception
     */
    public void testExecute_removeActionForm_hasErrors() throws Exception {
        register(PppAction.class, "pppAction");
        register(MyForm.class, "myForm");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("pppAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/ppp");
        getRequest().setParameter("submit", "submit");
        S2ExecuteConfigUtil.setExecuteConfig(actionMapping
                .findExecuteConfig(getRequest()));
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        wrapper.execute(actionMapping, null, getRequest(), getResponse());
        assertNotNull(getRequest().getSession().getAttribute("myForm"));
    }

    /**
     * @throws Exception
     */
    public void testValidateUsingValidator() throws Exception {
        register(EeeAction.class, "aaa_eeeAction");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("aaa_eeeAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/aaa/eee");
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ActionMessages errors = wrapper.validateUsingValidator(getRequest(),
                actionMapping.getExecuteConfig("execute"));
        System.out.println(errors);
        assertFalse(errors.isEmpty());
    }

    /**
     * @throws Exception
     */
    public void testValidateUsingValidator_multi() throws Exception {
        register(IiiAction.class, "iiiAction");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("iiiAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/iii");
        IiiAction action = (IiiAction) getComponent(IiiAction.class);
        action.validate = true;
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ActionMessages errors = wrapper.validateUsingValidator(getRequest(),
                actionMapping.getExecuteConfig("execute"));
        assertNotNull(errors.get("hoge2"));
    }

    /**
     * @throws Exception
     */
    public void testValidateUsingValidator_multi2() throws Exception {
        register(IiiAction.class, "iiiAction");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("iiiAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/iii");
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ActionMessages errors = wrapper.validateUsingValidator(getRequest(),
                actionMapping.getExecuteConfig("execute"));
        assertNotNull(errors.get("hoge"));
    }

    /**
     * @throws Exception
     */
    public void testValidateUsingValidator_multi3() throws Exception {
        register(IiiAction.class, "iiiAction");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("iiiAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/iii");
        IiiAction action = (IiiAction) getComponent(IiiAction.class);
        action.hoge = "111";
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ActionMessages errors = wrapper.validateUsingValidator(getRequest(),
                actionMapping.getExecuteConfig("execute"));
        assertNotNull(errors.get("hoge3"));
    }

    /**
     * @throws Exception
     */
    public void testInputForward() throws Exception {
        register(GggAction.class, "aaa_gggAction");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("aaa_gggAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/aaa/ggg");
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ActionForward forward = wrapper.execute(getRequest(), actionMapping
                .getExecuteConfig("execute"));
        assertEquals("/aaa/input2.jsp", forward.getPath());
    }

    /**
     * @throws Exception
     */
    public void testInputForwardForPattern() throws Exception {
        register(GggAction.class, "aaa_gggAction");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("aaa_gggAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/aaa/ggg");
        GggAction action = (GggAction) getComponent("aaa_gggAction");
        action.id = "111";
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        ActionForward forward = wrapper.execute(getRequest(), actionMapping
                .getExecuteConfig("execute2"));
        assertEquals("/edit/111", forward.getPath());
    }

    /**
     * @throws Exception
     */
    public void testRemoveActionForm() throws Exception {
        register(FffAction.class, "fffAction");
        register(MyForm.class, "myForm");
        ActionCustomizer customizer = new ActionCustomizer();
        customizer.customize(getComponentDef("fffAction"));
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig("/fff");
        MyForm myForm = (MyForm) getComponent("myForm");
        myForm.aaa = "111";
        assertNotNull(getRequest().getSession().getAttribute("myForm"));
        ActionWrapper wrapper = new ActionWrapper(actionMapping);
        wrapper
                .execute(getRequest(), actionMapping
                        .getExecuteConfig("execute"));
        assertNull(getRequest().getSession().getAttribute("myForm"));
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
        public HttpServletRequest request;

        /**
         * 
         */
        public Map<String, Object> requestScope;

        /**
         * @return
         */
        @Execute(validator = false)
        public String index() {
            hoge = "111";
            return "index.jsp";
        }

        /**
         * @return
         */
        @Execute(validator = false)
        public String execute2() {
            return "execute2.jsp";
        }

        /**
         * @return
         */
        @Execute(validator = false)
        public String execute3() {
            return "execute";
        }
    }

    /**
     * 
     */
    public static class CccAction {

        /**
         * 
         */
        public String aaa;

        /**
         * @return
         */
        @Execute(validate = "validate", input = "input")
        public String execute() {
            return "result.jsp";
        }

        /**
         * @return
         */
        @Execute(validator = false)
        public String input() {
            return "input.jsp";
        }

        /**
         * @return
         */
        public ActionMessages validate() {
            ActionMessages errors = new ActionMessages();
            errors.add("hoge", new ActionMessage("errors.required", "hoge"));
            return errors;
        }
    }

    /**
     * 
     */
    public static class DddAction {

        /**
         * @return
         */
        @Execute(validate = "validate", saveErrors = SaveType.SESSION, input = "input.jsp")
        public String execute() {
            return "result.jsp";
        }

        /**
         * @return
         */
        public ActionMessages validate() {
            ActionMessages errors = new ActionMessages();
            errors.add("hoge", new ActionMessage("errors.required", "hoge"));
            return errors;
        }
    }

    /**
     * 
     */
    public static class EeeAction {

        /**
         * 
         */
        @Required
        public String hoge;

        /**
         * @return
         */
        @Execute(input = "input.jsp")
        public String execute() {
            return "success";
        }
    }

    /**
     * 
     */
    public static class FffAction {

        /**
         * 
         */
        @ActionForm
        public MyForm myForm;

        /**
         * 
         */
        public String hoge;

        /**
         * @return
         */
        @Execute(validator = false, removeActionForm = true)
        public String execute() {
            return "/aaa/bbb.jsp";
        }
    }

    /**
     * 
     */
    public static class GggAction {

        /**
         * 
         */
        public String id;

        /**
         * 
         */
        @Required
        public String hoge;

        /**
         * @return
         */
        @Execute(input = "/aaa/input2.jsp")
        public String execute() {
            return "execute.jsp";
        }

        /**
         * @return
         */
        @Execute(input = "/edit/{id}")
        public String execute2() {
            return "execute2.jsp";
        }
    }

    /**
     * 
     */
    public static class HhhAction {

        /**
         * 
         */
        @Required
        public String hoge;

        /**
         * @return
         */
        @Execute(validate = "validate", input = "input.jsp", stopOnValidationError = false)
        public String execute() {
            return "result.jsp";
        }

        /**
         * @return
         */
        public ActionMessages validate() {
            ActionMessages errors = new ActionMessages();
            errors.add("hoge2", new ActionMessage("errors.required", "hoge"));
            return errors;
        }
    }

    /**
     * 
     */
    public static class IiiAction {

        boolean validate = false;

        /**
         * 
         */
        @Required
        public String hoge;

        /**
         * @return
         */
        @Execute(validator = true, validate = "validate, @, validate2", input = "input.jsp")
        public String execute() {
            return "result.jsp";
        }

        /**
         * @return
         */
        public ActionMessages validate() {
            ActionMessages errors = new ActionMessages();
            if (validate) {
                errors.add("hoge2",
                        new ActionMessage("errors.required", "hoge"));
            }
            return errors;
        }

        /**
         * @return
         */
        public ActionMessages validate2() {
            ActionMessages errors = new ActionMessages();
            errors.add("hoge3", new ActionMessage("errors.required", "hoge"));
            return errors;
        }
    }

    /**
     * 
     */
    public static class JjjAction {

        /**
         * @return
         */
        @Execute(validator = false)
        public String index() {
            return null;
        }
    }

    /**
     * 
     */
    public static class KkkAction {

        /**
         * 
         */
        @ActionForm
        @Resource
        protected KkkActionDto kkkActionDto;

        /**
         * @return
         */
        @Execute(validator = true, validate = "validate", input = "index.jsp")
        public String index() {
            return null;
        }
    }

    /**
     * 
     */
    public static class LllAction {

        /**
         * 
         */
        @ActionForm
        @Resource
        protected LllActionForm lllActionForm;

        /**
         * @return
         */
        @Execute(validator = true, validate = "validate", input = "index.jsp")
        public String index() {
            return null;
        }
    }

    /**
     * 
     */
    public static class MmmAction {

        /**
         * @return
         */
        @Execute(validator = false, redirect = true)
        public String index() {
            return "index.jsp";
        }
    }

    /**
     * 
     */
    public abstract static class NnnAction {

        /**
         * @return
         */
        public ActionMessages validate() {
            ActionMessages errors = new ActionMessages();
            errors.add("hoge", new ActionMessage("errors.required", "hoge"));
            return errors;
        }
    }

    /**
     * 
     */
    public static class OooAction extends NnnAction {

        /**
         * 
         */
        @ActionForm
        public MyForm myForm;

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
        @Execute(validate = "validate", input = "index.jsp", removeActionForm = true)
        public String execute() {
            return "result.jsp";
        }
    }

    /**
     * 
     */
    public static class PppAction {

        /**
         * 
         */
        @ActionForm
        public MyForm myForm;

        /**
         * @return
         */
        @Execute(validator = false, redirect = true, removeActionForm = true)
        public String submit() {
            ActionMessages errors = new ActionMessages();
            errors.add("aaa", new ActionMessage("errors.required", "hoge"));
            ActionMessagesUtil.addErrors(RequestUtil.getRequest(), errors);
            return "index.jsp";
        }
    }

    private static class MyActionServlet extends ActionServlet {
        private static final long serialVersionUID = 1L;

        private ServletContext servletContext;

        /**
         * @param servletContext
         */
        public MyActionServlet(ServletContext servletContext) {
            super();
            this.servletContext = servletContext;
        }

        @Override
        public ServletContext getServletContext() {
            return servletContext;
        }
    }

    /**
     * 
     */
    @Component(instance = InstanceType.SESSION)
    public static class MyForm {
        /**
         * 
         */
        public String aaa;
    }

    /**
     * 
     */
    public static class KkkActionDto {

        boolean validated = false;

        /**
         * @return
         * 
         */
        public ActionMessages validate() {
            validated = true;
            return null;
        }
    }

    /**
     * 
     */
    public static class LllActionForm {

        boolean validated = false;

        /**
         * @return
         * 
         */
        public ActionMessages validate() {
            validated = true;
            return null;
        }
    }
}