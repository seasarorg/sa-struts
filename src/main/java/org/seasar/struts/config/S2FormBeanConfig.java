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
package org.seasar.struts.config;

import org.apache.commons.beanutils.DynaClass;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.FormBeanConfig;

/**
 * Seasar2用のアクションフォーム設定です。
 * 
 * @author higa
 * 
 */
public class S2FormBeanConfig extends FormBeanConfig {

    private static final long serialVersionUID = 1L;

    private static final String TYPE = "org.seasar.struts.action.ActionFormWrapper";

    /**
     * 動的クラスです。
     */
    protected DynaClass dynaClass;

    @Override
    public ActionForm createActionForm(ActionServlet servlet)
            throws IllegalAccessException, InstantiationException {

        ActionForm actionForm = (ActionForm) dynaClass.newInstance();
        actionForm.setServlet(servlet);
        return actionForm;
    }

    /**
     * 動的クラスを返します。
     * 
     * @return 動的クラス
     */
    public DynaClass getDynaClass() {
        return dynaClass;
    }

    /**
     * 動的クラスを設定します。
     * 
     * @param dynaClass
     *            動的クラス
     */
    public void setDynaClass(DynaClass dynaClass) {
        this.dynaClass = dynaClass;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
