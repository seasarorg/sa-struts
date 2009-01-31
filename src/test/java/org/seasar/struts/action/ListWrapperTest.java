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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author higa
 * 
 */
public class ListWrapperTest extends TestCase {

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testIterate() throws Exception {
        ListWrapper wrapper = new ListWrapper(Arrays.asList(1));
        Iterator iterator = wrapper.iterator();
        assertEquals(IteratorWrapper.class, iterator.getClass());
        assertEquals(1, iterator.next());
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testIterator_nest() throws Exception {
        ListWrapper wrapper = new ListWrapper(Arrays.asList(Arrays.asList(1)));
        Iterator i = wrapper.iterator();
        List l = (List) i.next();
        assertEquals(ListWrapper.class, l.getClass());
        assertEquals(1, l.iterator().next());
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testListIterate() throws Exception {
        ListWrapper wrapper = new ListWrapper(Arrays.asList(1));
        Iterator iterator = wrapper.listIterator();
        assertEquals(ListIteratorWrapper.class, iterator.getClass());
        assertEquals(1, iterator.next());
    }

    /**
     * @throws Exception
     */
    public void testGet() throws Exception {
        ListWrapper wrapper = new ListWrapper(Arrays.asList(1));
        assertEquals(1, wrapper.get(0));
    }

    /**
     * @throws Exception
     */
    public void testToArray() throws Exception {
        ListWrapper wrapper = new ListWrapper(Arrays.asList(1));
        Object[] array = wrapper.toArray();
        assertEquals(1, array.length);
        assertEquals(1, array[0]);
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testToString() throws Exception {
        ListWrapper wrapper = new ListWrapper(Arrays.asList(Arrays.asList(1)));
        assertEquals("[[1]]", wrapper.toString());
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testToStringForEmpty() throws Exception {
        ListWrapper wrapper = new ListWrapper(new ArrayList());
        assertEquals("[]", wrapper.toString());
    }
}