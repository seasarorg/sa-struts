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

/**
 * 集合のラッパークラスです。
 * 
 * @author higa
 * 
 */
@SuppressWarnings("unchecked")
public class CollectionWrapper implements Collection {

    /**
     * 集合です。
     */
    protected Collection collection;

    /**
     * インスタンスを構築します。
     * 
     * @param collection
     *            集合
     */
    public CollectionWrapper(Collection collection) {
        this.collection = collection;
    }

    public boolean add(Object o) {
        return collection.add(o);
    }

    public boolean addAll(Collection c) {
        return collection.addAll(c);
    }

    public void clear() {
        collection.clear();
    }

    public boolean contains(Object o) {
        return collection.contains(o);
    }

    public boolean containsAll(Collection c) {
        return collection.containsAll(c);
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }

    public Iterator iterator() {
        return new IteratorWrapper(collection.iterator());
    }

    public boolean remove(Object o) {
        return collection.remove(o);
    }

    public boolean removeAll(Collection c) {
        return collection.removeAll(c);
    }

    public boolean retainAll(Collection c) {
        return collection.retainAll(c);
    }

    public int size() {
        return collection.size();
    }

    public Object[] toArray() {
        Object[] array = new Object[size()];
        int i = 0;
        for (Iterator ite = iterator(); ite.hasNext();) {
            array[i++] = ite.next();
        }
        return array;
    }

    public Object[] toArray(Object[] a) {
        throw new UnsupportedOperationException("toArray");
    }
}