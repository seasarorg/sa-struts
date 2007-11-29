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
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;
import org.seasar.struts.config.S2ActionMapping;

/**
 * アクションフォームのラッパーです。
 * 
 * @author higa
 * 
 */
public class ActionFormWrapper extends ActionForm implements DynaBean {

    private static final long serialVersionUID = 1L;

    /**
     * 動的クラスです。
     */
    protected DynaClass dynaClass;

    /**
     * アクションマッピングです。
     */
    protected S2ActionMapping actionMapping;

    /**
     * POJOアクションフォームです。
     */
    protected Object actionForm;

    /**
     * インスタンスを構築します。
     * 
     * @param dynaClass
     *            動的クラス
     * @param actionMapping
     *            アクションマッピング
     */
    public ActionFormWrapper(DynaClass dynaClass, S2ActionMapping actionMapping) {
        this.dynaClass = dynaClass;
        this.actionMapping = actionMapping;
        actionForm = actionMapping.getActionForm();
    }

    public DynaClass getDynaClass() {
        return dynaClass;
    }

    public Object get(String name) {
        S2DynaProperty property = getProperty(name);
        return property.getPropertyDesc().getValue(actionForm);
    }

    /**
     * プロパティを返します。
     * 
     * @param name
     *            名前
     * @return プロパティ
     */
    protected S2DynaProperty getProperty(String name) {
        S2DynaProperty property = actionMapping.getDynaProperty(name);
        if (property == null) {
            throw new PropertyNotFoundRuntimeException(actionMapping
                    .getActionFormClass(), name);
        }
        return property;
    }

    public void set(String name, Object value) {
        S2DynaProperty property = getProperty(name);
        property.getPropertyDesc().setValue(actionForm, value);
    }

    public Object get(String name, int index) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.beanutils.DynaBean#contains(java.lang.String,
     *      java.lang.String)
     */
    public boolean contains(String name, String key) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.beanutils.DynaBean#get(java.lang.String,
     *      java.lang.String)
     */
    public Object get(String name, String key) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.beanutils.DynaBean#remove(java.lang.String,
     *      java.lang.String)
     */
    public void remove(String name, String key) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.beanutils.DynaBean#set(java.lang.String, int,
     *      java.lang.Object)
     */
    public void set(String name, int index, Object value) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.beanutils.DynaBean#set(java.lang.String,
     *      java.lang.String, java.lang.Object)
     */
    public void set(String name, String key, Object value) {
        // TODO Auto-generated method stub

    }

}
