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
package org.seasar.struts.exception;

import org.seasar.framework.exception.SRuntimeException;

/**
 * インデックス付きプロパティがリストでも配列でもない場合の例外です。
 * 
 * @author higa
 * 
 */
public class IndexedPropertyNotListArrayRuntimeException extends
        SRuntimeException {

    private static final long serialVersionUID = 1L;

    private Class<?> targetClass;

    private String propertyName;

    /**
     * インスタンスを構築します。
     * 
     * @param targetClass
     *            ターゲットクラス
     * @param propertyName
     *            プロパティ名
     * 
     */
    public IndexedPropertyNotListArrayRuntimeException(Class<?> targetClass,
            String propertyName) {
        super("ESAS0002", new Object[] { targetClass.getName(), propertyName });
        this.targetClass = targetClass;
        this.propertyName = propertyName;
    }

    /**
     * ターゲットクラスを返します。
     * 
     * @return ターゲットクラス
     */
    public Class<?> getTargetClass() {
        return targetClass;
    }

    /**
     * プロパティ名を返します。
     * 
     * @return プロパティ名
     */
    public String getPropertyName() {
        return propertyName;
    }
}