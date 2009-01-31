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
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

/**
 * @author higa
 * 
 */
public class ListIteratorWrapperTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testNext_empty() throws Exception {
        ListIteratorWrapper wrapper = new ListIteratorWrapper(
                new ArrayList<Integer>().listIterator());
        try {
            wrapper.next();
            fail();
        } catch (NoSuchElementException e) {
            System.out.println(e);
        }
    }

    /**
     * @throws Exception
     */
    public void testNext_notEmpty() throws Exception {
        ListIteratorWrapper wrapper = new ListIteratorWrapper(Arrays.asList(1)
                .listIterator());
        assertEquals(1, wrapper.next());
        assertFalse(wrapper.hasNext());
        try {
            wrapper.next();
            fail();
        } catch (NoSuchElementException e) {
            System.out.println(e);
        }
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testNext_wrap() throws Exception {
        ListIteratorWrapper wrapper = new ListIteratorWrapper(Arrays.asList(
                Arrays.asList(1)).listIterator());
        List l = (List) wrapper.next();
        assertEquals(ListWrapper.class, l.getClass());
    }

    /**
     * @throws Exception
     */
    public void testPrevious_empty() throws Exception {
        ListIteratorWrapper wrapper = new ListIteratorWrapper(
                new ArrayList<Integer>().listIterator());
        try {
            wrapper.previous();
            fail();
        } catch (NoSuchElementException e) {
            System.out.println(e);
        }
    }

    /**
     * @throws Exception
     */
    public void testPrevious_notEmpty() throws Exception {
        ListIteratorWrapper wrapper = new ListIteratorWrapper(Arrays.asList(1)
                .listIterator());
        assertEquals(1, wrapper.next());
        assertEquals(1, wrapper.previous());
        assertFalse(wrapper.hasPrevious());
        try {
            wrapper.previous();
            fail();
        } catch (NoSuchElementException e) {
            System.out.println(e);
        }
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testPrevious_wrap() throws Exception {
        ListIteratorWrapper wrapper = new ListIteratorWrapper(Arrays.asList(
                Arrays.asList(1)).listIterator());
        wrapper.next();
        List l = (List) wrapper.previous();
        assertEquals(ListWrapper.class, l.getClass());
    }
}