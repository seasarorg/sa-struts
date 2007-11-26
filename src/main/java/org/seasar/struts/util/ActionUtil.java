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
package org.seasar.struts.util;

/**
 * Action用のユーティリティです。
 * 
 * @author higa
 * 
 */
public final class ActionUtil {

    private static final String SUFFIX = "Action";

    private ActionUtil() {
    }

    /**
     * パスをアクション名に変換します。
     * 
     * @param path
     *            パス
     * @return アクション名
     */
    public static String fromPathToActionName(String path) {
        String servletMapping = ServletContextUtil.getServletMapping();
        if (servletMapping.startsWith("*.")) {
            path = path.substring(1, path.length() - servletMapping.length()
                    + 1);
        } else if (servletMapping.endsWith("/*")) {
            path = path.substring(servletMapping.length() - 1);
        } else if (servletMapping.equals("/")) {
            path = path.substring(1);
        }
        return path.replace('/', '_') + SUFFIX;
    }

    /**
     * アクション名をパスに変換します。
     * 
     * @param actionName
     *            アクション名
     * @return パス
     */
    public static String fromActionNameToPath(String actionName) {
        actionName = actionName.replace('_', '/').substring(0,
                actionName.length() - SUFFIX.length());
        String servletMapping = ServletContextUtil.getServletMapping();
        if (servletMapping.startsWith("*.")) {
            actionName = "/" + actionName + servletMapping.substring(1);
        } else if (servletMapping.endsWith("/*")) {
            actionName = servletMapping.substring(0,
                    servletMapping.length() - 1)
                    + actionName;
        } else if (servletMapping.equals("/")) {
            actionName = "/" + actionName;
        }
        return actionName;
    }
}
