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

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.struts.action.ActionForm;
import org.seasar.framework.aop.javassist.AspectWeaver;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;

/**
 * 動的Beanです。
 * 
 * @author higa
 * 
 */
public class S2DynaBean extends ActionForm implements DynaBean {

    private static final long serialVersionUID = 1L;

    /**
     * 動的クラスです。
     */
    protected DynaClass dynaClass;

    /**
     * POJO Beanです。
     */
    protected Object bean;

    /**
     * インスタンスを構築します。
     * 
     * @param dynaClass
     *            動的クラス
     * @param bean
     *            POJO Bean
     */
    public S2DynaBean(DynaClass dynaClass, Object bean) {
        this.dynaClass = dynaClass;
        this.bean = bean;
    }

    public DynaClass getDynaClass() {
        return dynaClass;
    }

    /**
     * Beanのクラスを返します。 アスペクトでエンハンスされている場合は、元のクラスを返します。
     * 
     * @return Beanのクラス
     */
    protected Class<?> getBeanClass() {
        Class<?> clazz = bean.getClass();
        if (clazz.getName().indexOf(AspectWeaver.SUFFIX_ENHANCED_CLASS) > 0) {
            return clazz.getSuperclass();
        }
        return clazz;
    }

    public Object get(String name) {
        S2DynaProperty property = getProperty(name);
        return property.getValue(bean);
    }

    /**
     * プロパティを返します。
     * 
     * @param name
     *            名前
     * @return プロパティ
     */
    protected S2DynaProperty getProperty(String name) {
        S2DynaProperty property = (S2DynaProperty) dynaClass
                .getDynaProperty(name);
        if (property == null) {
            throw new PropertyNotFoundRuntimeException(getBeanClass(), name);
        }
        return property;
    }

    public void set(String name, Object value) {
        S2DynaProperty property = getProperty(name);
        property.setValue(bean, value);
    }

    public Object get(String name, int index) {
        S2DynaIndexedProperty property = (S2DynaIndexedProperty) getProperty(name);
        return property.getValue(bean, index);
    }

    public void set(String name, int index, Object value) {
        S2DynaIndexedProperty property = (S2DynaIndexedProperty) getProperty(name);
        property.setValue(bean, index, value);
    }

    public Object get(String name, String key) {
        S2DynaMappedProperty property = (S2DynaMappedProperty) getProperty(name);
        return property.getValue(bean, key);
    }

    public boolean contains(String name, String key) {
        S2DynaMappedProperty property = (S2DynaMappedProperty) getProperty(name);
        return property.contains(bean, key);
    }

    public void set(String name, String key, Object value) {
        S2DynaMappedProperty property = (S2DynaMappedProperty) getProperty(name);
        property.setValue(bean, key, value);
    }

    public void remove(String name, String key) {
        S2DynaMappedProperty property = (S2DynaMappedProperty) getProperty(name);
        property.remove(bean, key);
    }

}
