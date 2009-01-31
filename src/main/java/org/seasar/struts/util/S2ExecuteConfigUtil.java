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
package org.seasar.struts.util;

import javax.servlet.http.HttpServletRequest;

import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.config.S2ModuleConfig;

/**
 * Seasar2の実行設定に関するユーティリティです。
 * 
 * @author higa
 * 
 */
public final class S2ExecuteConfigUtil {

    private static final String KEY = S2ExecuteConfigUtil.class.getName();

    private S2ExecuteConfigUtil() {
    }

    /**
     * 実行設定を返します。
     * 
     * @return 実行設定
     */
    public static S2ExecuteConfig getExecuteConfig() {
        return (S2ExecuteConfig) RequestUtil.getRequest().getAttribute(KEY);
    }

    /**
     * 実行設定を設定します。
     * 
     * @param executeConfig
     *            実行設定
     */
    public static void setExecuteConfig(S2ExecuteConfig executeConfig) {
        RequestUtil.getRequest().setAttribute(KEY, executeConfig);
    }

    /**
     * 実行設定を探します。
     * 
     * @param actionPath
     *            アクションパス
     * @param paramPath
     *            パラメータパス
     * @return 実行設定
     */
    public static S2ExecuteConfig findExecuteConfig(String actionPath,
            String paramPath) {
        S2ModuleConfig moduleConfig = S2ModuleConfigUtil.getModuleConfig();
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig(actionPath);
        return actionMapping.findExecuteConfig(paramPath);
    }

    /**
     * 実行設定を探します。
     * 
     * @param actionPath
     *            アクションパス
     * @param request
     *            リクエスト
     * @return 実行設定
     */
    public static S2ExecuteConfig findExecuteConfig(String actionPath,
            HttpServletRequest request) {
        S2ModuleConfig moduleConfig = S2ModuleConfigUtil.getModuleConfig();
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig(actionPath);
        return actionMapping.findExecuteConfig(request);
    }
}