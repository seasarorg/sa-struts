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

import org.apache.struts.Globals;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.struts.config.S2ActionMapping;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.config.S2ModuleConfig;

/**
 * @author higa
 * 
 */
public class S2ExecuteConfigUtilTest extends S2TestCase {

    /**
     * @return
     */
    public String index() {
        return null;
    }

    /**
     * @throws Exception
     */
    public void testExecuteConfig() throws Exception {
        S2ExecuteConfigUtil.setExecuteConfig(new S2ExecuteConfig());
        assertNotNull(S2ExecuteConfigUtil.getExecuteConfig());
    }

    /**
     * @throws Exception
     */
    public void testFindExecuteConfig() throws Exception {
        S2ModuleConfig moduleConfig = new S2ModuleConfig("");
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setPath("/aaa");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(getClass().getMethod("getClass"));
        actionMapping.addExecuteConfig(executeConfig);
        moduleConfig.addActionConfig(actionMapping);
        getServletContext().setAttribute(Globals.MODULE_KEY, moduleConfig);
        assertNotNull(S2ExecuteConfigUtil.findExecuteConfig("/aaa", "getClass"));
    }

    /**
     * @throws Exception
     */
    public void testFindExecuteConfig_request() throws Exception {
        S2ModuleConfig moduleConfig = new S2ModuleConfig("");
        S2ActionMapping actionMapping = new S2ActionMapping();
        actionMapping.setPath("/aaa");
        S2ExecuteConfig executeConfig = new S2ExecuteConfig();
        executeConfig.setMethod(getClass().getMethod("index"));
        actionMapping.addExecuteConfig(executeConfig);
        moduleConfig.addActionConfig(actionMapping);
        getServletContext().setAttribute(Globals.MODULE_KEY, moduleConfig);
        assertNotNull(S2ExecuteConfigUtil.findExecuteConfig("/aaa",
                getRequest()));
    }
}