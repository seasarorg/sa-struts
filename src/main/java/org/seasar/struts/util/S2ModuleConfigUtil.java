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

import org.apache.struts.Globals;
import org.seasar.struts.config.S2ModuleConfig;

/**
 * モジュール設定に関するユーティリティです。
 * 
 * @author higa
 * 
 */
public final class S2ModuleConfigUtil {

    private S2ModuleConfigUtil() {
    }

    /**
     * モジュール設定を返します。
     * 
     * @return モジュール設定
     */
    public static S2ModuleConfig getModuleConfig() {
        return (S2ModuleConfig) ServletContextUtil.getServletContext()
                .getAttribute(Globals.MODULE_KEY);
    }
}