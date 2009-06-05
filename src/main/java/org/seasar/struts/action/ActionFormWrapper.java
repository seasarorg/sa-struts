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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;
import org.seasar.framework.util.MethodUtil;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.util.S2ExecuteConfigUtil;

/**
 * アクションフォームのラッパーです。
 * 
 * @author higa
 * 
 */
public class ActionFormWrapper extends ActionForm implements DynaBean {

    private static final long serialVersionUID = 1L;

    /**
     * アクションフォームラッパーの動的クラスです。
     */
    protected ActionFormWrapperClass actionFormWrapperClass;

    /**
     * アクションフォームです。
     */
    protected Object actionForm;

    /**
     * インスタンスを構築します。
     * 
     * @param actionFormWrapperClass
     *            アクションフォームラッパーの動的クラス
     */
    public ActionFormWrapper(ActionFormWrapperClass actionFormWrapperClass) {
        this.actionFormWrapperClass = actionFormWrapperClass;
        actionForm = actionFormWrapperClass.actionMapping.getActionForm();
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        S2ExecuteConfig executeConfig = S2ExecuteConfigUtil.getExecuteConfig();
        if (executeConfig != null) {
            Method m = executeConfig.getResetMethod();
            if (m != null) {
                MethodUtil.invoke(m, actionForm, null);
            }
        }
    }

    public DynaClass getDynaClass() {
        return actionFormWrapperClass;
    }

    /**
     * 動的プロパティを返します。
     * 
     * @param name
     *            名前
     * @return 動的プロパティ
     */
    protected S2DynaProperty getProperty(String name) {
        S2DynaProperty property = (S2DynaProperty) actionFormWrapperClass
                .getDynaProperty(name);
        if (property == null) {
            throw new PropertyNotFoundRuntimeException(
                    actionFormWrapperClass.actionMapping
                            .getActionFormBeanDesc().getBeanClass(), name);
        }
        return property;
    }

    public Object get(String name) {
        S2DynaProperty property = getProperty(name);
        return property.getValue(actionForm);
    }

    public void set(String name, Object value) {
        throw new UnsupportedOperationException("set");
    }

    public boolean contains(String name, String key) {
        throw new UnsupportedOperationException("contains");
    }

    @SuppressWarnings("unchecked")
    public Object get(String name, int index) {
        S2DynaProperty property = getProperty(name);
        Object value = property.getValue(actionForm);
        if (value == null) {
            throw new IllegalStateException("The value of property(" + name
                    + ") is null.");
        } else if (value.getClass().isArray()) {
            return (Array.get(value, index));
        } else if (value instanceof List) {
            return ((List) value).get(index);
        } else {
            throw new IllegalStateException("The value of property(" + name
                    + ") is not indexed.");
        }
    }

    @SuppressWarnings("unchecked")
    public Object get(String name, String key) {
        S2DynaProperty property = getProperty(name);
        Object value = property.getValue(actionForm);
        if (value == null) {
            throw new IllegalStateException("The value of property(" + name
                    + ") is null.");
        } else if (value instanceof Map) {
            return ((Map) value).get(key);
        } else {
            throw new IllegalStateException("The value of property(" + name
                    + ") is not mapped.");
        }
    }

    public void remove(String name, String key) {
        throw new UnsupportedOperationException("remove");

    }

    public void set(String name, int index, Object value) {
        throw new UnsupportedOperationException("set");
    }

    public void set(String name, String key, Object value) {
        throw new UnsupportedOperationException("set");

    }
}