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

import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 * 
 */
public class ActionFormUtilTest extends S2TestCase {

    /**
     * @throws Exception
     */
    public void testGetActionForm_request() throws Exception {
        ActionMapping mapping = new ActionMapping();
        mapping.setAttribute("hoge");
        mapping.setScope("request");
        getRequest().setAttribute("hoge", new ValidatorForm());
        assertNotNull(ActionFormUtil.getActionForm(getRequest(), mapping));
    }

    /**
     * @throws Exception
     */
    public void testGetActionForm_session() throws Exception {
        ActionMapping mapping = new ActionMapping();
        mapping.setAttribute("hoge");
        getRequest().getSession().setAttribute("hoge", new ValidatorForm());
        assertNotNull(ActionFormUtil.getActionForm(getRequest(), mapping));
    }
}