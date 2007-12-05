/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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
package org.seasar.struts.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.config.FormBeanConfig;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.struts.config.S2ActionMapping;

/**
 * Seasar2用のリクエストプロセッサです。
 * 
 * @author higa
 */
public class S2RequestProcessor extends RequestProcessor {

    @Override
    public HttpServletRequest processMultipart(HttpServletRequest request) {
        HttpServletRequest result = super.processMultipart(request);
        SingletonS2ContainerFactory.getContainer().getExternalContext()
                .setRequest(result);
        return result;
    }

    @Override
    protected ActionForm processActionForm(HttpServletRequest request,
            HttpServletResponse response, ActionMapping mapping) {

        String name = mapping.getName();
        if (name == null) {
            return null;
        }
        FormBeanConfig formConfig = moduleConfig.findFormBeanConfig(name);
        if (formConfig == null) {
            return null;
        }
        ActionForm actionForm = null;
        try {
            actionForm = formConfig.createActionForm(servlet);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug(" Storing ActionForm bean instance in scope '"
                    + mapping.getScope() + "' under attribute key '"
                    + mapping.getAttribute() + "'");
        }
        if ("request".equals(mapping.getScope())) {
            request.setAttribute(mapping.getAttribute(), actionForm);
        } else {
            HttpSession session = request.getSession();
            session.setAttribute(mapping.getAttribute(), actionForm);
        }
        return actionForm;

    }

    @Override
    protected Action processActionCreate(HttpServletRequest request,
            HttpServletResponse response, ActionMapping mapping)
            throws IOException {

        if (log.isTraceEnabled()) {
            log.trace("  Creating new Action instance");
        }
        Action action = null;
        try {
            action = new ActionWrapper(((S2ActionMapping) mapping));
        } catch (Exception e) {
            log.error(getInternal().getMessage("actionCreate",
                    mapping.getPath()), e);
            response
                    .sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            getInternal().getMessage("actionCreate",
                                    mapping.getPath()));
            return null;
        }
        action.setServlet(servlet);
        return action;
    }
}