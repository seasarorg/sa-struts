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

import org.apache.struts.action.ActionForward;
import org.seasar.struts.enums.SaveType;

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
     * バリデータを呼び出すかどうかです。
     */
    protected boolean validator = true;

    /**
     * 検証メソッドです。
     */
    protected Method validateMethod;

    /**
     * 検証エラー時の遷移先です。
     */
    protected ActionForward inputForward;

    /**
     * エラーメッセージの保存場所です。
     */
    protected SaveType saveErrors;

    /**
     * インスタンスを構築します。
     * 
     * @param method
     *            メソッド
     * @param validator
     *            バリデータを呼び出すかどうか
     * @param validateMethod
     *            検証メソッド
     * @param saveErrors
     *            エラーメッセージの保存場所
     * @param inputForward
     *            検証エラー時の遷移先です。
     */
    public S2ExecuteConfig(Method method, boolean validator,
            Method validateMethod, SaveType saveErrors,
            ActionForward inputForward) {
        this.method = method;
        this.validator = validator;
        this.validateMethod = validateMethod;
        this.saveErrors = saveErrors;
        this.inputForward = inputForward;
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
     * バリデータを呼び出すかどうかを返します。
     * 
     * @return バリデータを呼び出すかどうか
     */
    public boolean isValidator() {
        return validator;
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
     * エラーメッセージの保存場所を返します。
     * 
     * @return エラーメッセージの保存場所
     */
    public SaveType getSaveErrors() {
        return saveErrors;
    }

    /**
     * 検証エラー時の遷移先を返します。
     * 
     * @return 検証エラー時の遷移先
     */
    public ActionForward getInputForward() {
        return inputForward;
    }
}