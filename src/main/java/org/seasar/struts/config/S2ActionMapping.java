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
package org.seasar.struts.config;

import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.ArrayMap;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.RoutingUtil;
import org.seasar.struts.util.S2ExecuteConfigUtil;
import org.seasar.struts.util.ServletContextUtil;

/**
 * Seasar2用のアクションマッピングです。
 * 
 * @author higa
 * 
 */
public class S2ActionMapping extends ActionMapping {

    private static final long serialVersionUID = 1L;

    private static final String REDIRECT = "redirect=true";

    /**
     * コンポーネント定義です。
     */
    protected ComponentDef componentDef;

    /**
     * アクションフォームのコンポーネント定義です。
     */
    protected ComponentDef actionFormComponentDef;

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
    protected ArrayMap executeConfigs = new ArrayMap();

    /**
     * アクションフォーム用のフィールドです。
     */
    protected Field actionFormField;

    /**
     * インスタンスを構築します。
     */
    public S2ActionMapping() {
        scope = "request";
        validate = false;
    }

    /**
     * アクションフォワードを作成します。
     * 
     * @param path
     *            パス
     * @return アクションフォワード
     */
    public ActionForward createForward(String path) {
        return createForward(path, false);
    }

    /**
     * アクションフォワードを作成します。
     * 
     * @param path
     *            パス
     * @param redirect
     *            リダイレクトするかどうか
     * @return アクションフォワード
     */
    public ActionForward createForward(String path, boolean redirect) {
        if (path == null) {
            return null;
        }
        if (path.endsWith(REDIRECT)) {
            redirect = true;
            path = path.substring(0, path.length() - REDIRECT.length() - 1);
        }
        if (path.indexOf(":") < 0) {
            if (!path.startsWith("/")) {
                path = getActionPath(componentDef.getComponentName()) + path;
            }
            if (!redirect) {
                if (path.indexOf('.') < 0) {
                    path = createRoutingPath(path);
                } else {
                    String viewPrefix = ServletContextUtil.getViewPrefix();
                    if (viewPrefix != null) {
                        path = viewPrefix + path;
                    }
                }
            }
        }
        return new ActionForward(path, redirect);
    }

    /**
     * ルーティング用のパスを作成します。
     * 
     * @param path
     *            パス
     * @return ルーティング用のパス
     */
    protected String createRoutingPath(String path) {
        String originalPath = path;
        String queryString = "";
        int index = path.indexOf('?');
        if (index >= 0) {
            queryString = path.substring(index);
            path = path.substring(0, index);
        }
        String[] names = StringUtil.split(path, "/");
        S2Container container = SingletonS2ContainerFactory.getContainer();
        StringBuilder sb = new StringBuilder(50);
        for (int i = 0; i < names.length; i++) {
            if (container.hasComponentDef(sb + names[i] + "Action")) {
                String actionPath = RoutingUtil.getActionPath(names, i);
                String paramPath = RoutingUtil.getParamPath(names, i + 1);
                if (StringUtil.isEmpty(paramPath)) {
                    return actionPath
                            + ".do"
                            + getQueryString(queryString, actionPath,
                                    paramPath, null);
                }
                S2ExecuteConfig executeConfig = S2ExecuteConfigUtil
                        .findExecuteConfig(actionPath, paramPath);
                if (executeConfig != null) {
                    return actionPath
                            + ".do"
                            + getQueryString(queryString, actionPath,
                                    paramPath, executeConfig);
                }
            }
            if (container.hasComponentDef(sb + "indexAction")) {
                String actionPath = RoutingUtil.getActionPath(names, i - 1)
                        + "/index";
                String paramPath = RoutingUtil.getParamPath(names, i);
                if (StringUtil.isEmpty(paramPath)) {
                    return actionPath
                            + ".do"
                            + getQueryString(queryString, actionPath,
                                    paramPath, null);
                }
                S2ExecuteConfig executeConfig = S2ExecuteConfigUtil
                        .findExecuteConfig(actionPath, paramPath);
                if (executeConfig != null) {
                    return actionPath
                            + ".do"
                            + getQueryString(queryString, actionPath,
                                    paramPath, executeConfig);
                }
            }
            sb.append(names[i] + "_");
        }
        if (container.hasComponentDef(sb + "indexAction")) {
            String actionPath = RoutingUtil.getActionPath(names,
                    names.length - 1)
                    + "/index";
            return actionPath + ".do"
                    + getQueryString(queryString, actionPath, "", null);
        }
        return originalPath;
    }

    /**
     * Viewのディレクトリを返します。
     * 
     * @param componentName
     *            アクションのコンポーネント名
     * @return Viewのディレクトリ
     */
    protected String getActionPath(String componentName) {
        if (!componentName.endsWith("Action")) {
            throw new IllegalArgumentException(componentName);
        }
        if (componentName.equals("indexAction")) {
            return "/";
        }
        if (componentName.endsWith("indexAction")) {
            componentName = componentName.substring(0,
                    componentName.length() - 12);
        } else {
            componentName = componentName.substring(0,
                    componentName.length() - 6);
        }
        return "/" + componentName.replace('_', '/') + "/";

    }

