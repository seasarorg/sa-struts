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

import org.apache.struts.Globals;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.exception.ActionMessagesException;
import org.seasar.struts.util.S2ExecuteConfigUtil;

/**
 * @author higa
 * 
 */
public class ActionMessagesThrowsInterceptorTest extends S2TestCase {

    /**
     * @throws Throwable
     */
    public void testHandleThrowable() throws Throwable {
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(getClass().getMethod("getClass"));
        executeConfig.setInput("input");
        S2ExecuteConfigUtil.setExecuteConfig(executeConfig);
        ActionMessagesException e = new ActionMessagesException(
                "errors.required", "hoge");
        ActionMessagesThrowsInterceptor interceptor = new ActionMessagesThrowsInterceptor();
        assertEquals("input", interceptor.handleThrowable(e, null));
        assertNotNull(getRequest().getAttribute(Globals.ERROR_KEY));
    }
}
