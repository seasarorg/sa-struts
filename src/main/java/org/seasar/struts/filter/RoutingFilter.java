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
package org.seasar.struts.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.RoutingUtil;
import org.seasar.struts.util.S2ExecuteConfigUtil;

/**
 * リクエストされたURLを適切なアクションに振り分けるフィルタです。
 * 
 * @author higa
 */
public class RoutingFilter implements Filter {

    /**
     * JSPのダイレクトアクセスを許すかどうかです。
     */
    protected boolean jspDirectAccess = false;

    public void init(FilterConfig config) throws ServletException {
        String access = config.getInitParameter("jspDirectAccess");
        if (!StringUtil.isBlank(access)) {
            jspDirectAccess = Boolean.valueOf(access);
        }
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String contextPath = req.getContextPath();
        if (contextPath.equals("/")) {
            contextPath = "";
        }
        String path = RequestUtil.getPath(req);
        if (!processDirectAccess(request, response, chain, path)) {
            return;
        }
        if (path.indexOf('.') < 0) {
            String[] names = StringUtil.split(path, "/");
            S2Container container = SingletonS2ContainerFactory.getContainer();
            StringBuilder sb = new StringBuilder(50);
            for (int i = 0; i < names.length; i++) {
                if (container.hasComponentDef(sb + names[i] + "Action")) {
                    String actionPath = RoutingUtil.getActionPath(names, i);
                    String paramPath = RoutingUtil.getParamPath(names, i + 1);
                    if (StringUtil.isEmpty(paramPath)) {
                        if (!path.endsWith("/")) {
                            String queryString = "";
                            if (req.getQueryString() != null) {
                                queryString = "?" + req.getQueryString();
                            }
                            res.sendRedirect(contextPath + path + "/"
                                    + queryString);
                            return;
                        } else if (S2ExecuteConfigUtil.findExecuteConfig(
                                actionPath, req) != null) {
                            forward((HttpServletRequest) request,
                                    (HttpServletResponse) response, actionPath,
                                    null, null);
                            return;
                        }
                    } else {
                        S2ExecuteConfig executeConfig = S2ExecuteConfigUtil
                                .findExecuteConfig(actionPath, paramPath);
                        if (executeConfig != null) {
                            forward((HttpServletRequest) request,
                                    (HttpServletResponse) response, actionPath,
                                    paramPath, executeConfig);
                            return;
                        }
                    }
                }
                if (container.hasComponentDef(sb + "indexAction")) {
                    String actionPath = RoutingUtil.getActionPath(names, i - 1)
                            + "/index";
                    String paramPath = RoutingUtil.getParamPath(names, i);
                    if (StringUtil.isEmpty(paramPath)) {
                        if (!path.endsWith("/")) {
                            String queryString = "";
                            if (req.getQueryString() != null) {
                                queryString = "?" + req.getQueryString();
                            }
                            res.sendRedirect(contextPath + path + "/"
                                    + queryString);
                            return;
                        } else if (S2ExecuteConfigUtil.findExecuteConfig(
                                actionPath, req) != null) {
                            forward((HttpServletRequest) request,
                                    (HttpServletResponse) response, actionPath,
                                    null, null);
                            return;
                        }
                    } else {
                        S2ExecuteConfig executeConfig = S2ExecuteConfigUtil
                                .findExecuteConfig(actionPath, paramPath);
                        if (executeConfig != null) {
                            forward((HttpServletRequest) request,
                                    (HttpServletResponse) response, actionPath,
                                    paramPath, executeConfig);
                            return;
                        }
                    }
                }
                sb.append(names[i] + "_");
            }
            if (container.hasComponentDef(sb + "indexAction")) {
                String actionPath = RoutingUtil.getActionPath(names,
                        names.length - 1)
                        + "/index";
                if (!path.endsWith("/")) {
                    String queryString = "";
                    if (req.getQueryString() != null) {
                        queryString = "?" + req.getQueryString();
                    }
                    res.sendRedirect(contextPath + path + "/" + queryString);
                    return;
                } else if (S2ExecuteConfigUtil.findExecuteConfig(actionPath,
                        req) != null) {
                    forward((HttpServletRequest) request,
                            (HttpServletResponse) response, actionPath, null,
                            null);
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * ダイレクトアクセスを処理します。
     * 
     * @param request
     *            リクエスト
     * @param response
     *            レスポンス
     * @param chain
     *            フィルタチェイン
     * @param path
     *            パス
     * @return JSPのダイレクトアクセスのチェックがNGの場合は、 falseを返します。
     * @throws IOException
     *             IO例外が発生した場合。
     */
    protected boolean processDirectAccess(ServletRequest request,
            ServletResponse response, FilterChain chain, String path)
            throws IOException {
        if (!jspDirectAccess
                && ((HttpServletRequest) request).getMethod().equalsIgnoreCase(
                        "get") && path.endsWith(".jsp")) {
            String message = "Direct access for JSP is not permitted.";
            if (path.endsWith("index.jsp")) {
                message += " Remove \"index.jsp\" from welcome-file-list of (default) \"web.xml\".";
            }
            ((HttpServletResponse) response).sendError(
                    HttpServletResponse.SC_BAD_REQUEST, message);
            return false;
        }
        return true;
    }

    /**
     * Strutsのサーブレットにフォワードします。
     * 
     * @param request
     *            リクエスト
     * @param response
     *            レスポンス
     * @param actionPath
     *            アクションパス
     * @param paramPath
     *            パラメータのパス
     * @param executeConfig
     *            実行設定
     * @throws IOException
     *             IO例外が発生した場合
     * @throws ServletException
     *             サーブレット例外が発生した場合
     */
    protected void forward(HttpServletRequest request,
            HttpServletResponse response, String actionPath, String paramPath,
            S2ExecuteConfig executeConfig) throws IOException, ServletException {
        String forwardPath = actionPath + ".do";
        if (executeConfig != null) {
            forwardPath = forwardPath + executeConfig.getQueryString(paramPath);
        }
        request.getRequestDispatcher(forwardPath).forward(request, response);
    }
}