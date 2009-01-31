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

import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * @author higa
 * 
 */
public class ArrayWrapperTest extends TestCase {

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testIterate() throws Exception {
        ArrayWrapper wrapper = new ArrayWrapper(new int[] { 1 });
        Iterator iterator = wrapper.iterator();
        assertEquals(ArrayIteratorWrapper.class, iterator.getClass());
        assertEquals(1, iterator.next());
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testIterator_nest() throws Exception {
        ArrayWrapper wrapper = new ArrayWrapper(new int[][] { new int[] { 1 } });
        Iterator i = wrapper.iterator();
        Collection c = (Collection) i.next();
        assertEquals(ArrayWrapper.class, c.getClass());
        assertEquals(1, c.iterator().next());
    }

    /**
     * @throws Exception
     */
    public void testGet() throws Exception {
        ArrayWrapper wrapper = new ArrayWrapper(new int[] { 1 });
        assertEquals(1, wrapper.get(0));
    }

    /**
     * @throws Exception
     */
    public void testToArray() throws Exception {
        ArrayWrapper wrapper = new ArrayWrapper(new int[] { 1 });
        Object[] array = wrapper.toArray();
        assertEquals(1, array.length);
        assertEquals(1, array[0]);
    }

    /**
     * @throws Exception
     */
    public void testToString() throws Exception {
        ArrayWrapper wrapper = new ArrayWrapper(new int[] { 1, 2 });
        assertEquals("[1, 2]", wrapper.toString());
    }

    /**
     * @throws Exception
     */
    public void testToStringForEmpty() throws Exception {
        ArrayWrapper wrapper = new ArrayWrapper(new int[] {});
        assertEquals("[]", wrapper.toString());
    }

    /**
     * @throws Exception
     */
    public void testContains() throws Exception {
        ArrayWrapper wrapper = new ArrayWrapper(new int[] { 1 });
        assertTrue(wrapper.contains(1));
        assertFalse(wrapper.contains(0));
    }

    /**
     * @throws Exception
     */
    public void testContains_null() throws Exception {
        ArrayWrapper wrapper = new ArrayWrapper(new Integer[] { null });
        assertTrue(wrapper.contains(null));
        assertFalse(wrapper.contains(0));
    }

    /**
     * @throws Exception
     */
    public void testIndexOf() throws Exception {
        ArrayWrapper wrapper = new ArrayWrapper(new int[] { 1 });
        assertEquals(0, wrapper.indexOf(1));
        assertEquals(-1, wrapper.indexOf(0));
    }

    /**
     * @throws Exception
     */
    public void testIndexOf_null() throws Exception {
        ArrayWrapper wrapper = new ArrayWrapper(new Integer[] { null });
        assertEquals(0, wrapper.indexOf(null));
        assertEquals(-1, wrapper.indexOf(0));
    }
}