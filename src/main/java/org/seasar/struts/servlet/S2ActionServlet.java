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
package org.seasar.struts.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.config.ModuleConfig;
import org.seasar.framework.container.SingletonS2Container;

/**
 * @author higa
 */
public class S2ActionServlet extends ActionServlet {

    private static final long serialVersionUID = 0L;

    /**
     * サーブレットコンテキストです。
     */
    protected ServletContext servletContext;

    public ServletContext getServletContext() {
        if (servletContext != null) {
            return servletContext;
        }
        return super.getServletContext();
    }

    /**
     * サーブレットコンテキストを設定します。
     * 
     * @param servletContext
     *            サーブレットコンテキスト
     */
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * モジュールに対応したリクエストプロセッサを返します。
     * 
     * @param config
     *            モジュール設定
     * 
     * @exception ServletException
     *                リクエストプロセッサが生成できない場合
     */
    protected synchronized RequestProcessor getRequestProcessor(
            ModuleConfig config) throws ServletException {
        RequestProcessor processor = getProcessorForModule(config);
        if (processor == null) {
            processor = SingletonS2Container
                    .getComponent(RequestProcessor.class);
            processor.init(this, config);
            String key = Globals.REQUEST_PROCESSOR_KEY + config.getPrefix();
            getServletContext().setAttribute(key, processor);
        }
        return processor;

    }

    /**
     * サーブレットコンテキストにキャッシュしているリクエストプロセッサを返します。
     * 
     * @param config
     *            モジュール設定
     * @return リクエストプロセッサ
     */
    protected RequestProcessor getProcessorForModule(ModuleConfig config) {
        String key = Globals.REQUEST_PROCESSOR_KEY + config.getPrefix();
        return (RequestProcessor) getServletContext().getAttribute(key);
    }
}