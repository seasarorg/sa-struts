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

import org.apache.commons.beanutils.DynaProperty;
import org.seasar.framework.beans.PropertyDesc;

/**
 * Seasar2用の動的プロパティです。
 * 
 * @author higa
 * 
 */
public class S2DynaProperty extends DynaProperty {

    private static final long serialVersionUID = 1L;

    /**
     * プロパティ記述です。
     */
    protected PropertyDesc propertyDesc;

    /**
     * インスタンスを構築します。
     * 
     * @param name
     *            名前
     * @param propertyDesc
     *            プロパティ記述
     */
    public S2DynaProperty(String name, PropertyDesc propertyDesc) {
        super(name);
        this.propertyDesc = propertyDesc;
    }

    /**
     * インスタンスを構築します。
     * 
     * @param name
     *            名前
     * @param type
     *            型
     * @param propertyDesc
     *            プロパティ記述
     */
    public S2DynaProperty(String name, Class<?> type, PropertyDesc propertyDesc) {
        super(name, type);
        this.propertyDesc = propertyDesc;
    }

    /**
     * インスタンスを構築します。
     * 
     * @param name
     *            名前
     * @param type
     *            型
     * @param contentType
     *            中身の型
     * @param propertyDesc
     *            プロパティ記述
     */
    public S2DynaProperty(String name, Class<?> type, Class<?> contentType,
            PropertyDesc propertyDesc) {
        super(name, type, contentType);
        this.propertyDesc = propertyDesc;
    }

    /**
     * プロパティ記述を返します。
     * 
     * @return プロパティ記述
     */
    public PropertyDesc getPropertyDesc() {
        return propertyDesc;
    }
}