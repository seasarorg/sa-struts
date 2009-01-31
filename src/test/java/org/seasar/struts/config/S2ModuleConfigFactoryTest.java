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

import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.ModuleConfigFactory;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 * 
 */
public class S2ModuleConfigFactoryTest extends S2TestCase {

    private String factoryClass;

    @Override
    public void setUp() {
        factoryClass = ModuleConfigFactory.getFactoryClass();
        ModuleConfigFactory.setFactoryClass(S2ModuleConfigFactory.class
                .getName());
    }

    @Override
    public void tearDown() {
        ModuleConfigFactory.setFactoryClass(factoryClass);
    }

    /**
     * @throws Exception
     */
    public void testCreateFactory() throws Exception {
        ModuleConfigFactory factory = ModuleConfigFactory.createFactory();
        assertNotNull(factory);
        assertEquals(S2ModuleConfigFactory.class, factory.getClass());
    }

    /**
     * @throws Exception
     */
    public void testCreateModuleConfig() throws Exception {
        ModuleConfigFactory factory = ModuleConfigFactory.createFactory();
        ModuleConfig config = factory.createModuleConfig("");
        assertEquals(S2ModuleConfig.class, config.getClass());
    }
}