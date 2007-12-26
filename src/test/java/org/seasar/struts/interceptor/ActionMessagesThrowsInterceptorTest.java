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
package org.seasar.struts.interceptor;

import org.apache.struts.Globals;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.struts.exception.ActionMessagesException;

/**
 * @author higa
 * 
 */
public class ActionMessagesThrowsInterceptorTest extends S2TestCase {

    /**
     * @throws Throwable
     */
    public void testHandleThrowable() throws Throwable {
        ActionMessagesException e = new ActionMessagesException(
                "errors.required", "hoge");
        ActionMessagesThrowsInterceptor interceptor = new ActionMessagesThrowsInterceptor();
        assertEquals("input", interceptor.handleThrowable(e, null));
        assertNotNull(getRequest().getAttribute(Globals.ERROR_KEY));
    }
}