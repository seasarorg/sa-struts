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

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.ArrayMap;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.RoutingUtil;
import org.seasar.struts.util.S2ExecuteConfigUtil;
import org.seasar.struts.util.S2ModuleConfigUtil;
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
     * アクションフォーム用のプロパティ記述です。
     */
    protected PropertyDesc actionFormPropertyDesc;

    /**
     * リセットメソッドです。
     */
    protected Method resetMethod;

    /**
     * インスタンスを構築します。
     */
    public S2ActionMapping() {
        scope = "request";
        validate = false;
    }

    /**
     * @param path
     * @return
     */
    /**
     * アクションフォワードを作成します。
     * 
     * @param path
     *            パス
     * @return アクションフォワード
     */
    public ActionForward createForward(String path) {
        if (path == null) {
            return null;
        }
        boolean redirect = false;
        if (path.endsWith(REDIRECT)) {
            redirect = true;
            path = path.substring(0, path.length() - REDIRECT.length() - 1);
        }
        if (path.indexOf(":") < 0) {
            if (!path.startsWith("/")) {
                path = getActionPath(componentDef.getComponentName()) + path;
            }
            if (path.indexOf('.') < 0 && !redirect) {
                path = createRoutingPath(path);
            } else {
                String viewPrefix = ServletContextUtil.getViewPrefix();
                if (viewPrefix != null) {
                    path = viewPrefix + path;
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
                            + getQueryString(queryString, actionPath, paramPath);
                }
                S2ExecuteConfig executeConfig = S2ExecuteConfigUtil
                        .findExecuteConfig(actionPath, paramPath);
                if (executeConfig != null) {
                    return actionPath
                            + ".do"
                            + getQueryString(queryString, actionPath, paramPath);
                }
            }
            sb.append(names[i] + "_");
        }
        if (container.hasComponentDef("indexAction")) {
            String actionPath = "/";
            String paramPath = RoutingUtil.getParamPath(names, 0);
            if (StringUtil.isEmpty(paramPath)) {
                return "/index.do"
                        + getQueryString(queryString, actionPath, paramPath);
            }
            S2ExecuteConfig executeConfig = S2ExecuteConfigUtil
                    .findExecuteConfig(actionPath, paramPath);
            if (executeConfig != null) {
                return "/index.do"
                        + getQueryString(queryString, actionPath, paramPath);
            }
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
        if (componentName.equals("indexAction")) {
            return "/";
        }
        if (componentName.endsWith("Action")) {
            return "/"
                    + componentName.substring(0, componentName.length() - 6)
                            .replace('_', '/') + "/";
        }
        throw new IllegalArgumentException(componentName);
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
     * @return クエリストリング
     */
    protected String getQueryString(String queryString, String actionPath,
            String paramPath) {
        String queryString2 = "";
        S2ModuleConfig moduleConfig = S2ModuleConfigUtil.getModuleConfig();
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig(actionPath);
        S2ExecuteConfig executeConfig = actionMapping
                .findExecuteConfig(paramPath);
        if (executeConfig != null) {
            queryString2 = executeConfig.getQueryString(paramPath);
        } else {
            executeConfig = S2ExecuteConfigUtil.getExecuteConfig();
            if (executeConfig != null) {
                queryString2 = "?" + executeConfig.method.getName() + "=";
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
     * アクションフォームのプロパティ記述を返します。
     * 
     * @return アクションフォームのプロパティ記述
     */
    public PropertyDesc getActionFormPropertyDesc() {
        return actionFormPropertyDesc;
    }

    /**
     * アクションフォームのプロパティ記述を設定します。
     * 
     * @param actionFormPropertyDesc
     *            アクションフォームのプロパティ記述
     */
    public void setActionFormPropertyDesc(PropertyDesc actionFormPropertyDesc) {
        this.actionFormPropertyDesc = actionFormPropertyDesc;
        actionFormBeanDesc = BeanDescFactory.getBeanDesc(actionFormPropertyDesc
                .getPropertyType());
    }

    /**
     * リセットメソッドを返します。
     * 
     * @return リセットメソッド
     */
    public Method getResetMethod() {
        return resetMethod;
    }

    /**
     * リセットメソッドを設定します。
     * 
     * @param resetMethod
     *            リセットメソッド
     */
    public void setResetMethod(Method resetMethod) {
        this.resetMethod = resetMethod;
    }
}