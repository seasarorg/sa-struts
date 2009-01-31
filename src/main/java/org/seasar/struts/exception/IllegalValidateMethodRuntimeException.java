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
package org.seasar.struts.exception;

import org.seasar.framework.exception.SRuntimeException;

/**
 * 
 * 検証メソッドのシグニチャが間違っている場合の例外です。
 * 
 * 
 * @author higa
 * 
 */
public class IllegalValidateMethodRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = 1L;

    private Class<?> actionClass;

    private String validateMethodName;

    /**
     * インスタンスを構築します。
     * 
     * @param actionClass
     *            アクションクラス
     * @param validateMethodName
     *            検証メソッド名
     */
    public IllegalValidateMethodRuntimeException(Class<?> actionClass,
            String validateMethodName) {
        super("ESAS0006", new Object[] { actionClass.getName(),
                validateMethodName });
        this.actionClass = actionClass;
        this.validateMethodName = validateMethodName;
    }

    /**
     * アクションクラスを返します。
     * 
     * @return アクションクラス
     */
    public Class<?> getActionClass() {
        return actionClass;
    }

    /**
     * 検証メソッド名を返します。
     * 
     * @return 検証メソッド名
     */
    public String getValidateMethodName() {
        return validateMethodName;
    }
}