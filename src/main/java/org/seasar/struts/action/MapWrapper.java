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
import java.util.Map;
import java.util.Set;

/**
 * マップのラッパーです。
 * 
 * @author higa
 * 
 */
@SuppressWarnings("unchecked")
public class MapWrapper implements Map {

    /**
     * マップです。
     */
    protected Map map;

    /**
     * インスタンスを構築します。
     * 
     * @param map
     *            マップ
     */
    public MapWrapper(Map map) {
        this.map = map;
    }

    public Object get(Object key) {
        return WrapperUtil.convert(map.get(key));
    }

    public Object put(Object key, Object value) {
        return map.put(key, value);
    }

    public void clear() {
        map.clear();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Set entrySet() {
        return map.entrySet();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Set keySet() {
        return map.keySet();
    }

    public void putAll(Map t) {
        map.putAll(t);
    }

    public Object remove(Object key) {
        return map.remove(key);
    }

    public int size() {
        return map.size();
    }

    public Collection values() {
        return map.values();
    }

    @Override
    public String toString() {
        return map.toString();
    }
}