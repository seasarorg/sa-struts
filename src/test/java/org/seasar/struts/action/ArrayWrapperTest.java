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
    public void testToArray() throws Exception {
        ArrayWrapper wrapper = new ArrayWrapper(new int[] { 1 });
        Object[] array = wrapper.toArray();
        assertEquals(1, array.length);
        assertEquals(1, array[0]);
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
}