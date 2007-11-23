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
package org.seasar.struts.servlet;

import org.apache.struts.Globals;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.config.impl.ModuleConfigImpl;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 * 
 */
public class S2ActionServletTest extends S2TestCase {

    private S2ActionServlet actionServlet;

    protected void setUp() throws Exception {
        actionServlet = new S2ActionServlet();
        actionServlet.servletContext = getServletContext();
        register(RequestProcessor.class);
    }

    /**
     * @throws Exception
     */
    public void testGetProcessorForModule() throws Exception {
        ModuleConfigImpl config = new ModuleConfigImpl();
        RequestProcessor processor = new RequestProcessor();
        getServletContext().setAttribute(
                Globals.REQUEST_PROCESSOR_KEY + config.getPrefix(), processor);
        assertSame(processor, actionServlet.getProcessorForModule(config));
    }

    /**
     * @throws Exception
     */
    public void testGetRequestProcessor() throws Exception {
        ModuleConfigImpl config = new ModuleConfigImpl();
        RequestProcessor processor = actionServlet.getRequestProcessor(config);
        assertNotNull(processor);
        assertSame(processor, actionServlet.getRequestProcessor(config));
    }
}
