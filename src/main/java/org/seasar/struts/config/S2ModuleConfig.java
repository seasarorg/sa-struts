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

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.impl.ModuleConfigImpl;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.container.hotdeploy.HotdeployUtil;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;
import org.seasar.struts.util.ActionUtil;

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
        BeanUtilsBean.setInstance(new BeanUtilsBean());
        initialized = false;
    }

    @Override
    public ActionConfig findActionConfig(String path) {
        if (!initialized) {
            initialize();
        }
        ActionConfig ac = (ActionConfig) actionConfigs.get(path);
        if (ac == null && HotdeployUtil.isHotdeploy()) {
            SingletonS2ContainerFactory.getContainer().getComponent(
                    ActionUtil.fromPathToActionName(path));
        }
        return (ActionConfig) actionConfigs.get(path);
    }

    @Override
    public void freeze() {
    }
}