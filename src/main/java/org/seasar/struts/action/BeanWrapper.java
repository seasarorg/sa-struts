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

import java.util.Collection;
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
        PropertyDesc pd = beanDesc.getPropertyDesc(key.toString());
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
        throw new UnsupportedOperationException("containsKey");
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("containsValue");
    }

    public Set entrySet() {
        throw new UnsupportedOperationException("size");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("isEmpty");
    }

    public Set keySet() {
        throw new UnsupportedOperationException("keySet");
    }

    public void putAll(Map t) {
        throw new UnsupportedOperationException("putAll");
    }

    public Object remove(Object key) {
        return put(key, null);
    }

    public int size() {
        throw new UnsupportedOperationException("size");
    }

    public Collection values() {
        throw new UnsupportedOperationException("values");
    }
}