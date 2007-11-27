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

import java.lang.reflect.Method;

import org.seasar.framework.exception.SRuntimeException;

/**
 * <p>
 * 実行メソッドのシグニチャが間違っている場合の例外です。
 * </p>
 * 
 * @author higa
 * 
 */
public class IllegalExecuteMethodRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = 1L;

    private Class<?> actionClass;

    private Method executeMethod;

    /**
     * インスタンスを構築します。
     * 
     * @param actionClass
     *            アクションクラス
     * @param executeMethod
     *            実行メソッド
     */
    public IllegalExecuteMethodRuntimeException(Class<?> actionClass,
            Method executeMethod) {
        super("ESAS0001", new Object[] { actionClass.getName(),
                executeMethod.getName() });
        this.actionClass = actionClass;
        this.executeMethod = executeMethod;
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
     * 実行メソッドを返します。
     * 
     * @return 実行メソッド
     */
    public Method getExecuteMethod() {
        return executeMethod;
    }
}