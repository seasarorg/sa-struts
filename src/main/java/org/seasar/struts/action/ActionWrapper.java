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
package org.seasar.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.seasar.framework.util.MethodUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;

/**
 * POJO Actionのラッパーです。
 * 
 * @author higa
 * 
 */
public class ActionWrapper extends Action {

    /**
     * POJO Actionです。
     */
    protected Object action;

    /**
     * インスタンスを構築します。
     * 
     * @param action
     *            POJO Action
     */
    public ActionWrapper(Object action) {
        this.action = action;
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        S2ActionMapping s2mapping = (S2ActionMapping) mapping;
        String[] names = s2mapping.getExecuteMethodNames();
        if (names.length == 1) {
            return execute(s2mapping, names[0]);
        }
        for (String name : names) {
            if (!StringUtil.isEmpty(request.getParameter(name))) {
                return execute(s2mapping, name);
            }
        }
        return null;
    }

    /**
     * Actionを実行します。
     * 
     * @param mapping
     *            アクションマッピング
     * @param methodName
     *            メソッド名
     * @return アクションフォワード
     */
    protected ActionForward execute(S2ActionMapping mapping, String methodName) {
        S2ExecuteConfig executeConfig = mapping.getExecuteConfig(methodName);
        String next = (String) MethodUtil.invoke(executeConfig.getMethod(),
                action, null);
        return mapping.findForward(next);
    }
}