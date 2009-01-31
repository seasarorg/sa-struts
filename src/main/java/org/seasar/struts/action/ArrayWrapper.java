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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 配列をリストとして扱うクラスです。
 * 
 * @author higa
 * 
 */
@SuppressWarnings("unchecked")
public class ArrayWrapper implements List {

    /**
     * 配列の長さです。
     */
    public int length;

    /**
     * オリジナルの配列です。
     */
    protected Object array;

    /**
     * インスタンスを構築します。
     * 
     * @param array
     *            オリジナルの配列
     */
    public ArrayWrapper(Object array) {
        this.array = array;
        length = Array.getLength(array);
    }

    public boolean add(Object o) {
        throw new UnsupportedOperationException("add");
    }

    public void add(int index, Object element) {
        throw new UnsupportedOperationException("add");
    }

    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException("addAll");
    }

    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException("addAll");
    }

    public void clear() {
        throw new UnsupportedOperationException("clear");
    }

    public boolean contains(Object o) {
        for (int i = 0; i < length; i++) {
            Object o2 = get(i);
            if (o != null && o.equals(o2) || o == null && o2 == null) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(Collection c) {
        throw new UnsupportedOperationException("containsAll");
    }

    public Object get(int index) {
        return WrapperUtil.convert(Array.get(array, index));
    }

    public int indexOf(Object o) {
        for (int i = 0; i < length; i++) {
            Object o2 = get(i);
            if (o != null && o.equals(o2) || o == null && o2 == null) {
                return i;
            }
        }
        return -1;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Iterator iterator() {
        return new ArrayIteratorWrapper(array);
    }

    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("lastIndexOf");
    }

    public ListIterator listIterator() {
        throw new UnsupportedOperationException("listIterator");
    }

    public ListIterator listIterator(int index) {
        throw new UnsupportedOperationException("listIterator");
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException("remove");
    }

    public Object remove(int index) {
        throw new UnsupportedOperationException("remove");
    }

    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException("removeAll");
    }

    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException("retainAll");
    }

    public Object set(int index, Object element) {
        throw new UnsupportedOperationException("set");
    }

    public int size() {
        return length;
    }

    public List subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("subList");
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(30);
        sb.append('[');
        for (int i = 0; i < length; i++) {
            sb.append(Array.get(array, i));
            sb.append(", ");
        }
        if (length > 0) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(']');
        return sb.toString();
    }
}