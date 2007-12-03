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

import java.util.HashMap;
import java.util.Map;

import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.ModifierUtil;

/**
 * インデックスで値にアクセスするプロパティです。
 * 
 * @author higa
 * 
 */
public class S2DynaMappedProperty extends S2DynaProperty {

    private static final long serialVersionUID = 1L;

    /**
     * インスタンスを構築します。
     * 
     * @param propertyDesc
     *            プロパティ記述
     */
    public S2DynaMappedProperty(PropertyDesc propertyDesc) {
        super(propertyDesc);
        contentType = propertyDesc.getValueClassOfMap();
    }

    @Override
    public boolean isIndexed() {
        return false;
    }

    @Override
    public boolean isMapped() {
        return true;
    }

    /**
     * 値を返します。
     * 
     * @param bean
     *            Bean
     * @param key
     *            キー
     * @return 値
     */
    @SuppressWarnings("unchecked")
    public Object getValue(Object bean, String key) {
        Map<String, Object> map = (Map<String, Object>) getValue(bean);
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    /**
     * 要素が含まれているかどうかを返します。
     * 
     * @param bean
     *            Bean
     * @param key
     *            キー
     * @return 要素が含まれているかどうか
     */
    @SuppressWarnings("unchecked")
    public boolean contains(Object bean, String key) {
        Map<String, Object> map = (Map<String, Object>) getValue(bean);
        if (map == null) {
            return false;
        }
        return map.containsKey(key);
    }

    /**
     * 値を設定します。
     * 
     * @param bean
     *            Bean
     * @param key
     *            キー
     * @param value
     *            値
     */
    @SuppressWarnings("unchecked")
    public void setValue(Object bean, String key, Object value) {
        Map<String, Object> map = (Map<String, Object>) getValue(bean);
        if (map == null) {
            if (ModifierUtil.isAbstract(type)) {
                map = new HashMap<String, Object>();
            } else {
                map = (Map<String, Object>) ClassUtil.newInstance(type);
            }
            setValue(bean, map);
        }
        map.put(key, value);
    }

    /**
     * 要素を削除します。
     * 
     * @param bean
     *            Bean
     * @param key
     *            キー
     */
    @SuppressWarnings("unchecked")
    public void remove(Object bean, String key) {
        Map<String, Object> map = (Map<String, Object>) getValue(bean);
        if (map == null) {
            return;
        }
        map.remove(key);
    }
}