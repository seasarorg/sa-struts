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
 * 検証メソッドに対応する実行メソッドが見つからない場合の例外です。
 * 
 * @author higa
 * 
 */
public class ExecuteMethodForValidateNotFoundRuntimeException extends
        SRuntimeException {

    private static final long serialVersionUID = 1L;

    private Class<?> actionClass;

    private String validateMethodName;

    private String executeMethodName;

    /**
     * インスタンスを構築します。
     * 
     * @param actionClass
     *            アクションクラス
     * @param validateMethodName
     *            検証メソッド名
     * @param executeMethodName
     *            実行メソッド名
     */
    public ExecuteMethodForValidateNotFoundRuntimeException(
            Class<?> actionClass, String validateMethodName,
            String executeMethodName) {
        super("ESAS0007", new Object[] { actionClass.getName(),
                validateMethodName, executeMethodName });
        this.actionClass = actionClass;
        this.validateMethodName = validateMethodName;
        this.executeMethodName = executeMethodName;
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

    /**
     * 実行メソッド名を返します。
     * 
     * @return 実行メソッド名
     */
    public String getExecuteMethodName() {
        return executeMethodName;
    }
}