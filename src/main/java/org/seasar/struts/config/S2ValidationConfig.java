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
package org.seasar.struts.config;

import java.lang.reflect.Method;

/**
 * 検証設定です。
 * 
 * @author higa
 * 
 */
public class S2ValidationConfig {

    /**
     * 検証メソッドです。
     */
    protected Method validateMethod;

    /**
     * コンストラクタです。
     */
    public S2ValidationConfig() {
    }

    /**
     * コンストラクタです。
     * 
     * @param validateMethod
     *            検証メソッド
     */
    public S2ValidationConfig(Method validateMethod) {
        this.validateMethod = validateMethod;
    }

    /**
     * 検証メソッドを返します。
     * 
     * @return 検証メソッド
     */
    public Method getValidateMethod() {
        return validateMethod;
    }

    /**
     * バリデータを実行するかどうかを返します。
     * 
     * @return バリデータを実行するかどうか
     */
    public boolean isValidator() {
        return validateMethod == null;
    }
}
