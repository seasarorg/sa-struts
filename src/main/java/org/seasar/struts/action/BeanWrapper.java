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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * JavaBeansをマップとして扱うクラスです。
 * 
 * @author higa
 * 
 */
@SuppressWarnings("unchecked")
public class BeanWrapper implements Map {

    private static final char INDEXED_DELIM = '[';

    private static final char INDEXED_DELIM2 = ']';

    /**
     * JavaBeansです。
     */
    protected Object bean;

    /**
     * Bean記述です。
     */
    protected BeanDesc beanDesc;

    /**
     * インスタンスを構築します。
     * 
     * @param bean
     *            JavaBeans
     */
    public BeanWrapper(Object bean) {
        this.bean = bean;
        beanDesc = BeanDescFactory.getBeanDesc(bean.getClass());
    }

    public Object get(Object key) {
        if (key == null) {
            throw new NullPointerException(
                    "The key parameter must not be null.");
        }
        String k = key.toString();
        int pos = k.lastIndexOf(INDEXED_DELIM);
        if (pos > 0) {
            int endPos = k.lastIndexOf(INDEXED_DELIM2);
            if (pos >= endPos) {
                throw new IllegalArgumentException("The key(" + k
                        + ") is invalid.");
            }
            int index = Integer.valueOf(k.substring(pos + 1, endPos));
            String name = k.substring(0, pos);
            
            Object value;
            Class type;
            if(name.indexOf(INDEXED_DELIM) > 0){
                value = get(name);
                type = value.getClass();
            }else{
                PropertyDesc pd = beanDesc.getPropertyDesc(name);
                if (!pd.isReadable()) {
                    return null;
                }
                value = pd.getValue(bean);
                if (value == null) {
                    return null;
                }

                type = pd.getPropertyType();
            }
            if (type.isArray()) {
                return WrapperUtil.convert(Array.get(value, index));
            }
            if (List.class.isAssignableFrom(type)) {
                return WrapperUtil.convert(((List) value).get(index));
            }
            throw new IllegalStateException(
                    "Index property must be an array or a list.");
        }
        PropertyDesc pd = beanDesc.getPropertyDesc(k);
        if (!pd.isReadable()) {
            return null;
        }
        return WrapperUtil.convert(pd.getValue(bean));
    }

    public Object put(Object key, Object value) {
        PropertyDesc pd = beanDesc.getPropertyDesc(key.toString());
        pd.setValue(bean, value);
        return null;
    }

    public void clear() {
        throw new UnsupportedOperationException("clear");
    }

    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        return beanDesc.hasPropertyDesc(key.toString());
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("containsValue");
    }

    public Set entrySet() {
        Set set = new HashSet<Entry>();
        int size = beanDesc.getPropertyDescSize();
        for (int i = 0; i < size; i++) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            set.add(new BeanEntry(bean, pd));
        }
        return set;
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("isEmpty");
    }

    public Set keySet() {
        Set<String> set = new HashSet<String>();
        int size = beanDesc.getPropertyDescSize();
        for (int i = 0; i < size; i++) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            set.add(pd.getPropertyName());
        }
        return set;
    }

    public void putAll(Map t) {
        throw new UnsupportedOperationException("putAll");
    }

    public Object remove(Object key) {
        return put(key, null);
    }

    public int size() {
        return beanDesc.getPropertyDescSize();
    }

    public Collection values() {
        throw new UnsupportedOperationException("values");
    }

    @Override
    public String toString() {
        return bean.toString();
    }

    /**
     * Bean用の {@link Entry}です。
     * 
     */
    protected static class BeanEntry implements Entry {

        /**
         * プロパティ記述です。
         */
        protected PropertyDesc propDesc;

        /**
         * Beanです。
         */
        protected Object bean;

        /**
         * インスタンスを構築します。
         * 
         * @param bean
         *            Beanです。
         * @param propDesc
         *            プロパティ記述です。
         */
        public BeanEntry(Object bean, PropertyDesc propDesc) {
            this.propDesc = propDesc;
            this.bean = bean;
        }

        public Object getKey() {
            return propDesc.getPropertyName();
        }

        public Object getValue() {
            if (!propDesc.isReadable()) {
                return null;
            }
            return WrapperUtil.convert(propDesc.getValue(bean));
        }

        public Object setValue(Object value) {
            propDesc.setValue(bean, value);
            return null;
        }

    }
}