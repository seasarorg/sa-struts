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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.seasar.struts.config.S2ActionMapping;

/**
 * アクションフォームラッパーの動的クラスです。
 * 
 * @author higa
 * 
 */
public class ActionFormWrapperClass implements DynaClass {

    /**
     * アクションマッピングです。
     */
    protected S2ActionMapping actionMapping;

    /**
     * 動的プロパティ用のマップです。
     */
    protected Map<String, DynaProperty> propertyMap = new HashMap<String, DynaProperty>();

    /**
     * インスタンスを構築します。
     * 
     * @param actionMapping
     *            アクションマッピング
     */
    public ActionFormWrapperClass(S2ActionMapping actionMapping) {
        this.actionMapping = actionMapping;
    }

    public DynaProperty[] getDynaProperties() {
        return propertyMap.values().toArray(
                new DynaProperty[propertyMap.size()]);
    }

    public DynaProperty getDynaProperty(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        return propertyMap.get(name);
    }

    /**
     * 動的プロパティを追加します。
     * 
     * @param property
     *            動的プロパティ
     */
    public void addDynaProperty(DynaProperty property) {
        propertyMap.put(property.getName(), property);
    }

    public String getName() {
        return actionMapping.getName();
    }

    public DynaBean newInstance() throws IllegalAccessException,
            InstantiationException {
        return new ActionFormWrapper(this);
    }

    /**
     * アクションマッピングを返します。
     * 
     * @return アクションマッピング
     */
    public S2ActionMapping getActionMapping() {
        return actionMapping;
    }
}