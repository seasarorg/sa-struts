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

import java.io.Serializable;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.seasar.struts.config.S2ActionMapping;

/**
 * アクションフォームラッパーのメタ情報を管理するクラスです。
 * 
 * @author higa
 * 
 */
public class ActionFormWrapperClass implements DynaClass, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * アクションマッピングです。
     */
    protected S2ActionMapping actionMapping;

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
        return actionMapping.getDynaProperties();
    }

    public DynaProperty getDynaProperty(String name) {
        return actionMapping.getDynaProperty(name);
    }

    public String getName() {
        return actionMapping.getName();
    }

    public DynaBean newInstance() throws IllegalAccessException,
            InstantiationException {
        // TODO Auto-generated method stub
        return null;
    }
}