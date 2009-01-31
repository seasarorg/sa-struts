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

import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.StringUtil;

/**
 * リクエストに関するユーティリティです。
 * 
 * @author higa
 * 
 */
public final class RequestUtil {

    private RequestUtil() {
    }

    /**
     * リクエストを返します。
     * 
     * @return リクエスト
     */
    public static HttpServletRequest getRequest() {
        return SingletonS2Container.getComponent(HttpServletRequest.class);
    }

    /**
     * パスを返します。
     * 
     * @return パス
     */
    public static String getPath() {
        return getPath(getRequest());
    }

    /**
     * パスを返します。
     * 
     * @param request
     *            リクエスト
     * @return パス
     */
    public static String getPath(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (StringUtil.isEmpty(path)) {
            path = request.getServletPath();
        }
        if (path == null) {
            return null;
        }
        String viewPrefix = ServletContextUtil.getViewPrefix();
        if (viewPrefix == null) {
            return path;
        }
        if (path.startsWith(viewPrefix)) {
            path = path.substring(viewPrefix.length());
        }
        return path;
    }
}