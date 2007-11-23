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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.impl.ModuleConfigImpl;

/**
 * Seasar2用のモジュール設定です。
 * 
 * @author higa
 * 
 */
public class S2ModuleConfig extends ModuleConfigImpl {

    private static final long serialVersionUID = 1L;

    /**
     * サーブレットのマッピングです。
     */
    protected String servletMapping;

    /**
     * アクション設定のキャッシュです。
     */
    protected ConcurrentHashMap<String, ActionConfig> actionConfigMap = new ConcurrentHashMap<String, ActionConfig>(
            200);

    /**
     * インスタンスを構築します。
     * 
     * @param applicationScope
     *            applicationスコープ
     * 
     */
    public S2ModuleConfig(Map<String, Object> applicationScope) {
        setup(applicationScope);
    }

    @Override
    public ActionConfig findActionConfig(String path) {
        ActionConfig actionConfig = actionConfigMap.get(path);
        if (actionConfig != null) {
            return actionConfig;
        }
        actionConfig = createActionMapping(path);
        ActionConfig actionConfig2 = actionConfigMap.putIfAbsent(path,
                actionConfig);
        return actionConfig2 != null ? actionConfig2 : actionConfig;
    }

    protected ActionMapping createActionMapping(String path) {
        return null;
    }

    /**
     * パスをアクション名に変換します。
     * 
     * @param path
     *            パス
     * @return アクション名
     */
    protected String fromPathToActionName(String path) {
        if (servletMapping.startsWith("*.")) {
            path = path.substring(1, path.length() - servletMapping.length()
                    + 1);
        } else if (servletMapping.endsWith("/*")) {
            path = path.substring(servletMapping.length() - 1);
        } else if (servletMapping.equals("/") || servletMapping.equals("*")) {
            path = path.substring(1);
        }
        return path.replace('/', '_') + "Action";
    }

    /**
     * セットアップをします。
     * 
     * @param applicationScope
     *            applicationスコープです。
     * 
     */
    protected void setup(Map<String, Object> applicationScope) {
        servletMapping = (String) applicationScope.get(Globals.SERVLET_KEY);
    }
}