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

import java.util.Iterator;

/**
 * イテレータのラッパクラスです。
 * 
 * @author higa
 * 
 */
@SuppressWarnings("unchecked")
public class IteratorWrapper implements Iterator {

    /**
     * イテレータです。
     */
    protected Iterator iterator;

    /**
     * インスタンスを構築します。
     * 
     * @param iterator
     *            イテレータ
     */
    public IteratorWrapper(Iterator iterator) {
        this.iterator = iterator;
    }

    public Object next() {
        return WrapperUtil.convert(iterator.next());
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public void remove() {
        iterator.remove();
    }
}