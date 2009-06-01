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

import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;
import org.seasar.extension.jdbc.types.ValueTypes;

/**
 * ラッパー用のユーティリティです。
 * 
 * @author higa
 * 
 */
public final class WrapperUtil {

    private WrapperUtil() {
    }

    /**
     * 値を必要なら適当なラッパーに変換します。
     * 
     * @param <T>
     *            戻り値の型
     * @param value
     *            値
     * @return 変換後の値
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (ValueTypes.isSimpleType(clazz)) {
            return (T) value;
        }
        if (clazz.isArray()) {
            return (T) new ArrayWrapper(value);
        }
        if (DynaBean.class.isAssignableFrom(clazz)) {
            return (T) value;
        }
        if (List.class.isAssignableFrom(clazz)) {
            return (T) new ListWrapper((List) value);
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return (T) new MapWrapper((Map) value);
        }
        if (clazz.getSuperclass().isEnum()) {
            return (T) value;
        }

        return (T) new BeanWrapper(value);
    }
}
