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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

/**
 * @author higa
 * 
 */
public class CollectionWrapperTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testHasNext_empty() throws Exception {
        CollectionWrapper wrapper = new CollectionWrapper(
                new ArrayList<Integer>());
        assertFalse(wrapper.hasNext());
    }

    /**
     * @throws Exception
     */
    public void testNext_empty() throws Exception {
        CollectionWrapper wrapper = new CollectionWrapper(
                new ArrayList<Integer>());
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
    public void testHasNext_notEmpty() throws Exception {
        CollectionWrapper wrapper = new CollectionWrapper(Arrays.asList(1));
        assertTrue(wrapper.hasNext());
        assertTrue(wrapper.hasNext());
        wrapper.next();
        assertFalse(wrapper.hasNext());
        assertFalse(wrapper.hasNext());
    }

    /**
     * @throws Exception
     */
    public void testNext_notEmpty() throws Exception {
        CollectionWrapper wrapper = new CollectionWrapper(Arrays.asList(1));
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
    public void testNext_nest() throws Exception {
        CollectionWrapper wrapper = new CollectionWrapper(Arrays.asList(Arrays
                .asList(1)));
        Iterator i = (Iterator) wrapper.next();
        assertEquals(1, i.next());
    }
}