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
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.config.S2ModuleConfig;
import org.seasar.struts.util.RoutingUtil;
import org.seasar.struts.util.S2ModuleConfigUtil;

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
        String path = getPath(req);
        if (!jspDirectAccess && req.getMethod().equalsIgnoreCase("get")
                && path.endsWith(".jsp")) {
            throw new ServletException(
                    "Direct access for JSP is not permitted.");
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
                        forward((HttpServletRequest) request,
                                (HttpServletResponse) response, actionPath,
                                null, null);
                        return;
                    }
                    S2ExecuteConfig executeConfig = findExecuteConfig(
                            actionPath, paramPath);
                    if (executeConfig != null) {
                        forward((HttpServletRequest) request,
                                (HttpServletResponse) response, actionPath,
                                paramPath, executeConfig);
                        return;
                    }

                }
                sb.append(names[i] + "_");
            }
            if (container.hasComponentDef("indexAction")) {
                String actionPath = "/index";
                String paramPath = RoutingUtil.getParamPath(names, 0);
                if (StringUtil.isEmpty(paramPath)) {
                    forward((HttpServletRequest) request,
                            (HttpServletResponse) response, actionPath, null,
                            null);
                    return;
                }
                S2ExecuteConfig executeConfig = findExecuteConfig(actionPath,
                        paramPath);
                if (executeConfig != null) {
                    forward((HttpServletRequest) request,
                            (HttpServletResponse) response, actionPath,
                            paramPath, executeConfig);
                    return;
                }

            }
        }
        chain.doFilter(request, response);
    }

    /**
     * パスを返します。
     * 
     * @param request
     *            リクエスト
     * @return パス
     */
    protected String getPath(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (!StringUtil.isEmpty(path)) {
            return path;
        }
        return request.getServletPath();
    }

    /**
     * 実行設定を返します。
     * 
     * @param actionPath
     *            アクションパス
     * @param paramPath
     *            パラメータパス
     * @return 実行設定
     */
    protected S2ExecuteConfig findExecuteConfig(String actionPath,
            String paramPath) {
        S2ModuleConfig moduleConfig = S2ModuleConfigUtil.getModuleConfig();
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig(actionPath);
        return actionMapping.findExecuteConfig(paramPath);
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