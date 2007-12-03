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
package org.seasar.struts.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts.action.ActionMapping;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.ComponentDef;

/**
 * Seasar2用のアクションマッピングです。
 * 
 * @author higa
 * 
 */
public class S2ActionMapping extends ActionMapping {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * コンポーネント定義です。
     */
    protected ComponentDef componentDef;

    /**
     * アクションのBean記述です。
     */
    protected BeanDesc actionBeanDesc;

    /**
     * アクションフォームのBean記述です。
     */
    protected BeanDesc actionFormBeanDesc;

    /**
     * 実行設定のマップです
     */
    protected Map<String, S2ExecuteConfig> executeConfigs = new HashMap<String, S2ExecuteConfig>();

    /**
     * アクションフォーム用のプロパティ記述です。
     */
    protected PropertyDesc actionFormPropertyDesc;

    /**
     * コンポーネント定義を返します。
     * 
     * @return コンポーネント定義
     */
    public ComponentDef getComponentDef() {
        return componentDef;
    }

    /**
     * コンポーネント定義を設定します。
     * 
     * @param componentDef
     */
    public void setComponentDef(ComponentDef componentDef) {
        this.componentDef = componentDef;
        actionBeanDesc = BeanDescFactory.getBeanDesc(componentDef
                .getComponentClass());
        actionFormBeanDesc = actionBeanDesc;
    }

    /**
     * アクションのBean記述を返します。
     * 
     * @return アクションのBean記述
     */
    public BeanDesc getActionBeanDesc() {
        return actionBeanDesc;
    }

    /**
     * アクションフォームのBean記述を返します。
     * 
     * @return アクションフォームのBean記述
     */
    public BeanDesc getActionFormBeanDesc() {
        return actionFormBeanDesc;
    }

    /**
     * POJOアクションを返します。
     * 
     * @return POJOアクション
     */
    public Object getAction() {
        return componentDef.getComponent();
    }

    /**
     * POJOアクションフォームを返します。
     * 
     * @return POJOアクションフォーム
     */
    public Object getActionForm() {
        Object action = getAction();
        if (actionFormPropertyDesc != null) {
            return actionFormPropertyDesc.getValue(action);
        }
        return action;
    }

    @Override
    public String getType() {
        return componentDef.getComponentClass().getName();
    }

    /**
     * 実行メソッド名の配列を返します。
     * 
     * @return 実行メソッド名の配列
     */
    public String[] getExecuteMethodNames() {
        return executeConfigs.keySet().toArray(
                new String[executeConfigs.size()]);
    }

    /**
     * 実行設定を返します。
     * 
     * @param name
     *            名前
     * @return 実行設定
     */
    public S2ExecuteConfig getExecuteConfig(String name) {
        return executeConfigs.get(name);
    }

    /**
     * 実行設定を追加します。
     * 
     * @param executeConfig
     *            実行設定
     */
    public void addExecuteConfig(S2ExecuteConfig executeConfig) {
        executeConfigs.put(executeConfig.method.getName(), executeConfig);
    }
}