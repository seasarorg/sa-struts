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
import java.util.List;

import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.ModifierUtil;

/**
 * リスト用の動的プロパティです。
 * 
 * @author higa
 * 
 */
public class S2DynaListProperty extends S2DynaIndexedProperty {

    private static final long serialVersionUID = 1L;

    /**
     * インスタンスを構築します。
     * 
     * @param propertyDesc
     *            プロパティ記述
     */
    public S2DynaListProperty(PropertyDesc propertyDesc) {
        super(propertyDesc);
        contentType = propertyDesc.getElementClassOfCollection();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getValue(Object bean, int index) {
        List list = (List) getValue(bean);
        if (list == null) {
            return null;
        }
        return list.get(index);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object bean, int index, Object value) {
        List list = (List) getValue(bean);
        if (list == null) {
            if (ModifierUtil.isAbstract(type)) {
                list = new ArrayList();
            } else {
                list = (List) ClassUtil.newInstance(type);
            }
            setValue(bean, list);
        }
        if (index <= list.size()) {
            int size = index - list.size() + 1;
            for (int i = 0; i < size; i++) {
                list.add(null);
            }
        }
        list.set(index, value);

    }

}
