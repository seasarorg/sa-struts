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

import java.util.ArrayList;
import java.util.Arrays;
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
import org.seasar.framework.aop.Aspect;
import org.seasar.framework.aop.Pointcut;
import org.seasar.framework.aop.impl.AspectImpl;
import org.seasar.framework.aop.impl.PointcutImpl;
import org.seasar.framework.aop.interceptors.TraceInterceptor;
import org.seasar.framework.aop.proxy.AopProxy;
import org.seasar.framework.mock.servlet.MockHttpServletRequest;
import org.seasar.struts.action.S2RequestProcessor.IndexParsedResult;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2FormBeanConfig;
import org.seasar.struts.config.S2ModuleConfig;
import org.seasar.struts.exception.IndexedPropertyNotListArrayRuntimeException;
import org.seasar.struts.exception.NoParameterizedListRuntimeException;

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
    public void testSetProperty_simple() throws Exception {
        BbbAction bean = new BbbAction();
        S2RequestProcessor processor = new S2RequestProcessor();
        processor.setProperty(bean, "hoge", new String[] { "111" });
        assertEquals("111", bean.hoge);
    }

    /**
     * @throws Exception
     */
    public void testSetProperty_nested() throws Exception {
        BbbAction bean = new BbbAction();
        S2RequestProcessor processor = new S2RequestProcessor();
        processor.setProperty(bean, "myBean.aaa", new String[] { "111" });
        assertEquals("111", bean.myBean.aaa);
    }

    /**
     * @throws Exception
     */
    public void testSetProperty_indexed_nested() throws Exception {
        BbbAction bean = new BbbAction();
        S2RequestProcessor processor = new S2RequestProcessor();
        processor.setProperty(bean, "myBeanArrayArray[1][1].aaa",
                new String[] { "111" });
        assertEquals("111", bean.myBeanArrayArray[1][1].aaa);
    }

    /**
     * @throws Exception
     */
    public void testSetProperty_illegal() throws Exception {
        BbbAction bean = new BbbAction();
        S2RequestProcessor processor = new S2RequestProcessor();
        try {
            processor.setProperty(bean, "myBeanArrayArray[1][1]",
                    new String[] { "111" });
            fail();
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
    }

    /**
     * @throws Exception
     */
    public void testSetSimpleProperty() throws Exception {
        BbbAction bean = new BbbAction();
        S2RequestProcessor processor = new S2RequestProcessor();
        processor.setSimpleProperty(bean, "hoge", new String[] { "111" });
        assertEquals("111", bean.hoge);
    }

    /**
     * @throws Exception
     */
    public void testSetSimpleProperty_ignore() throws Exception {
        BbbAction bean = new BbbAction();
        S2RequestProcessor processor = new S2RequestProcessor();
        processor.setSimpleProperty(bean, "xxx", null);
    }

    /**
     * @throws Exception
     */
    public void testSetSimpleProperty_array() throws Exception {
        BbbAction bean = new BbbAction();
        S2RequestProcessor processor = new S2RequestProcessor();
        processor.setSimpleProperty(bean, "hogeArray", new String[] { "111" });
        assertEquals(1, bean.hogeArray.length);
        assertEquals("111", bean.hogeArray[0]);
    }

    /**
     * @throws Exception
     */
    public void testSetSimpleProperty_list() throws Exception {
        BbbAction bean = new BbbAction();
        S2RequestProcessor processor = new S2RequestProcessor();
        processor.setSimpleProperty(bean, "hogeList", new String[] { "111" });
        assertEquals(1, bean.hogeList.size());
        assertEquals("111", bean.hogeList.get(0));
    }

    /**
     * @throws Exception
     */
    public void testSetSimpleProperty_empty() throws Exception {
        BbbAction bean = new BbbAction();
        bean.hoge = "111";
        S2RequestProcessor processor = new S2RequestProcessor();
        processor.setSimpleProperty(bean, "hoge", new String[0]);
        assertNull(bean.hoge);
    }

    /**
     * @throws Exception
     */
    public void testGetSimpleProperty_notNull() throws Exception {
        BbbAction bean = new BbbAction();
        MyBean myBean = new MyBean();
        bean.myBean = myBean;
        S2RequestProcessor processor = new S2RequestProcessor();
        assertSame(myBean, processor.getSimpleProperty(bean, "myBean"));
    }

    /**
     * @throws Exception
     */
    public void testGetSimpleProperty_null() throws Exception {
        BbbAction bean = new BbbAction();
        S2RequestProcessor processor = new S2RequestProcessor();
        assertNotNull(processor.getSimpleProperty(bean, "myBean"));
        assertNotNull(bean.myBean);
    }

    /**
     * @throws Exception
     */
    public void testParseIndex() throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        IndexParsedResult result = processor.parseIndex("12].aaa");
        assertEquals(1, result.indexes.length);
        assertEquals(12, result.indexes[0]);
        assertEquals("aaa", result.name);
    }

    /**
     * @throws Exception
     */
    public void testParseIndex_nest() throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        IndexParsedResult result = processor.parseIndex("12][34].aaa");
        assertEquals(2, result.indexes.length);
        assertEquals(12, result.indexes[0]);
        assertEquals(34, result.indexes[1]);
        assertEquals("aaa", result.name);
    }

    /**
     * @throws Exception
     */
    public void testParseIndex_end() throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        IndexParsedResult result = processor.parseIndex("12]");
        assertEquals(1, result.indexes.length);
        assertEquals(12, result.indexes[0]);
        assertEquals("", result.name);
    }

    /**
     * @throws Exception
     */
    public void testGetIndexedProperty_array() throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        BbbAction bean = new BbbAction();
        MyBean result = (MyBean) processor.getIndexedProperty(bean,
                "myBeanArray", new int[] { 0 });
        assertNotNull(result);
        assertEquals(1, bean.myBeanArray.length);
    }

    /**
     * @throws Exception
     */
    public void testGetIndexedProperty_array_nest() throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        BbbAction bean = new BbbAction();
        MyBean myBean = new MyBean();
        myBean.aaa = "111";
        bean.myBeanArrayArray = new MyBean[][] { new MyBean[] { myBean } };
        MyBean result = (MyBean) processor.getIndexedProperty(bean,
                "myBeanArrayArray", new int[] { 1, 2 });
        assertNotNull(result);
        assertEquals(2, bean.myBeanArrayArray.length);
        assertEquals(1, bean.myBeanArrayArray[0].length);
        assertEquals("111", bean.myBeanArrayArray[0][0].aaa);
        assertEquals(3, bean.myBeanArrayArray[1].length);
    }

    /**
     * @throws Exception
     */
    public void testGetIndexedProperty_list() throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        BbbAction bean = new BbbAction();
        MyBean result = (MyBean) processor.getIndexedProperty(bean,
                "myBeanList", new int[] { 0 });
        assertNotNull(result);
        assertEquals(1, bean.myBeanList.size());
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testGetIndexedProperty_list_nest() throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        BbbAction bean = new BbbAction();
        MyBean myBean = new MyBean();
        myBean.aaa = "111";
        bean.myBeanListList = new ArrayList<List<MyBean>>();
        bean.myBeanListList.add(Arrays.asList(myBean));
        MyBean result = (MyBean) processor.getIndexedProperty(bean,
                "myBeanListList", new int[] { 1, 2 });
        assertNotNull(result);
        assertEquals(2, bean.myBeanListList.size());
        assertEquals(1, bean.myBeanListList.get(0).size());
        assertEquals("111", bean.myBeanListList.get(0).get(0).aaa);
        assertEquals(3, bean.myBeanListList.get(1).size());
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testGetIndexedProperty_list_nest_notParameterizedList()
            throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        BbbAction bean = new BbbAction();
        try {
            processor
                    .getIndexedProperty(bean, "myBeanList", new int[] { 1, 2 });
            fail();
        } catch (NoParameterizedListRuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testGetIndexedProperty_notListArray() throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        BbbAction bean = new BbbAction();
        try {
            processor.getIndexedProperty(bean, "hoge", new int[] { 1, 2 });
            fail();
        } catch (IndexedPropertyNotListArrayRuntimeException e) {
            System.out.println(e.getMessage());
            assertEquals(BbbAction.class, e.getTargetClass());
            assertEquals("hoge", e.getPropertyName());
        }
    }

    /**
     * @throws Exception
     */
    public void testExpand() throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        int[] result = (int[]) processor.expand(new int[] { 1 },
                new int[] { 1 }, int.class);
        assertEquals(2, result.length);
        assertEquals(1, result[0]);
    }

    /**
     * @throws Exception
     */
    public void testExpand_nest() throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        int[][] result = (int[][]) processor.expand(
                new int[][] { new int[] { 1 } }, new int[] { 1, 2 }, int.class);
        assertEquals(2, result.length);
        assertEquals(1, result[0].length);
        assertEquals(1, result[0][0]);
        assertEquals(3, result[1].length);
    }

    /**
     * @throws Exception
     */
    public void testExpand_nest_bean() throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        MyBean[][] result = (MyBean[][]) processor.expand(
                new MyBean[][] { new MyBean[] { new MyBean() } }, new int[] {
                        1, 2 }, MyBean.class);
        assertEquals(2, result.length);
        assertEquals(1, result[0].length);
        assertNotNull(result[0][0]);
        assertEquals(3, result[1].length);
    }

    /**
     * @throws Exception
     */
    public void testGetArrayElementType() throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        assertEquals(MyBean.class, processor.getArrayElementType(
                new MyBean[0][0].getClass(), 2));
    }

    /**
     * @throws Exception
     */
    public void testFillArrayValue() throws Exception {
        S2RequestProcessor processor = new S2RequestProcessor();
        assertEquals(1, processor.fillArrayValue(
                new int[][] { new int[] { 1 } }, new int[] { 0, 0 }, int.class));
        assertNotNull(processor.fillArrayValue(
                new MyBean[][] { new MyBean[] { null } }, new int[] { 0, 0 },
                MyBean.class));
    }

    /**
     * @throws Exception
     */
    public void testGetRealClass() throws Exception {
        Pointcut pointcut = new PointcutImpl(new String[] { "execte" });
        Aspect aspect = new AspectImpl(new TraceInterceptor(), pointcut);
        AopProxy aopProxy = new AopProxy(BbbAction.class,
                new Aspect[] { aspect });
        BbbAction proxy = (BbbAction) aopProxy.create();
        System.out.println(proxy.getClass());
        S2RequestProcessor processor = new S2RequestProcessor();
        assertEquals(BbbAction.class, processor.getRealClass(proxy.getClass()));
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
        public MyBean myBean;

        /**
         * 
         */
        public MyBean[] myBeanArray;

        /**
         * 
         */
        public MyBean[][] myBeanArrayArray;

        /**
         * 
         */
        public List<MyBean> myBeanList;

        /**
         * 
         */
        public List<List<MyBean>> myBeanListList;

        /**
         * @return
         */
        public String execute() {
            return "success";
        }

        /**
         * 
         */
        public void reset() {
            hoge = "aaa";
        }
    }

    /**
     * 
     */
    public static class MyBean {
        /**
         * 
         */
        public String aaa;

    }
}