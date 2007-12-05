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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

/**
 * 配列を集合として扱うクラスです。
 * 
 * @author higa
 * 
 */
@SuppressWarnings("unchecked")
public class ArrayWrapper implements Collection {

    /**
     * 配列です。
     */
    protected Object array;

    /**
     * 配列のサイズです。
     */
    protected int size;

    /**
     * インスタンスを構築します。
     * 
     * @param array
     *            配列
     */
    public ArrayWrapper(Object array) {
        this.array = array;
        size = Array.getLength(array);
    }

    public boolean add(Object o) {
        throw new UnsupportedOperationException("add");
    }

    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException("addAll");
    }

    public void clear() {
        throw new UnsupportedOperationException("clear");
    }

    public boolean contains(Object o) {
        throw new UnsupportedOperationException("contains");
    }

    public boolean containsAll(Collection c) {
        throw new UnsupportedOperationException("containsAll");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("isEmpty");
    }

    public Iterator iterator() {
        return new ArrayIteratorWrapper(array);
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException("remove");
    }

    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException("removeAll");
    }

    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException("retainAll");
    }

    public int size() {
        return size;
    }

    public Object[] toArray() {
        Object[] arr = new Object[size()];
        int i = 0;
        for (Iterator ite = iterator(); ite.hasNext();) {
            arr[i++] = ite.next();
        }
        return arr;
    }

    public Object[] toArray(Object[] a) {
        throw new UnsupportedOperationException("toArray");
    }
}