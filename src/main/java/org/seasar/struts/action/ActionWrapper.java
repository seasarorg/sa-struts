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
package org.seasar.struts.action;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorException;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.Resources;
import org.seasar.framework.container.deployer.InstanceDefFactory;
import org.seasar.framework.util.MethodUtil;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.config.S2ValidationConfig;
import org.seasar.struts.enums.SaveType;
import org.seasar.struts.util.ActionFormUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.S2ExecuteConfigUtil;
import org.seasar.struts.util.ServletContextUtil;

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

        S2ExecuteConfig executeConfig = S2ExecuteConfigUtil.getExecuteConfig();
        if (executeConfig != null) {
            return execute(request, executeConfig);
        }
        return null;
    }

    /**
     * Actionを実行します。
     * 
     * @param request
     *            リクエスト
     * @param executeConfig
     *            実行設定
     * @return アクションフォワード
     */
    protected ActionForward execute(HttpServletRequest request,
            S2ExecuteConfig executeConfig) {
        ActionMessages errors = new ActionMessages();
        List<S2ValidationConfig> validationConfigs = executeConfig
                .getValidationConfigs();
        if (validationConfigs != null) {
            for (S2ValidationConfig cfg : validationConfigs) {
                if (cfg.isValidator()) {
                    ActionMessages errors2 = validateUsingValidator(request,
                            executeConfig);
                    if (errors2 != null && !errors2.isEmpty()) {
                        errors.add(errors2);
                        if (executeConfig.isStopOnValidationError()) {
                            return processErrors(errors, request, executeConfig);
                        }
                    }
                } else {
                    Object target = actionForm;
                    if (cfg.getValidateMethod().getDeclaringClass()
                            .isAssignableFrom(
                                    actionMapping.getComponentDef()
                                            .getComponentClass())) {
                        target = action;
                    }
                    ActionMessages errors2 = (ActionMessages) MethodUtil
                            .invoke(cfg.getValidateMethod(), target, null);
                    if (errors2 != null && !errors2.isEmpty()) {
                        errors.add(errors2);
                        if (executeConfig.isStopOnValidationError()) {
                            return processErrors(errors, request, executeConfig);
                        }
                    }
                }
            }
        }
        if (!errors.isEmpty()) {
            return processErrors(errors, request, executeConfig);
        }
        String next = (String) MethodUtil.invoke(executeConfig.getMethod(),
                action, null);
        if (executeConfig.isRemoveActionForm()
                && !ActionMessagesUtil.hasErrors(request)) {
            if (actionMapping.getActionFormComponentDef().getInstanceDef()
                    .equals(InstanceDefFactory.SESSION)) {
                RequestUtil.getRequest().getSession().removeAttribute(
                        actionMapping.getActionFormComponentDef()
                                .getComponentName());
            } else {
                RequestUtil.getRequest().removeAttribute(
                        actionMapping.getActionFormComponentDef()
                                .getComponentName());
            }
            RequestUtil.getRequest().removeAttribute(
                    actionMapping.getAttribute());
        }
        boolean redirect = executeConfig.isRedirect();
        if (redirect && ActionMessagesUtil.hasErrors(request)) {
            redirect = false;
        }
        return actionMapping.createForward(next, redirect);
    }

    /**
     * バリデータによる検証を行います。
     * 
     * @param request
     *            リクエスト
     * @param executeConfig
     *            実行設定
     * @return エラーメッセージ
     */
    protected ActionMessages validateUsingValidator(HttpServletRequest request,
            S2ExecuteConfig executeConfig) {
        ServletContext application = ServletContextUtil.getServletContext();
        ActionMessages errors = new ActionMessages();
        String validationKey = actionMapping.getName() + "_"
                + executeConfig.getMethod().getName();
        Validator validator = Resources.initValidator(validationKey,
                ActionFormUtil.getActionForm(request, actionMapping),
                application, request, errors, 0);
        try {
            validator.validate();
        } catch (ValidatorException e) {
            throw new RuntimeException(e);
        }
        return errors;
    }

    /**
     * 検証エラーの処理を行います。
     * 
     * @param errors
     *            エラーメッセージ
     * @param request
     *            リクエスト
     * @param executeConfig
     *            実行設定
     * @return アクションフォワード
     */
    protected ActionForward processErrors(ActionMessages errors,
            HttpServletRequest request, S2ExecuteConfig executeConfig) {
        if (executeConfig.getSaveErrors() == SaveType.REQUEST) {
            request.setAttribute(Globals.ERROR_KEY, errors);
        } else {
            request.getSession().setAttribute(Globals.ERROR_KEY, errors);
        }
        return actionMapping.createForward(executeConfig
                .resolveInput(actionMapping));
    }
}