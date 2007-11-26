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
     * アクション名です。
     */
    protected String actionName;

    /**
     * 実行設定のマップです
     */
    protected Map<String, S2ExecuteConfig> executeConfigs = new HashMap<String, S2ExecuteConfig>();

    /**
     * アクション名を返します。
     * 
     * @return アクション名
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * アクション名を設定します。
     * 
     * @param actionName
     *            アクション名
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
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
}