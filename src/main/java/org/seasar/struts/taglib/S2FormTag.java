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
package org.seasar.struts.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.FormTag;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionUtil;
import org.seasar.struts.util.RoutingUtil;

/**
 * Seasar2用のFormTagです。
 * 
 * @author higa
 * 
 */
public class S2FormTag extends FormTag {

    private static final long serialVersionUID = 1L;

    /**
     * onkeypressのイベントを定義します。
     */
    protected String onkeypress;

    /**
     * onkeyupのイベントを定義します。
     */
    protected String onkeyup;

    /**
     * onkeydownのイベントを定義します。
     */
    protected String onkeydown;

    /**
     * onkeypressのイベント定義を返します。
     * 
     * @return onkeypressのイベント定義
     */
    public String getOnkeypress() {
        return onkeypress;
    }

    /**
     * onkeypressのイベント定義を設定します。
     * 
     * @param onkeypress
     *            onkeypressのイベント定義
     */
    public void setOnkeypress(String onkeypress) {
        this.onkeypress = onkeypress;
    }

    /**
     * onkeyupのイベント定義を返します。
     * 
     * @return onkeyupのイベント定義
     */
    public String getOnkeyup() {
        return onkeyup;
    }

    /**
     * onkeyupのイベント定義を設定します。
     * 
     * @param onkeyup
     *            onkeyupのイベント定義
     */
    public void setOnkeyup(String onkeyup) {
        this.onkeyup = onkeyup;
    }

    /**
     * onkeydownのイベント定義を返します。
     * 
     * @return onkeydownのイベント定義
     */
    public String getOnkeydown() {
        return onkeydown;
    }

    /**
     * onkeydownのイベント定義を設定します。
     * 
     * @param onkeydown
     *            onkeydownのイベント定義
     */
    public void setOnkeydown(String onkeydown) {
        this.onkeydown = onkeydown;
    }

    @Override
    protected void lookup() throws JspException {
        moduleConfig = TagUtils.getInstance().getModuleConfig(pageContext);
        if (moduleConfig == null) {
            JspException e = new JspException(messages
                    .getMessage("formTag.collections"));
            pageContext.setAttribute(Globals.EXCEPTION_KEY, e,
                    PageContext.REQUEST_SCOPE);
            throw e;
        }
        servlet = (ActionServlet) pageContext.getServletContext().getAttribute(
                Globals.ACTION_SERVLET_KEY);
        if (action == null) {
            action = ActionUtil.calcActionPath();
        } else if (!action.startsWith("/")) {
            action = ActionUtil.calcActionPath() + action;
        } else {
            String[] names = StringUtil.split(action, "/");
            S2Container container = SingletonS2ContainerFactory.getContainer();
            StringBuilder sb = new StringBuilder(50);
            for (int i = 0; i < names.length; i++) {
                if (container.hasComponentDef(sb + names[i] + "Action")) {
                    String actionPath = RoutingUtil.getActionPath(names, i);
                    String paramPath = RoutingUtil.getParamPath(names, i + 1);
                    if (StringUtil.isEmpty(paramPath)) {
                        action = actionPath + "/";
                        break;
                    }
                }
                sb.append(names[i] + "_");
            }
        }
        mapping = (ActionMapping) moduleConfig.findActionConfig(action);
        if (mapping == null) {
            JspException e = new JspException(messages.getMessage(
                    "formTag.mapping", action));
            pageContext.setAttribute(Globals.EXCEPTION_KEY, e,
                    PageContext.REQUEST_SCOPE);
            throw e;
        }
        FormBeanConfig formBeanConfig = moduleConfig.findFormBeanConfig(mapping
                .getName());
        if (formBeanConfig == null) {
            JspException e = new JspException(messages.getMessage(
                    "formTag.formBean", mapping.getName(), action));
            pageContext.setAttribute(Globals.EXCEPTION_KEY, e,
                    PageContext.REQUEST_SCOPE);
            throw e;
        }
        beanName = mapping.getAttribute();
        beanScope = mapping.getScope();
        beanType = formBeanConfig.getType();
    }

    @Override
    protected void renderAction(StringBuffer results) {
        HttpServletRequest request = (HttpServletRequest) pageContext
                .getRequest();
        HttpServletResponse response = (HttpServletResponse) pageContext
                .getResponse();
        results.append(" action=\"");
        String contextPath = request.getContextPath();
        StringBuffer value = new StringBuffer();
        if (contextPath.length() > 1) {
            value.append(contextPath);
        }
        value.append(action);
        results.append(response.encodeURL(value.toString()));
        results.append("\"");
    }

    @Override
    public void release() {
        super.release();
        onkeypress = null;
        onkeyup = null;
        onkeydown = null;
    }

    @Override
    protected void renderOtherAttributes(StringBuffer results) {
        super.renderOtherAttributes(results);
        renderAttribute(results, "onkeypress", onkeypress);
        renderAttribute(results, "onkeyup", onkeyup);
        renderAttribute(results, "onkeydown", onkeydown);
    }

}