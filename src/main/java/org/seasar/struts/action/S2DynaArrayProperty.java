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

import java.lang.reflect.Array;

import org.seasar.framework.beans.PropertyDesc;

/**
 * リスト用の動的プロパティです。
 * 
 * @author higa
 * 
 */
public class S2DynaArrayProperty extends S2DynaIndexedProperty {

    private static final long serialVersionUID = 1L;

    /**
     * インスタンスを構築します。
     * 
     * @param propertyDesc
     *            プロパティ記述
     */
    public S2DynaArrayProperty(PropertyDesc propertyDesc) {
        super(propertyDesc);
        contentType = propertyDesc.getPropertyType().getComponentType();
    }

    @Override
    public Object getValue(Object bean, int index) {
        Object array = getValue(bean);
        if (array == null) {
            return null;
        }
        return Array.get(array, index);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object bean, int index, Object value) {
        Object array = getValue(bean);
        if (array == null) {
            array = Array.newInstance(contentType, index + 1);
            setValue(bean, array);
        }
        int size = Array.getLength(array);
        if (index <= size) {
            Object array2 = Array.newInstance(contentType, index + 1);
            System.arraycopy(array, 0, array2, 0, size);
            array = array2;
            setValue(bean, array);
        }
        Array.set(array, index, value);
    }
}
