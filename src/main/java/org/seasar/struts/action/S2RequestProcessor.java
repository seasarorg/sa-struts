/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.upload.MultipartRequestHandler;
import org.apache.struts.upload.MultipartRequestWrapper;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.EnumerationIterator;
import org.seasar.framework.util.ModifierUtil;
import org.seasar.struts.config.S2ActionMapping;

/**
 * Seasar2用のリクエストプロセッサです。
 * 
 * @author higa
 */
public class S2RequestProcessor extends RequestProcessor {

    private static final char NESTED_DELIM = '.';

    private static final char INDEXED_DELIM = '[';

    private static final char INDEXED_DELIM2 = ']';

    @Override
    public HttpServletRequest processMultipart(HttpServletRequest request) {
        HttpServletRequest result = super.processMultipart(request);
        SingletonS2ContainerFactory.getContainer().getExternalContext()
                .setRequest(result);
        return result;
    }

    @Override
    protected ActionForm processActionForm(HttpServletRequest request,
            HttpServletResponse response, ActionMapping mapping) {

        String name = mapping.getName();
        if (name == null) {
            return null;
        }
        FormBeanConfig formConfig = moduleConfig.findFormBeanConfig(name);
        if (formConfig == null) {
            return null;
        }
        ActionForm actionForm = null;
        try {
            actionForm = formConfig.createActionForm(servlet);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            return null;
        }
        if ("request".equals(mapping.getScope())) {
            request.setAttribute(mapping.getAttribute(), actionForm);
        } else {
            HttpSession session = request.getSession();
            session.setAttribute(mapping.getAttribute(), actionForm);
        }
        return actionForm;

    }

    @Override
    protected Action processActionCreate(HttpServletRequest request,
            HttpServletResponse response, ActionMapping mapping)
            throws IOException {

        Action action = null;
        try {
            action = new ActionWrapper(((S2ActionMapping) mapping));
        } catch (Exception e) {
            log.error(getInternal().getMessage("actionCreate",
                    mapping.getPath()), e);
            response
                    .sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            getInternal().getMessage("actionCreate",
                                    mapping.getPath()));
            return null;
        }
        action.setServlet(servlet);
        return action;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void processPopulate(HttpServletRequest request,
            HttpServletResponse response, ActionForm form, ActionMapping mapping)
            throws ServletException {

        if (form == null) {
            return;
        }
        form.setServlet(servlet);
        form.reset(mapping, request);
        Iterator<String> names = null;
        String contentType = request.getContentType();
        String method = request.getMethod();
        boolean isMultipart = false;
        form.setMultipartRequestHandler(null);
        MultipartRequestHandler multipartHandler = null;
        if (contentType != null
                && contentType.startsWith("multipart/form-data")
                && method.equalsIgnoreCase("POST")) {
            multipartHandler = getMultipartHandler(mapping.getMultipartClass());
            if (multipartHandler != null) {
                isMultipart = true;
                multipartHandler.setServlet(servlet);
                multipartHandler.setMapping(mapping);
                multipartHandler.handleRequest(request);
                Boolean maxLengthExceeded = (Boolean) request
                        .getAttribute(MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED);
                if ((maxLengthExceeded != null)
                        && (maxLengthExceeded.booleanValue())) {
                    form.setMultipartRequestHandler(multipartHandler);
                    return;
                }
                names = getAllParameterNamesForMultipartRequest(request,
                        multipartHandler);
            }
        }
        if (!isMultipart) {
            names = new EnumerationIterator(request.getParameterNames());
        }
        while (names.hasNext()) {
            String name = names.next();
            S2ActionMapping actionMapping = (S2ActionMapping) mapping;
            populate(actionMapping.getActionForm(), name, request
                    .getParameterValues(name));
        }
    }

    /**
     * アクションフォームにリクエストの値を設定します。
     * 
     * @param bean
     *            JavaBeans
     * @param name
     *            パラメータ名
     * @param values
     *            値の配列
     * @throws ServletException
     *             何か例外が発生した場合。
     */
    protected void populate(Object bean, String name, String[] values) {
        int nestedIndex = name.indexOf(NESTED_DELIM);
        int indexedIndex = name.indexOf(INDEXED_DELIM);
        if (nestedIndex < 0 && indexedIndex < 0) {
            populateSimpleProperty(bean, name, values);
        }
    }

    /**
     * アクションフォームにシンプルにリクエストの値を設定します。
     * 
     * @param bean
     *            JavaBeans
     * @param name
     *            パラメータ名
     * @param values
     *            値の配列
     * @throws ServletException
     *             何か例外が発生した場合。
     */
    @SuppressWarnings("unchecked")
    protected void populateSimpleProperty(Object bean, String name,
            String[] values) {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(bean.getClass());
        if (!beanDesc.hasPropertyDesc(name)) {
            return;
        }
        PropertyDesc pd = beanDesc.getPropertyDesc(name);
        if (pd.getPropertyType().isArray()) {
            if (pd.isWritable()) {
                pd.setValue(bean, values);
            }
        } else if (List.class.isAssignableFrom(pd.getPropertyType())) {
            List<String> list = ModifierUtil.isAbstract(pd.getPropertyType()) ? new ArrayList<String>()
                    : (List<String>) ClassUtil
                            .newInstance(pd.getPropertyType());
            list.addAll(Arrays.asList(values));
            pd.setValue(bean, list);
        }
    }

    /**
     * マルチパートリクエストハンドラを返します。
     * 
     * @param multipartClass
     *            マルチパートリクエストハンドラのクラス名
     * @return マルチパートリクエストハンドラ
     * @throws ServletException
     *             何か例外が発生した場合。
     */
    protected MultipartRequestHandler getMultipartHandler(String multipartClass)
            throws ServletException {

        MultipartRequestHandler multipartHandler = null;
        if (multipartClass != null) {
            try {
                multipartHandler = (MultipartRequestHandler) ClassUtil
                        .newInstance(multipartClass);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                throw new ServletException(t.getMessage(), t);
            }
            if (multipartHandler != null) {
                return multipartHandler;
            }
        }
        multipartClass = moduleConfig.getControllerConfig().getMultipartClass();
        if (multipartClass != null) {
            try {
                multipartHandler = (MultipartRequestHandler) ClassUtil
                        .newInstance(multipartClass);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                throw new ServletException(t.getMessage(), t);
            }
            if (multipartHandler != null) {
                return multipartHandler;
            }
        }
        return null;
    }

    /**
     * マルチパート用のパラメータを返します。
     * 
     * @param request
     *            リクエスト
     * @param multipartHandler
     *            マルチパートリクエストハンドラ
     * @return マルチパート用のパラメータ
     */
    @SuppressWarnings("unchecked")
    protected Iterator<String> getAllParameterNamesForMultipartRequest(
            HttpServletRequest request, MultipartRequestHandler multipartHandler) {
        Set<String> names = new LinkedHashSet<String>();
        Hashtable elements = multipartHandler.getAllElements();
        Enumeration e = elements.keys();
        while (e.hasMoreElements()) {
            names.add((String) e.nextElement());
        }
        if (request instanceof MultipartRequestWrapper) {
            request = ((MultipartRequestWrapper) request).getRequest();
            e = request.getParameterNames();
            while (e.hasMoreElements()) {
                names.add((String) e.nextElement());
            }
        }
        return names.iterator();
    }
}