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
package org.seasar.struts.config;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Actionの実行メソッド用の設定です。
 * 
 * @author higa
 * 
 */
public class S2ExecuteConfig implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * メソッドです。
     */
    protected Method method;

    /**
     * バリデーションをするかどうかです。
     */
    protected boolean validate = true;

    /**
     * インスタンスを構築します。
     * 
     * @param method
     *            メソッド
     * @param validate
     *            バリデーションをするかどうか
     */
    public S2ExecuteConfig(Method method, boolean validate) {
        this.method = method;
        this.validate = validate;
    }

    /**
     * メソッドを返します。
     * 
     * @return メソッド
     */
    public Method getMethod() {
        return method;
    }

    /**
     * バリデーションをするかどうかを返します。
     * 
     * @return バリデーションをするかどうか
     */
    public boolean isValidate() {
        return validate;
    }
}