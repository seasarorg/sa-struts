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

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.InvalidCancelException;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.upload.MultipartRequestHandler;
import org.apache.struts.upload.MultipartRequestWrapper;
import org.seasar.framework.aop.javassist.AspectWeaver;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.IllegalPropertyRuntimeException;
import org.seasar.framework.beans.ParameterizedClassDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.ArrayUtil;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.ModifierUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.exception.IndexedPropertyNotListArrayRuntimeException;
import org.seasar.struts.exception.NoParameterizedListRuntimeException;
import org.seasar.struts.exception.NoRoleRuntimeException;
import org.seasar.struts.util.ActionFormUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.seasar.struts.util.S2ActionMappingUtil;
import org.seasar.struts.util.S2ExecuteConfigUtil;

/**
 * Seasar2用のリクエストプロセッサです。
 * 
 * @author higa
 */
public class S2RequestProcessor extends RequestProcessor {

    private static final char NESTED_DELIM = '.';

    private static final char INDEXED_DELIM = '[';

    private static final char INDEXED_DELIM2 = ']';

    private static final char MAPPED_DELIM = '(';

    private static final char MAPPED_DELIM2 = ')';

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request = processMultipart(request);
        String path = processPath(request, response);
        if (path == null) {
            return;
        }
        processLocale(request, response);
        processContent(request, response);
        processNoCache(request, response);
        if (!processPreprocess(request, response)) {
            return;
        }
        processCachedMessages(request, response);
        ActionMapping mapping = processMapping(request, response, path);
        if (mapping == null) {
            return;
        }
        ActionForm form = processActionForm(request, response, mapping);
        processPopulate(request, response, form, mapping);
        if (!processRoles(request, response, mapping)) {
            return;
        }
        try {
            if (!processValidate(request, response, form, mapping)) {
                return;
            }
        } catch (InvalidCancelException e) {
            ActionForward forward = processException(request, response, e,
                    form, mapping);
            processForwardConfig(request, response, forward);
            return;
        } catch (IOException e) {
            throw e;
        } catch (ServletException e) {
            throw e;
        }
        if (!processForward(request, response, mapping)) {
            return;
        }
        if (!processInclude(request, response, mapping)) {
            return;
        }
        Action action = processActionCreate(request, response, mapping);
        if (action == null) {
            return;
        }
        ActionForward forward = processActionPerform(request, response, action,
                form, mapping);
        processForwardConfig(request, response, forward);
    }

    @Override
    protected ActionMapping processMapping(HttpServletRequest request,
            HttpServletResponse response, String path) throws IOException {
        S2ActionMapping mapping = (S2ActionMapping) moduleConfig
                .findActionConfig(path);
        if (mapping != null) {
            request.setAttribute(Globals.MAPPING_KEY, mapping);
            return mapping;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND, path
                + " not found.");
        return null;
    }

    /**
     * 実行設定を処理します。
     * 
     * @param request
     *            リクエスト
     * @param response
     *            レスポンス
     * @param mapping
     *            アクションマッピング
     */
    protected void processExecuteConfig(HttpServletRequest request,
            HttpServletResponse response, ActionMapping mapping) {
        S2ExecuteConfig executeConfig = ((S2ActionMapping) mapping)
                .findExecuteConfig(request);
        S2ExecuteConfigUtil.setExecuteConfig(executeConfig);
    }

    @Override
    protected boolean processRoles(HttpServletRequest request,
            HttpServletResponse response, ActionMapping mapping)
            throws IOException, ServletException {
        S2ExecuteConfig executeConfig = S2ExecuteConfigUtil.getExecuteConfig();
        if (executeConfig == null) {
            return true;
        }
        String roles[] = executeConfig.getRoles();
        if (roles == null || roles.length == 0) {
            return true;
        }
        for (int i = 0; i < roles.length; i++) {
            if (request.isUserInRole(roles[i])) {
                return true;
            }
        }
        ActionForward forward = processException(request, response,
                new NoRoleRuntimeException(request.getRemoteUser()), null,
                mapping);
        if (forward != null) {
            processForwardConfig(request, response, forward);
        }
        return false;
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
        ActionForm actionForm = ActionFormUtil.getActionForm(request, mapping);
        if (actionForm != null) {
            return actionForm;
        }
        try {
            actionForm = formConfig.createActionForm(servlet);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
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

    @Override
    protected void processPopulate(HttpServletRequest request,
            HttpServletResponse response, ActionForm form, ActionMapping mapping)
            throws ServletException {

        if (form == null) {
            return;
        }
        form.setServlet(servlet);
        String contentType = request.getContentType();
        String method = request.getMethod();
        form.setMultipartRequestHandler(null);
        MultipartRequestHandler multipartHandler = null;
        if (contentType != null
                && contentType.startsWith("multipart/form-data")
                && method.equalsIgnoreCase("POST")) {
            multipartHandler = getMultipartHandler(mapping.getMultipartClass());
            if (multipartHandler != null) {
                multipartHandler.setServlet(servlet);
                multipartHandler.setMapping(mapping);
                multipartHandler.handleRequest(request);
                Boolean maxLengthExceeded = (Boolean) request
                        .getAttribute(MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED);
                if ((maxLengthExceeded != null)
                        && (maxLengthExceeded.booleanValue())) {
                    form.setMultipartRequestHandler(multipartHandler);
                    processExecuteConfig(request, response, mapping);
                    return;
                }
                SingletonS2ContainerFactory.getContainer().getExternalContext()
                        .setRequest(request);
            }
        }
        processExecuteConfig(request, response, mapping);
        form.reset(mapping, request);
        Map<String, Object> params = getAllParameters(request, multipartHandler);
        S2ActionMapping actionMapping = (S2ActionMapping) mapping;
        for (Iterator<String> i = params.keySet().iterator(); i.hasNext();) {
            String name = i.next();
            try {
                setProperty(actionMapping.getActionForm(), name, params
                        .get(name));
            } catch (Throwable t) {
                throw new IllegalPropertyRuntimeException(actionMapping
                        .getActionFormBeanDesc().getBeanClass(), name, t);
            }
        }
    }

    @Override
    protected void doForward(String uri, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        if (isExporablePath(uri)) {
            exportPropertiesToRequest(request, S2ActionMappingUtil
                    .getActionMapping(), S2ExecuteConfigUtil.getExecuteConfig());
        }
        super.doForward(uri, request, response);
    }

    /**
     * プロパティをリクエストに設定します。 *
     * 
     * @param request
     *            リクエスト
     * @param actionMapping
     *            アクションマッピング
     * @param executeConfig
     *            実行設定
     */
    protected void exportPropertiesToRequest(HttpServletRequest request,
            S2ActionMapping actionMapping, S2ExecuteConfig executeConfig) {
        if (ActionMessagesUtil.hasErrors(request)
                || !executeConfig.isRemoveActionForm()) {
            ActionFormWrapper actionForm = (ActionFormWrapper) request
                    .getAttribute(actionMapping.getAttribute());
            if (actionForm != null) {
                DynaClass dynaClass = actionForm.getDynaClass();
                for (DynaProperty prop : dynaClass.getDynaProperties()) {
                    S2DynaProperty s2prop = (S2DynaProperty) prop;
                    PropertyDesc pd = s2prop.getPropertyDesc();
                    if (isExportableProperty(pd)) {
                        Object value = actionForm.get(pd.getPropertyName());
                        if (value != null) {
                            request.setAttribute(pd.getPropertyName(), value);
                        }
                    }
                }
            }
        }
        BeanDesc actionBeanDesc = actionMapping.getActionBeanDesc();
        for (int i = 0; i < actionBeanDesc.getPropertyDescSize(); i++) {
            Object action = actionMapping.getAction();
            PropertyDesc pd = actionBeanDesc.getPropertyDesc(i);
            if (pd.isReadable() && isExportableProperty(pd)) {
                Object value = WrapperUtil.convert(pd.getValue(action));
                if (value != null) {
                    request.setAttribute(pd.getPropertyName(), value);
                }
            }
        }
    }

    /**
     * リクエストに設定可能なプロパティかどうかを返します。
     * 
     * @param propertyDesc
     *            プロパティ記述
     * @return リクエストに設定可能かどうか
     */
    protected boolean isExportableProperty(PropertyDesc propertyDesc) {
        return !propertyDesc.getPropertyType().getName().startsWith(
                "javax.servlet")
                && !propertyDesc.getPropertyName().equals("requestScope")
                && !propertyDesc.getPropertyName().equals("sessionScope")
                && !propertyDesc.getPropertyName().equals("appplicationScope");
    }

    /**
     * プロパティをリクエストにエクスポート可能なパスかどうかを返します。
     * 
     * @param path
     *            パス
     * @return プロパティをリクエストにエクスポート可能なパスかどうか
     */
    protected boolean isExporablePath(String path) {
        return path != null && path.indexOf(".") > 0 && path.indexOf(".do") < 0;
    }

    /**
     * プロパティの値を設定します。
     * 
     * @param bean
     *            JavaBeans
     * @param name
     *            パラメータ名
     * @param value
     *            パラメータの値
     * @throws ServletException
     *             何か例外が発生した場合。
     */
    protected void setProperty(Object bean, String name, Object value) {
        if (bean == null) {
            return;
        }
        int nestedIndex = name.indexOf(NESTED_DELIM);
        int indexedIndex = name.indexOf(INDEXED_DELIM);
        int mappedIndex = name.indexOf(MAPPED_DELIM);
        if (nestedIndex < 0 && indexedIndex < 0 && mappedIndex < 0) {
            setSimpleProperty(bean, name, value);
        } else {
            int minIndex = minIndex(minIndex(nestedIndex, indexedIndex),
                    mappedIndex);
            if (minIndex == nestedIndex) {
                setProperty(getSimpleProperty(bean, name.substring(0,
                        nestedIndex)), name.substring(nestedIndex + 1), value);
            } else if (minIndex == indexedIndex) {
                IndexParsedResult result = parseIndex(name
                        .substring(indexedIndex + 1));
                if (StringUtil.isEmpty(result.name)) {
                    setIndexedProperty(bean, name.substring(0, indexedIndex),
                            result.indexes, value);
                } else {
                    bean = getIndexedProperty(bean, name.substring(0,
                            indexedIndex), result.indexes);
                    setProperty(bean, result.name, value);
                }
            } else {
                int endIndex = name.indexOf(MAPPED_DELIM2, mappedIndex);
                setProperty(bean, name.substring(0, mappedIndex) + "."
                        + name.substring(mappedIndex + 1, endIndex)
                        + name.substring(endIndex + 1), value);
            }
        }
    }

    /**
     * 0以上で小さいほうのインデックスを返します。
     * 
     * @param index1
     *            インデックス1
     * @param index2
     *            インデックス2
     * @return 0以上で小さいほうのインデックス
     */
    protected int minIndex(int index1, int index2) {
        if (index1 >= 0 && index2 < 0) {
            return index1;
        } else if (index1 < 0 && index2 >= 0) {
            return index2;
        } else {
            return Math.min(index1, index2);
        }
    }

    /**
     * 単純なプロパティの値を設定します。
     * 
     * @param bean
     *            JavaBeans
     * @param name
     *            パラメータ名
     * @param value
     *            パラメータの値
     * @throws ServletException
     *             何か例外が発生した場合。
     */
    @SuppressWarnings("unchecked")
    protected void setSimpleProperty(Object bean, String name, Object value) {
        if (bean instanceof Map) {
            setMapProperty((Map) bean, name, value);
            return;
        }
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(bean.getClass());
        if (!beanDesc.hasPropertyDesc(name)) {
            return;
        }
        PropertyDesc pd = beanDesc.getPropertyDesc(name);
        if (!pd.isWritable()) {
            return;
        }
        if (pd.getPropertyType().isArray()) {
            pd.setValue(bean, value);
        } else if (List.class.isAssignableFrom(pd.getPropertyType())) {
            List<String> list = ModifierUtil.isAbstract(pd.getPropertyType()) ? new ArrayList<String>()
                    : (List<String>) ClassUtil
                            .newInstance(pd.getPropertyType());
            list.addAll(Arrays.asList((String[]) value));
            pd.setValue(bean, list);
        } else if (value == null) {
            pd.setValue(bean, null);
        } else if (value instanceof String[]) {
            String[] values = (String[]) value;
            pd.setValue(bean, values.length > 0 ? values[0] : null);
        } else {
            pd.setValue(bean, value);
        }
    }

    /**
     * Mapの値を設定します。
     * 
     * @param map
     *            マップ
     * @param name
     *            キー名
     * @param value
     *            値
     */
    @SuppressWarnings("unchecked")
    protected void setMapProperty(Map map, String name, Object value) {
        if (value instanceof String[]) {
            String[] values = (String[]) value;
            map.put(name, values.length > 0 ? values[0] : null);
        } else {
            map.put(name, value);
        }
    }

    /**
     * 単純なプロパティの値を返します。
     * 
     * @param bean
     *            JavaBeans
     * @param name
     *            プロパティ名
     * @return プロパティの値
     */
    protected Object getSimpleProperty(Object bean, String name) {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(bean.getClass());
        if (!beanDesc.hasPropertyDesc(name)) {
            return null;
        }
        PropertyDesc pd = beanDesc.getPropertyDesc(name);
        if (!pd.isReadable()) {
            return null;
        }
        Object value = pd.getValue(bean);
        if (value == null) {
            if (!ModifierUtil.isAbstract(pd.getPropertyType())) {
                value = ClassUtil.newInstance(pd.getPropertyType());
                if (pd.isWritable()) {
                    pd.setValue(bean, value);
                }
            } else if (Map.class.isAssignableFrom(pd.getPropertyType())) {
                value = new HashMap<String, Object>();
                if (pd.isWritable()) {
                    pd.setValue(bean, value);
                }
            }
        }
        return value;
    }

    /**
     * インデックス化されたプロパティの値を返します。
     * 
     * @param bean
     *            JavaBeans
     * @param name
     *            名前
     * @param indexes
     *            インデックスの配列
     * @return インデックス化されたプロパティの値
     * 
     */
    @SuppressWarnings("unchecked")
    protected Object getIndexedProperty(Object bean, String name, int[] indexes) {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(bean.getClass());
        if (!beanDesc.hasPropertyDesc(name)) {
            return null;
        }
        PropertyDesc pd = beanDesc.getPropertyDesc(name);
        if (!pd.isReadable()) {
            return null;
        }
        if (pd.getPropertyType().isArray()) {
            Object array = pd.getValue(bean);
            Class<?> elementType = getArrayElementType(pd.getPropertyType(),
                    indexes.length);
            if (array == null) {
                int[] newIndexes = new int[indexes.length];
                newIndexes[0] = indexes[0] + 1;
                array = Array.newInstance(elementType, newIndexes);
            }
            array = expand(array, indexes, elementType);
            pd.setValue(bean, array);
            return getArrayValue(array, indexes, elementType);
        } else if (List.class.isAssignableFrom(pd.getPropertyType())) {
            List list = (List) pd.getValue(bean);
            if (list == null) {
                list = new ArrayList(Math.max(50, indexes[0]));
                pd.setValue(bean, list);
            }
            ParameterizedClassDesc pcd = pd.getParameterizedClassDesc();
            for (int i = 0; i < indexes.length; i++) {
                if (pcd == null || !pcd.isParameterizedClass()
                        || !List.class.isAssignableFrom(pcd.getRawClass())) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j <= i; j++) {
                        sb.append("[").append(indexes[j]).append("]");
                    }
                    throw new NoParameterizedListRuntimeException(
                            getRealClass(beanDesc.getBeanClass()), pd
                                    .getPropertyName()
                                    + sb);
                }
                int size = list.size();
                pcd = pcd.getArguments()[0];
                for (int j = size; j <= indexes[i]; j++) {
                    if (i == indexes.length - 1) {
                        list.add(ClassUtil.newInstance(convertClass(pcd
                                .getRawClass())));
                    } else {
                        list.add(new ArrayList());
                    }
                }
                if (i < indexes.length - 1) {
                    list = (List) list.get(indexes[i]);
                }
            }
            return list.get(indexes[indexes.length - 1]);
        } else {
            throw new IndexedPropertyNotListArrayRuntimeException(
                    getRealClass(beanDesc.getBeanClass()), pd.getPropertyName());
        }
    }

    /**
     * インデックス化されたプロパティの値を設定します。
     * 
     * @param bean
     *            JavaBeans
     * @param name
     *            名前
     * @param indexes
     *            インデックスの配列
     * @param value
     *            値
     */
    @SuppressWarnings("unchecked")
    protected void setIndexedProperty(Object bean, String name, int[] indexes,
            Object value) {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(bean.getClass());
        if (!beanDesc.hasPropertyDesc(name)) {
            return;
        }
        PropertyDesc pd = beanDesc.getPropertyDesc(name);
        if (!pd.isWritable()) {
            return;
        }
        if (value.getClass().isArray() && Array.getLength(value) > 0) {
            value = Array.get(value, 0);
        }
        if (pd.getPropertyType().isArray()) {
            Object array = pd.getValue(bean);
            Class<?> elementType = getArrayElementType(pd.getPropertyType(),
                    indexes.length);
            if (array == null) {
                int[] newIndexes = new int[indexes.length];
                newIndexes[0] = indexes[0] + 1;
                array = Array.newInstance(elementType, newIndexes);
            }
            array = expand(array, indexes, elementType);
            pd.setValue(bean, array);
            setArrayValue(array, indexes, value);
        } else if (List.class.isAssignableFrom(pd.getPropertyType())) {
            List list = (List) pd.getValue(bean);
            if (list == null) {
                list = new ArrayList(Math.max(50, indexes[0]));
                pd.setValue(bean, list);
            }
            ParameterizedClassDesc pcd = pd.getParameterizedClassDesc();
            for (int i = 0; i < indexes.length; i++) {
                if (pcd == null || !pcd.isParameterizedClass()
                        || !List.class.isAssignableFrom(pcd.getRawClass())) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j <= i; j++) {
                        sb.append("[").append(indexes[j]).append("]");
                    }
                    throw new NoParameterizedListRuntimeException(
                            getRealClass(beanDesc.getBeanClass()), pd
                                    .getPropertyName()
                                    + sb);
                }
                int size = list.size();
                pcd = pcd.getArguments()[0];
                for (int j = size; j <= indexes[i]; j++) {
                    if (i == indexes.length - 1) {
                        list.add(ClassUtil.newInstance(convertClass(pcd
                                .getRawClass())));
                    } else {
                        list.add(new ArrayList());
                    }
                }
                if (i < indexes.length - 1) {
                    list = (List) list.get(indexes[i]);
                }
            }
            list.set(indexes[indexes.length - 1], value);
        } else {
            throw new IndexedPropertyNotListArrayRuntimeException(
                    getRealClass(beanDesc.getBeanClass()), pd.getPropertyName());
        }
    }

    /**
     * 配列の要素の型を返します。
     * 
     * @param clazz
     *            配列のクラス
     * @param depth
     *            配列の深さ
     * @return 配列の要素の型
     */
    protected Class<?> getArrayElementType(Class<?> clazz, int depth) {
        for (int i = 0; i < depth; i++) {
            clazz = clazz.getComponentType();
        }
        return clazz;
    }

    /**
     * 配列を拡張します。
     * 
     * @param array
     *            配列
     * @param indexes
     *            インデックスの配列
     * @param elementType
     *            配列の要素のクラス
     * @return 拡張後の配列
     */
    protected Object expand(Object array, int[] indexes, Class<?> elementType) {
        int length = Array.getLength(array);
        if (length <= indexes[0]) {
            int[] newIndexes = new int[indexes.length];
            newIndexes[0] = indexes[0] + 1;
            Object newArray = Array.newInstance(elementType, newIndexes);
            System.arraycopy(array, 0, newArray, 0, length);
            array = newArray;
        }
        if (indexes.length > 1) {
            int[] newIndexes = new int[indexes.length - 1];
            for (int i = 1; i < indexes.length; i++) {
                newIndexes[i - 1] = indexes[i];
            }
            Array.set(array, indexes[0], expand(Array.get(array, indexes[0]),
                    newIndexes, elementType));
        }
        return array;
    }

    /**
     * 配列の値を返します。
     * 
     * @param array
     *            配列
     * @param indexes
     *            インデックスの配列
     * @param elementType
     *            配列の要素の型
     * @return 配列の値
     */
    protected Object getArrayValue(Object array, int[] indexes,
            Class<?> elementType) {
        Object value = array;
        elementType = convertClass(elementType);
        for (int i = 0; i < indexes.length; i++) {
            Object value2 = Array.get(value, indexes[i]);
            if (i == indexes.length - 1 && value2 == null) {
                value2 = ClassUtil.newInstance(elementType);
                Array.set(value, indexes[i], value2);
            }
            value = value2;
        }
        return value;
    }

    /**
     * 配列の値を返します。
     * 
     * @param array
     *            配列
     * @param indexes
     *            インデックスの配列
     * @param value
     *            値
     */
    protected void setArrayValue(Object array, int[] indexes, Object value) {
        for (int i = 0; i < indexes.length - 1; i++) {
            array = Array.get(array, indexes[i]);
        }
        Array.set(array, indexes[indexes.length - 1], value);
    }

    /**
     * クラスが抽象クラスかつMap系ならHashMapに変換します。
     * 
     * @param clazz
     *            クラス
     * @return 変換後のクラス
     */
    protected Class<?> convertClass(Class<?> clazz) {
        if (ModifierUtil.isAbstract(clazz) && Map.class.isAssignableFrom(clazz)) {
            return HashMap.class;
        }
        return clazz;
    }

    /**
     * インデックスを解析します。
     * 
     * @param name
     *            プロパティ名
     * @return インデックスの解析結果
     */
    protected IndexParsedResult parseIndex(String name) {
        IndexParsedResult result = new IndexParsedResult();
        while (true) {
            int index = name.indexOf(INDEXED_DELIM2);
            if (index < 0) {
                throw new IllegalArgumentException(INDEXED_DELIM2
                        + " is not found in " + name);
            }
            result.indexes = ArrayUtil.add(result.indexes, Integer.valueOf(
                    name.substring(0, index)).intValue());
            name = name.substring(index + 1);
            if (name.length() == 0) {
                break;
            } else if (name.charAt(0) == INDEXED_DELIM) {
                name = name.substring(1);
            } else if (name.charAt(0) == NESTED_DELIM) {
                name = name.substring(1);
                break;
            } else {
                throw new IllegalArgumentException(name);
            }
        }
        result.name = name;
        return result;
    }

    /**
     * AOPで拡張されている場合は、拡張前の本当のクラスを返します。
     * 
     * @param clazz
     *            クラス
     * @return 本当のクラス
     */
    protected Class<?> getRealClass(Class<?> clazz) {
        if (clazz.getName().indexOf(AspectWeaver.SUFFIX_ENHANCED_CLASS) > 0) {
            return clazz.getSuperclass();
        }
        return clazz;
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
     * すべてのパラメータを返します。
     * 
     * @param request
     *            リクエスト
     * @param multipartHandler
     *            マルチパートリクエストハンドラ
     * @return すべてのパラメータ
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> getAllParameters(HttpServletRequest request,
            MultipartRequestHandler multipartHandler) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (request instanceof MultipartRequestWrapper) {
            request = ((MultipartRequestWrapper) request).getRequest();
        }
        Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            params.put(name, request.getParameterValues(name));
        }
        if (multipartHandler != null) {
            Hashtable elements = multipartHandler.getAllElements();
            params.putAll(elements);
        }
        return params;
    }

    /**
     * 
     */
    protected static class IndexParsedResult {
        /**
         * インデックスの配列です。
         */
        public int[] indexes = new int[0];

        /**
         * インデックス部分を除いた名前です。
         */
        public String name;
    }
}