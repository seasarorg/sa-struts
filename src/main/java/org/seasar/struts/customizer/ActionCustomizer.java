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
package org.seasar.struts.customizer;

import org.apache.struts.config.ForwardConfig;
import org.seasar.framework.container.ComponentCustomizer;
import org.seasar.framework.container.ComponentDef;
import org.seasar.struts.annotation.Input;
import org.seasar.struts.annotation.Result;
import org.seasar.struts.annotation.Results;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ModuleConfig;
import org.seasar.struts.util.ActionUtil;
import org.seasar.struts.util.ServletContextUtil;

/**
 * Actionのカスタマイザです。
 * 
 * @author higa
 * 
 */
public class ActionCustomizer implements ComponentCustomizer {

    public void customize(ComponentDef componentDef) {
        S2ActionMapping actionMapping = createActionMapping(componentDef);
        S2ModuleConfig moduleConfig = ServletContextUtil.getModuleConfig();
        moduleConfig.addActionConfig(actionMapping);
    }

    /**
     * アクションマッピングを作成します。
     * 
     * @param componentDef
     *            コンポーネント定義
     * @return アクションマッピング
     */
    protected S2ActionMapping createActionMapping(ComponentDef componentDef) {
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setPath(ActionUtil.fromActionNameToPath(componentDef
                .getComponentName()));
        actionMapping.setScope("request");
        actionMapping.setType(componentDef.getComponentClass().getName());
        actionMapping.setActionName(componentDef.getComponentName());
        Class<?> actionClass = componentDef.getComponentClass();
        setupInput(actionMapping, actionClass);
        setupResult(actionMapping, actionClass);
        return actionMapping;
    }

    /**
     * 入力元の情報をセットアップします。
     * 
     * @param actionMapping
     *            アクションマッピング
     * @param actionClass
     *            アクションクラス
     */
    protected void setupInput(S2ActionMapping actionMapping,
            Class<?> actionClass) {
        Input input = actionClass.getAnnotation(Input.class);
        if (input == null) {
            return;
        }
        actionMapping.setInput(input.name());
        ForwardConfig forwardConfig = new ForwardConfig();
        forwardConfig.setName(input.name());
        forwardConfig.setPath(input.path());
        forwardConfig.setRedirect(input.redirect());
        actionMapping.addForwardConfig(forwardConfig);
    }

    /**
     * 遷移先の情報をセットアップします。
     * 
     * @param actionMapping
     *            アクションマッピング
     * @param actionClass
     *            アクションクラス
     */
    protected void setupResult(S2ActionMapping actionMapping,
            Class<?> actionClass) {
        Result result = actionClass.getAnnotation(Result.class);
        if (result != null) {
            setupResult(actionMapping, result);
            return;
        }
        Results results = actionClass.getAnnotation(Results.class);
        if (results != null) {
            for (Result r : results.value()) {
                setupResult(actionMapping, r);
            }
        }
    }

    /**
     * 遷移先の情報をセットアップします。
     * 
     * @param actionMapping
     *            アクションマッピング
     * @param result
     *            遷移先
     */
    protected void setupResult(S2ActionMapping actionMapping, Result result) {
        ForwardConfig forwardConfig = new ForwardConfig();
        forwardConfig.setName(result.name());
        forwardConfig.setPath(result.path());
        forwardConfig.setRedirect(result.redirect());
        actionMapping.addForwardConfig(forwardConfig);
    }
}