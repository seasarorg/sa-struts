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
import org.seasar.struts.util.S2ExecuteConfigUtil;
import org.seasar.struts.util.S2ModuleConfigUtil;

/**
 * リクエストされたURLを適切なアクションに振り分けるフィルタです。
 * 
 * @author higa
 */
public class RoutingFilter implements Filter {

    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        String path = getPath((HttpServletRequest) request);
        if (path.indexOf('.') < 0) {
            String[] names = StringUtil.split(path, "/");
            S2Container container = SingletonS2ContainerFactory.getContainer();
            StringBuilder sb = new StringBuilder(50);
            for (int i = 0; i < names.length; i++) {
                if (container.hasComponentDef(sb + names[i] + "Action")) {
                    String actionPath = getActionPath(names, i);
                    String paramPath = getParamPath(names, i + 1);
                    forward((HttpServletRequest) request,
                            (HttpServletResponse) response, actionPath,
                            paramPath);
                    return;
                }
                sb.append(names[i] + "_");
            }
            if (container.hasComponentDef("indexAction")) {
                forward((HttpServletRequest) request,
                        (HttpServletResponse) response, "/", path.substring(1));
                return;
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
     * アクションのパスを返します。
     * 
     * @param names
     *            パスを/で区切った配列
     * @param index
     *            インデックス
     * @return アクションのパス
     */
    protected String getActionPath(String[] names, int index) {
        StringBuilder sb = new StringBuilder(30);
        for (int i = 0; i <= index; i++) {
            sb.append('/').append(names[i]);
        }
        return sb.toString();
    }

    /**
     * パラメータのパスを返します。
     * 
     * @param names
     *            パスを/で区切った配列
     * @param index
     *            インデックス
     * @return パラメータのパス
     */
    protected String getParamPath(String[] names, int index) {
        StringBuilder sb = new StringBuilder(30);
        for (int i = index; i < names.length; i++) {
            if (i != index) {
                sb.append('/');
            }
            sb.append(names[i]);
        }
        return sb.toString();
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
     * @throws IOException
     *             IO例外が発生した場合
     * @throws ServletException
     *             サーブレット例外が発生した場合
     */
    protected void forward(HttpServletRequest request,
            HttpServletResponse response, String actionPath, String paramPath)
            throws IOException, ServletException {
        S2ModuleConfig moduleConfig = S2ModuleConfigUtil.getModuleConfig();
        S2ActionMapping actionMapping = (S2ActionMapping) moduleConfig
                .findActionConfig(actionPath);
        String forwardPath = actionPath + ".do";
        S2ExecuteConfig executeConfig = actionMapping.findExecuteConfig(
                request, paramPath);
        if (executeConfig != null) {
            forwardPath = forwardPath + executeConfig.getQueryString(paramPath);
            S2ExecuteConfigUtil.setExecuteConfig(executeConfig);
        }
        request.getRequestDispatcher(forwardPath).forward(request, response);
    }
}