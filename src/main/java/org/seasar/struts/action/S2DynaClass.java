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
import org.seasar.framework.util.ArrayMap;

/**
 * 動的クラスです。
 * 
 * @author higa
 * 
 */
public class S2DynaClass implements DynaClass {

    /**
     * 動的プロパティの集合です。
     */
    protected ArrayMap dynaProperties = new ArrayMap();

    /**
     * 動的プロパティの配列を返します。
     * 
     * @return 動的プロパティの配列
     */
    public S2DynaProperty[] getDynaProperties() {
        return (S2DynaProperty[]) dynaProperties
                .toArray(new S2DynaProperty[dynaProperties.size()]);
    }

    /**
     * 動的プロパティを返します。
     * 
     * @param name
     *            名前
     * @return 動的プロパティ
     */
    public S2DynaProperty getDynaProperty(String name) {
        return (S2DynaProperty) dynaProperties.get(name);
    }

    /**
     * 動的プロパティを追加します。
     * 
     * @param property
     *            動的プロパティ
     */
    public void addDynaProperty(S2DynaProperty property) {
        dynaProperties.put(property.getName(), property);
    }

    public String getName() {
        throw new UnsupportedOperationException("getName");
    }

    public DynaBean newInstance() throws IllegalAccessException,
            InstantiationException {
        throw new UnsupportedOperationException("newInstance");
    }

}