    /**
     * クエリストリングを返します。
     * 
     * @param queryString
     *            元のクエリストリング
     * @param actionPath
     *            アクションパス
     * @param paramPath
     *            パラメータ用のパス
     * @param executeConfig
     *            実行設定
     * @return クエリストリング
     */
    protected String getQueryString(String queryString, String actionPath,
            String paramPath, S2ExecuteConfig executeConfig) {
        String queryString2 = "";
        if (executeConfig != null) {
            queryString2 = executeConfig.getQueryString(paramPath);
        } else {
            executeConfig = getExecuteConfig("index");
            if (executeConfig != null) {
                queryString2 = executeConfig.getQueryString(paramPath);
            }
        }
        if (StringUtil.isEmpty(queryString)) {
            return queryString2;
        }
        if (StringUtil.isEmpty(queryString2)) {
            return queryString;
        }
        return queryString + "&" + queryString2.substring(1);
    }

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
    }

    /**
     * アクションフォームのコンポーネント定義を返します。
     * 
     * @return アクションフォームのコンポーネント定義
     */
    public ComponentDef getActionFormComponentDef() {
        if (actionFormField == null) {
            return componentDef;
        }
        if (actionFormComponentDef == null) {
            actionFormComponentDef = SingletonS2ContainerFactory.getContainer()
                    .getComponentDef(actionFormField.getType());
        }
        return actionFormComponentDef;
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
        if (actionFormField == null) {
            return actionBeanDesc;
        }
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
        return getActionFormComponentDef().getComponent();
    }

    /**
     * プロパティの値を返します。
     * 
     * @param name
     *            プロパティ名
     * @return プロパティの値
     */
    public String getPropertyAsString(String name) {
        Object target = getActionForm();
        BeanDesc beanDesc = getActionFormBeanDesc();
        Object value = null;
        if (beanDesc.hasPropertyDesc(name)) {
            value = beanDesc.getPropertyDesc(name).getValue(target);
        }
        if (value != null) {
            return value.toString();
        }
        target = getAction();
        beanDesc = getActionBeanDesc();
        if (beanDesc.hasPropertyDesc(name)) {
            value = beanDesc.getPropertyDesc(name).getValue(target);
        }
        if (value != null) {
            return value.toString();
        }
        return "null";
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
    @SuppressWarnings("unchecked")
    public String[] getExecuteMethodNames() {
        return (String[]) executeConfigs.keySet().toArray(
                new String[executeConfigs.size()]);
    }

    /**
     * 実行メソッドを探します。
     * 
     * @param paramPath
     *            パラメータのパス
     * @return 実行メソッド
     */
    public S2ExecuteConfig findExecuteConfig(String paramPath) {
        for (int i = 0; i < executeConfigs.size(); i++) {
            S2ExecuteConfig executeConfig = (S2ExecuteConfig) executeConfigs
                    .get(i);
            if (executeConfig.isTarget(paramPath)) {
                return executeConfig;
            }
        }
        return null;
    }

    /**
     * 実行メソッドを探します。
     * 
     * @param request
     *            リクエスト
     * @return 実行メソッド
     */
    public S2ExecuteConfig findExecuteConfig(HttpServletRequest request) {
        if (executeConfigs.size() == 1) {
            return (S2ExecuteConfig) executeConfigs.get(0);
        }
        for (int i = 0; i < executeConfigs.size(); i++) {
            S2ExecuteConfig executeConfig = (S2ExecuteConfig) executeConfigs
                    .get(i);
            if (executeConfig.isTarget(request)) {
                return executeConfig;
            }
        }
        return getExecuteConfig("index");
    }

    /**
     * 実行設定を返します。
     * 
     * @param name
     *            名前
     * @return 実行設定
     */
    public S2ExecuteConfig getExecuteConfig(String name) {
        return (S2ExecuteConfig) executeConfigs.get(name);
    }

    /**
     * 実行設定の数を返します。
     * 
     * @return 実行設定の数
     */
    public int getExecuteConfigSize() {
        return executeConfigs.size();
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
     * アクションフォーム用のフィールドを返します。
     * 
     * @return アクションフォーム用のフィールド
     */
    public Field getActionFormField() {
        return actionFormField;
    }

    /**
     * アクションフォーム用ののフィールドを設定します。
     * 
     * @param actionFormField
     *            アクションフォーム用のフィールド
     */
    public void setActionFormField(Field actionFormField) {
        this.actionFormField = actionFormField;
        actionFormBeanDesc = BeanDescFactory.getBeanDesc(actionFormField
                .getType());
    }
}