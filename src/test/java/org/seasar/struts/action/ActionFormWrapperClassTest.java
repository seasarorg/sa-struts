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

import org.apache.commons.beanutils.DynaProperty;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.struts.config.S2ActionMapping;

/**
 * @author higa
 * 
 */
public class ActionFormWrapperClassTest extends S2TestCase {

    /**
     * @throws Exception
     */
    public void testGetName() throws Exception {
        S2ActionMapping mapping = new S2ActionMapping();
        mapping.setName("hoge");
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                mapping);
        assertEquals("hoge", wrapperClass.getName());
    }

    /**
     * @throws Exception
     */
    public void testGetDynaProperty() throws Exception {
        S2ActionMapping mapping = new S2ActionMapping();
        mapping.setName("hoge");
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                mapping);
        DynaProperty property = new DynaProperty("aaa");
        wrapperClass.addDynaProperty(property);
        assertSame(property, wrapperClass.getDynaProperty("aaa"));
    }

    /**
     * @throws Exception
     */
    public void testGetDynaProperties() throws Exception {
        S2ActionMapping mapping = new S2ActionMapping();
        mapping.setName("hoge");
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                mapping);
        DynaProperty property = new DynaProperty("aaa");
        wrapperClass.addDynaProperty(property);
        DynaProperty[] properties = wrapperClass.getDynaProperties();
        assertEquals(1, properties.length);
        assertSame(property, properties[0]);
    }

    /**
     * @throws Exception
     */
    public void testNewInstance() throws Exception {
        register(String.class, "myAction");
        S2ActionMapping mapping = new S2ActionMapping();
        mapping.setComponentDef(getComponentDef("myAction"));
        ActionFormWrapperClass wrapperClass = new ActionFormWrapperClass(
                mapping);
        assertNotNull(wrapperClass.newInstance());
    }
}