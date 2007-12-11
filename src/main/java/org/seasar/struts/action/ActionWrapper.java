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

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.util.MethodUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.enums.SaveType;

/**
 * POJO Actionのラッパーです。
 * 
 * @author higa
 * 
 */
public class ActionWrapper extends Action {

    /**
     * アクションマッピングです。
     */
    protected S2ActionMapping actionMapping;

    /**
     * アクションです。
     */
    protected Object action;

    /**
     * アクションフォームです。
     */
    protected Object actionForm;

    /**
     * インスタンスを構築します。
     * 
     * @param actionMapping
     *            アクションマッピング
     */
    public ActionWrapper(S2ActionMapping actionMapping) {
        this.actionMapping = actionMapping;
        action = actionMapping.getAction();
        actionForm = actionMapping.getActionForm();
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String[] names = actionMapping.getExecuteMethodNames();
        if (names.length == 1) {
            return execute(names[0], request);
        }
        for (String name : names) {
            if (!StringUtil.isEmpty(request.getParameter(name))) {
                return execute(name, request);
            }
        }
        return null;
    }

    /**
     * Actionを実行します。
     * 
     * @param methodName
     *            メソッド名
     * @param request
     *            リクエスト
     * @return アクションフォワード
     */
    protected ActionForward execute(String methodName,
            HttpServletRequest request) {
        S2ExecuteConfig executeConfig = actionMapping
                .getExecuteConfig(methodName);
        Method validateMethod = executeConfig.getValidateMethod();
        if (validateMethod != null) {
            ActionMessages errors = (ActionMessages) MethodUtil.invoke(
                    validateMethod, action, null);
            if (errors != null && !errors.isEmpty()) {
                if (executeConfig.getSaveErrors() == SaveType.REQUEST) {
                    request.setAttribute(Globals.ERROR_KEY, errors);
                } else {
                    request.getSession()
                            .setAttribute(Globals.ERROR_KEY, errors);
                }
                return actionMapping.findForward(actionMapping.getInput());
            }
        }
        String next = (String) MethodUtil.invoke(executeConfig.getMethod(),
                action, null);
        exportPropertiesToRequest(request);
        return actionMapping.findForward(next);
    }

    /**
     * プロパティをリクエストに設定します。 *
     * 
     * @param request
     *            リクエスト
     */
    protected void exportPropertiesToRequest(HttpServletRequest request) {
        BeanDesc beanDesc = actionMapping.getActionFormBeanDesc();
        for (int i = 0; i < beanDesc.getPropertyDescSize(); i++) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            Object value = WrapperUtil.convert(pd.getValue(actionForm));
            request.setAttribute(pd.getPropertyName(), value);
        }

    }
}