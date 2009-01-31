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
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * ActionForm用のユーティリティです。
 * 
 * @author higa
 * 
 */
public final class ActionFormUtil {

    private ActionFormUtil() {
    }

    /**
     * アクションフォームを返します。
     * 
     * @param request
     *            リクエスト
     * @param mapping
     *            アクションマッピング
     * @return アクションフォーム
     * 
     * 
     */
    public static ActionForm getActionForm(HttpServletRequest request,
            ActionMapping mapping) {
        if ("request".equals(mapping.getScope())) {
            return (ActionForm) request.getAttribute(mapping.getAttribute());
        }
        HttpSession session = request.getSession();
        return (ActionForm) session.getAttribute(mapping.getAttribute());
    }
}