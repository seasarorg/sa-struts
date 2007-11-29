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
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.util.ArrayMap;
import org.seasar.struts.action.S2DynaProperty;

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
     * Bean記述です。
     */
    protected BeanDesc beanDesc;

    /**
     * 実行設定のマップです
     */
    protected Map<String, S2ExecuteConfig> executeConfigs = new HashMap<String, S2ExecuteConfig>();

    /**
     * 動的プロパティの集合です。
     */
    protected ArrayMap dynaProperties = new ArrayMap();

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
        beanDesc = BeanDescFactory
                .getBeanDesc(componentDef.getComponentClass());
    }

    /**
     * Bean記述を返します。
     * 
     * @return Bean記述
     */
    public BeanDesc getBeanDesc() {
        return beanDesc;
    }

    /**
     * POJOアクションフォームを返します。
     * 
     * @return POJOアクションフォーム
     */
    public Object getActionForm() {
        return componentDef.getComponent();
    }

    /**
     * POJOアクションフォームのクラスを返します。
     * 
     * @return POJOアクションフォームのクラス
     */
    public Class<?> getActionFormClass() {
        return componentDef.getComponentClass();
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
}