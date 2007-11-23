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
package org.seasar.struts.processor;

import javax.servlet.http.HttpServletRequest;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.mock.servlet.MockHttpServletRequest;

/**
 * @author higa
 * 
 */
public class S2RequestProcessorTest extends S2TestCase {

    protected void setUp() throws Exception {
    }

    /**
     * @throws Exception
     */
    public void testProcessMultipart() throws Exception {
        MockHttpServletRequest request = getRequest();
        request.setMethod("POST");
        request.setContentType("multipart/form-data");
        S2RequestProcessor processor = new S2RequestProcessor();
        HttpServletRequest req = processor.processMultipart(request);
        assertSame(req, getContainer().getExternalContext().getRequest());
    }
}