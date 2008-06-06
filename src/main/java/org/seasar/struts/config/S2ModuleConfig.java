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

import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.impl.ModuleConfigImpl;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionUtil;
import org.seasar.struts.util.RoutingUtil;

/**
 * Seasar2用のモジュール設定です。
 * 
 * @author higa
 * 
 */
public class S2ModuleConfig extends ModuleConfigImpl implements Disposable {

    private static final long serialVersionUID = 1L;

    /**
     * 初期化されたかどうかです。
     */
    protected volatile boolean initialized;

    /**
     * インスタンスを構築します。
     * 
     * @param prefix
     *            プレフィックス
     */
    public S2ModuleConfig(String prefix) {
        super(prefix);
        initialize();
    }

    /**
     * 初期化を行ないます。
     */
    public void initialize() {
        DisposableUtil.add(this);
        initialized = true;
    }

    public void dispose() {
        actionConfigs.clear();
        actionConfigList.clear();
        formBeans.clear();
        initialized = false;
    }

    @Override
    public ActionConfig findActionConfig(String path) {
        if (!initialized) {
            initialize();
        }
        if (path == null) {
            path = ActionUtil.calcActionPath();
        } else if (!path.startsWith("/")) {
            path = ActionUtil.calcActionPath() + path;
        }
        int index = path.indexOf('?');
        if (index >= 0) {
            path = path.substring(0, index);
        }
        ActionConfig ac = (ActionConfig) actionConfigs.get(path);
        if (ac != null) {
            return ac;
        }
        if (path.indexOf('.') < 0) {
            String[] names = StringUtil.split(path, "/");
            S2Container container = SingletonS2ContainerFactory.getContainer();
            StringBuilder sb = new StringBuilder(50);
            for (int i = 0; i < names.length; i++) {
                if (container.hasComponentDef(sb + names[i] + "Action")) {
                    String actionPath = RoutingUtil.getActionPath(names, i);
                    S2ActionMapping mapping = (S2ActionMapping) actionConfigs
                            .get(actionPath);
                    String paramPath = RoutingUtil.getParamPath(names, i + 1);
                    if (StringUtil.isEmpty(paramPath)) {
                        return mapping;
                    }
                    S2ExecuteConfig executeConfig = mapping
                            .findExecuteConfig(paramPath);
                    if (executeConfig != null) {
                        return mapping;
                    }
                }
                if (container.hasComponentDef(sb + "indexAction")) {
                    String actionPath = RoutingUtil.getActionPath(names, i - 1)
                            + "/index";
                    String paramPath = RoutingUtil.getParamPath(names, i);
                    S2ActionMapping mapping = (S2ActionMapping) actionConfigs
                            .get(actionPath);
                    if (StringUtil.isEmpty(paramPath)) {
                        return mapping;
                    }
                    S2ExecuteConfig executeConfig = mapping
                            .findExecuteConfig(paramPath);
                    if (executeConfig != null) {
                        return mapping;
                    }
                }
                sb.append(names[i] + "_");
            }
            if (container.hasComponentDef(sb + "indexAction")) {
                String actionPath = RoutingUtil.getActionPath(names,
                        names.length - 1)
                        + "/index";
                return (S2ActionMapping) actionConfigs.get(actionPath);
            }
        }
        return null;
    }

    @Override
    public void freeze() {
    }
}