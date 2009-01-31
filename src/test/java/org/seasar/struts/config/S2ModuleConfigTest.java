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

import org.apache.struts.Globals;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.validator.ValidatorPlugIn;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.container.ComponentCreator;
import org.seasar.framework.container.creator.ActionCreator;
import org.seasar.framework.container.hotdeploy.HotdeployBehavior;
import org.seasar.framework.container.impl.S2ContainerBehavior;
import org.seasar.framework.convention.impl.NamingConventionImpl;
import org.seasar.framework.util.ClassUtil;
import org.seasar.struts.customizer.ActionCustomizer;
import org.seasar.struts.validator.S2ValidatorResources;

/**
 * @author higa
 * 
 */
public class S2ModuleConfigTest extends S2TestCase {

    /**
     * 
     */
    protected ClassLoader originalLoader;

    /**
     * 
     */
    protected String rootPackageName;

    /**
     * 
     */
    protected HotdeployBehavior ondemand;

    /**
     * 
     */
    protected ActionCreator creator;

    /**
     * 
     */
    protected S2ModuleConfig moduleConfig = new S2ModuleConfig("");

    /**
     * 
     */
    protected S2ValidatorResources validatorResources = new S2ValidatorResources();

    @Override
    public void setUp() {
        getServletContext().setAttribute(Globals.MODULE_KEY, moduleConfig);
        getServletContext().setAttribute(ValidatorPlugIn.VALIDATOR_KEY,
                validatorResources);
        originalLoader = Thread.currentThread().getContextClassLoader();
        NamingConventionImpl convention = new NamingConventionImpl();
        rootPackageName = ClassUtil.getPackageName(getClass());
        convention.addRootPackageName(rootPackageName);
        ondemand = new HotdeployBehavior();
        ondemand.setNamingConvention(convention);
        creator = new ActionCreator(convention);
        creator.setActionCustomizer(new ActionCustomizer());
        ondemand.setCreators(new ComponentCreator[] { creator });
        S2ContainerBehavior.setProvider(ondemand);
        ondemand.start();
    }

    @Override
    public void tearDown() {
        ondemand.stop();
        S2ContainerBehavior
                .setProvider(new S2ContainerBehavior.DefaultProvider());
        Thread.currentThread().setContextClassLoader(originalLoader);
    }

    /**
     * @throws Exception
     */
    public void testFindActionConfig() throws Exception {
        ActionConfig ac = moduleConfig.findActionConfig("/hello");
        assertNotNull(ac);
    }
}