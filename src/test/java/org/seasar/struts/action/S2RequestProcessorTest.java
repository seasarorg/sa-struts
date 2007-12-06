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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.upload.CommonsMultipartRequestHandler;
import org.apache.struts.upload.MultipartRequestHandler;
import org.apache.struts.upload.MultipartRequestWrapper;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.mock.servlet.MockHttpServletRequest;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2FormBeanConfig;
import org.seasar.struts.config.S2ModuleConfig;

/**
 * @author higa
 * 
 */
public class S2RequestProcessorTest extends S2TestCase {

    @Override
    public void setUp() throws Exception {
        register(BbbAction.class, "aaa_bbbAction");
    }

    /**
     * @throws Exception
     */
    public void testProcessMultipart() throws Exception {
        MockHttpServletRequest request = getRequest();
        request.setMethod("POST");
        request.setContentType("multipart/form-data");
        S2RequestProcessor processor = new S2RequestProcessor();
        HttpServletRequest req = processor.processMultipart(request);
        assertSame(req, getContainer().getExternalContext().getRequest());
    }

    /**
     * @throws Exception
     */
    public void testProcessActionCreate() throws Exception {
        S2ActionMapping mapping = new S2ActionMapping();
        mapping.setComponentDef(getComponentDef("aaa_bbbAction"));
        S2RequestProcessor processor = new S2RequestProcessor();
        S2ModuleConfig moduleConfig = new S2ModuleConfig("");
        processor.init(new ActionServlet(), moduleConfig);
        Action action = processor.processActionCreate(getRequest(),
                getResponse(), mapping);
        assertNotNull(action);
        assertEquals(ActionWrapper.class, action.getClass());
        assertNotNull(action.getServlet());
    }

    /**
     * @throws Exception
     */
    public void testProcessActionForm() throws Exception {
        S2ActionMapping mapping = new S2ActionMapping();
        mapping.setName("aaa_bbbActionForm");
        mapping.setComponentDef(getComponentDef("aaa_bbbAction"));
        S2RequestProcessor processor = new S2RequestProcessor();

        S2ModuleConfig moduleConfig = new S2ModuleConfig("");
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                mapping);
        S2FormBeanConfig formConfig = new S2FormBeanConfig();
        formConfig.setName("aaa_bbbActionForm");
        formConfig.setDynaClass(wrapperClass);
        moduleConfig.addFormBeanConfig(formConfig);
        processor.init(new ActionServlet(), moduleConfig);
        ActionForm actionForm = processor.processActionForm(getRequest(),
                getResponse(), mapping);
        assertNotNull(actionForm);
        assertEquals(ActionFormWrapper.class, actionForm.getClass());
        assertNotNull(getRequest().getAttribute("aaa_bbbActionForm"));
    }

    /**
     * @throws Exception
     */
    public void testGetMultipartHandler_request() throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        assertNotNull(processor
                .getMultipartHandler("org.apache.struts.upload.CommonsMultipartRequestHandler"));
    }

    /**
     * @throws Exception
     */
    public void testGetMultipartHandler_moduleConfig() throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        S2ModuleConfig moduleConfig = new S2ModuleConfig("");
        processor.init(null, moduleConfig);
        assertNotNull(processor.getMultipartHandler(null));
    }

    /**
     * @throws Exception
     */
    public void testGetAllParameterNamesForMultipartRequest() throws Exception {
        MultipartRequestHandler multipartHandler = new CommonsMultipartRequestHandler() {

            @SuppressWarnings("unchecked")
            @Override
            public Hashtable getAllElements() {
                Hashtable elements = new Hashtable();
                elements.put("aaa", "111");
                return elements;
            }

        };
        getRequest().addParameter("bbb", "222");
        HttpServletRequest request = new MultipartRequestWrapper(getRequest());
        S2RequestProcessor processor = new S2RequestProcessor();
        Iterator<String> i = processor.getAllParameterNamesForMultipartRequest(
                request, multipartHandler);
        assertEquals("aaa", i.next());
        assertEquals("bbb", i.next());
        assertFalse(i.hasNext());
    }

    /**
     * @throws Exception
     */
    public void testProcessPopulate_reset() throws Exception {
        register(BbbAction.class, "bbbAction");
        BbbAction actionForm = (BbbAction) getComponent("bbbAction");
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setComponentDef(getComponentDef("bbbAction"));
        actionMapping.setResetMethod(BbbAction.class.getMethod("reset"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                actionMapping);
        ActionFormWrapper formWrapper = new ActionFormWrapper(wrapperClass);
        S2RequestProcessor processor = new S2RequestProcessor();
        processor.processPopulate(getRequest(), getResponse(), formWrapper,
                actionMapping);
        assertEquals("aaa", actionForm.hoge);
    }

    /**
     * @throws Exception
     */
    public void testPopulateSimpleProperty_ignore() throws Exception {
        BbbAction bean = new BbbAction();
        S2RequestProcessor processor = new S2RequestProcessor();
        processor.populate(bean, "xxx", null);
    }

    /**
     * @throws Exception
     */
    public void testPopulateSimpleProperty_array() throws Exception {
        BbbAction bean = new BbbAction();
        S2RequestProcessor processor = new S2RequestProcessor();
        processor.populate(bean, "hogeArray", new String[] { "111" });
        assertEquals(1, bean.hogeArray.length);
        assertEquals("111", bean.hogeArray[0]);
    }

    /**
     * @throws Exception
     */
    public void testPopulateSimpleProperty_list() throws Exception {
        BbbAction bean = new BbbAction();
        S2RequestProcessor processor = new S2RequestProcessor();
        processor.populate(bean, "hogeList", new String[] { "111" });
        assertEquals(1, bean.hogeList.size());
        assertEquals("111", bean.hogeList.get(0));
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
        public String[] hogeArray;

        /**
         * 
         */
        public List<String> hogeList;

        /**
         * 
         */
        public void reset() {
            hoge = "aaa";
        }
    }
}