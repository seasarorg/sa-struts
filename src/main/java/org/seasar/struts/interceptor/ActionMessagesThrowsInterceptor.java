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
package org.seasar.struts.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.struts.Globals;
import org.seasar.framework.aop.interceptors.ThrowsInterceptor;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.enums.SaveType;
import org.seasar.struts.exception.ActionMessagesException;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.S2ActionMappingUtil;
import org.seasar.struts.util.S2ExecuteConfigUtil;

/**
 * {@link ActionMessagesException}を処理するThrowsInterceptorです。
 * 
 * @author higa
 * 
 */
public class ActionMessagesThrowsInterceptor extends ThrowsInterceptor {

    private static final long serialVersionUID = 1L;

    /**
     * 例外を処理します。
     * 
     * @param e
     *            例外
     * @param invocation
     *            メソッド呼び出し
     * @return 戻り先の名前
     * @throws Throwable
     *             例外が発生した場合
     */
    public String handleThrowable(ActionMessagesException e,
            MethodInvocation invocation) throws Throwable {
        HttpServletRequest request = RequestUtil.getRequest();
        if (e.getSaveErrors() == SaveType.REQUEST) {
            request.setAttribute(Globals.ERROR_KEY, e.getMessages());
        } else {
            HttpSession session = request.getSession();
            session.setAttribute(Globals.ERROR_KEY, e.getMessages());
        }
        S2ExecuteConfig executeConfig = S2ExecuteConfigUtil.getExecuteConfig();
        if (executeConfig.getInput() != null) {
            return executeConfig.resolveInput(S2ActionMappingUtil
                    .getActionMapping());
        }
        return S2ActionMappingUtil.getActionMapping().getInput();
    }
}