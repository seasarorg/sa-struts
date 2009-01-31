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
package org.seasar.struts.filter;

import org.apache.struts.Globals;
import org.apache.struts.util.MessageResourcesFactory;
import org.apache.struts.validator.ValidatorPlugIn;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.mock.servlet.MockHttpServletRequestImpl;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.config.S2ModuleConfig;
import org.seasar.struts.customizer.ActionCustomizer;
import org.seasar.struts.util.S2PropertyMessageResourcesFactory;
import org.seasar.struts.validator.S2ValidatorResources;

/**
 * @author higa
 * 
 */
public class RoutingFilterTest extends S2TestCase {

    private ActionCustomizer customizer = new ActionCustomizer();

    private S2ModuleConfig moduleConfig = new S2ModuleConfig("");

    private S2ValidatorResources validatorResources = new S2ValidatorResources();

    @Override
    public void setUpAfterContainerInit() {
        getServletContext().setAttribute(Globals.SERVLET_KEY, "/*");
        getServletContext().setAttribute(Globals.MODULE_KEY, moduleConfig);
        MessageResourcesFactory mrf = new S2PropertyMessageResourcesFactory();
        getServletContext().setAttribute(Globals.MESSAGES_KEY,
                mrf.createResources("SASMessages"));
        getServletContext().setAttribute(ValidatorPlugIn.VALIDATOR_KEY,
                validatorResources);

    }

    /**
     * @throws Exception
     */
    public void testDoFilter() throws Exception {
        register(AaaAction.class, "aaaAction");
        customizer.customize(getComponentDef("aaaAction"));
        RoutingFilter filter = new RoutingFilter();
        ((MockHttpServletRequestImpl) getRequest()).setPathInfo("/aaa");
        filter.doFilter(getRequest(), getResponse(), null);
    }

    /**
     * @throws Exception
     */
    public void testDoFilter_param() throws Exception {
        register(AaaAction.class, "aaaAction");
        customizer.customize(getComponentDef("aaaAction"));
        RoutingFilter filter = new RoutingFilter();
        ((MockHttpServletRequestImpl) getRequest()).setPathInfo("/aaa/edit/1");
        filter.doFilter(getRequest(), getResponse(), null);
    }

    /**
     * @throws Exception
     */
    public void testDoFilter_indexAction() throws Exception {
        register(IndexAction.class, "indexAction");
        customizer.customize(getComponentDef("indexAction"));
        RoutingFilter filter = new RoutingFilter();
        ((MockHttpServletRequestImpl) getRequest()).setPathInfo("/");
        filter.doFilter(getRequest(), getResponse(), null);
    }

    /**
     * @throws Exception
     */
    public void testDoFilter_indexAction_param() throws Exception {
        register(IndexAction.class, "indexAction");
        customizer.customize(getComponentDef("indexAction"));
        RoutingFilter filter = new RoutingFilter();
        ((MockHttpServletRequestImpl) getRequest()).setPathInfo("/hoge/edit");
        filter.doFilter(getRequest(), getResponse(), null);
    }

    /**
     * @throws Exception
     */
    public void testDoFilter_nestedIndexAction() throws Exception {
        register(IndexAction.class, "aaa_indexAction");
        customizer.customize(getComponentDef("aaa_indexAction"));
        RoutingFilter filter = new RoutingFilter();
        ((MockHttpServletRequestImpl) getRequest()).setPathInfo("/aaa");
        filter.doFilter(getRequest(), getResponse(), null);
    }

    /**
     * @throws Exception
     */
    public void testDoFilter_nestedIndexAction_param() throws Exception {
        register(IndexAction.class, "aaa_indexAction");
        customizer.customize(getComponentDef("aaa_indexAction"));
        RoutingFilter filter = new RoutingFilter();
        ((MockHttpServletRequestImpl) getRequest()).setPathInfo("/aaa/1/edit");
        filter.doFilter(getRequest(), getResponse(), null);
    }

    /**
     * 
     */
    public static class AaaAction {
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
        @Execute(validator = false, urlPattern = "edit/{id}")
        public String edit() {
            return "edit.jsp";
        }
    }

    /**
     * 
     */
    public static class IndexAction {
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
        @Execute(validator = false, urlPattern = "{id}/edit")
        public String edit() {
            return "edit.jsp";
        }
    }
}