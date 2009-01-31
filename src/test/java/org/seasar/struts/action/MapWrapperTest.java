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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.seasar.framework.util.tiger.Maps;

/**
 * @author higa
 * 
 */
public class MapWrapperTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testGet() throws Exception {
        MapWrapper wrapper = new MapWrapper(Maps.map("aaa", "111").$());
        assertEquals("111", wrapper.get("aaa"));
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testGet_nest() throws Exception {
        MapWrapper wrapper = new MapWrapper(Maps.map("aaa",
                Maps.map("bbb", "111").$()).$());
        Map map = (Map) wrapper.get("aaa");
        assertEquals("111", map.get("bbb"));
    }

    /**
     * @throws Exception
     */
    public void testToString() throws Exception {
        MapWrapper wrapper = new MapWrapper(Maps.map("aaa",
                Maps.map("bbb", "111").$()).$());
        assertEquals("{aaa={bbb=111}}", wrapper.toString());
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testToStringForEmpty() throws Exception {
        MapWrapper wrapper = new MapWrapper(new HashMap());
        assertEquals("{}", wrapper.toString());
    }
}