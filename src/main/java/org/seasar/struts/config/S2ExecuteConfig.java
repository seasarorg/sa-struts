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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.seasar.framework.util.StringUtil;
import org.seasar.struts.enums.SaveType;
import org.seasar.struts.exception.IllegalUrlPatternRuntimeException;

/**
 * Actionの実行メソッド用の設定です。
 * 
 * @author higa
 * 
 */
public class S2ExecuteConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String METHOD_NAME = "SAStruts.method";

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
     * エラーメッセージの保存場所です。
     */
    protected SaveType saveErrors;

    /**
     * 検証エラー時の遷移先です。
     */
    protected String input;

    /**
     * URLのパターンです。
     */
    protected String urlPattern;

    /**
     * URLのパターンのコンパイル結果です。
     */
    protected Pattern urlPatternRegexp;

    /**
     * URLのパラメータ名のリストです。
     */
    protected List<String> urlParamNames = new ArrayList<String>();

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
     * @param input
     *            検証エラー時の遷移先
     * @param urlPattern
     *            URLのパターン
     */
    public S2ExecuteConfig(Method method, boolean validator,
            Method validateMethod, SaveType saveErrors, String input,
            String urlPattern) {
        this.method = method;
        this.validator = validator;
        this.validateMethod = validateMethod;
        this.saveErrors = saveErrors;
        this.input = input;
        setUrlPattern(urlPattern);
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
    public String getInput() {
        return input;
    }

    /**
     * URLのパターンを返します。
     * 
     * @return URLのパターン
     */
    public String getUrlPattern() {
        return urlPattern;
    }

    /**
     * URLのパターンを設定します。
     * 
     * @param urlPattern
     *            URLのパターン
     */
    protected void setUrlPattern(String urlPattern) {
        if (StringUtil.isEmpty(urlPattern)) {
            urlPattern = method.getName();
        }
        this.urlPattern = urlPattern;
        StringBuilder sb = new StringBuilder(50);
        char[] chars = urlPattern.toCharArray();
        int length = chars.length;
        int index = -1;
        for (int i = 0; i < length; i++) {
            if (chars[i] == '{') {
                index = i;
            } else if (chars[i] == '}') {
                if (index >= 0) {
                    sb.append("([a-zA-Z0-9]+)");
                    urlParamNames.add(urlPattern.substring(index + 1, i));
                    index = -1;
                } else {
                    throw new IllegalUrlPatternRuntimeException(urlPattern);
                }
            } else if (index < 0) {
                sb.append(chars[i]);
            }
        }
        if (index >= 0) {
            throw new IllegalUrlPatternRuntimeException(urlPattern);
        }
        urlPatternRegexp = Pattern.compile("^" + sb.toString() + "$");
    }

    /**
     * 実行メソッドの対象かどうかを返します。
     * 
     * @param paramPath
     *            パラメータ用のパス
     * @return 実行メソッドの対象かどうか
     */
    public boolean isTarget(String paramPath) {
        if (!StringUtil.isEmpty(paramPath)) {
            return urlPatternRegexp.matcher(paramPath).find();
        }
        return false;
    }

    /**
     * 実行メソッドの対象かどうかを返します。
     * 
     * @param request
     *            リクエスト
     * @return リクエストが実行メソッドの対象かどうか
     */
    public boolean isTarget(HttpServletRequest request) {
        String methodName = request.getParameter(METHOD_NAME);
        if (!StringUtil.isEmpty(methodName)) {
            return methodName.equals(method.getName());
        }
        return !StringUtil.isEmpty(request.getParameter(method.getName()));
    }

    /**
     * クエリストリングを返します。
     * 
     * @param paramPath
     *            パラメータ用のパス
     * @return クエリストリング
     */
    public String getQueryString(String paramPath) {
        if (StringUtil.isEmpty(paramPath)) {
            return "?" + METHOD_NAME + "=" + method.getName();
        }
        Matcher matcher = urlPatternRegexp.matcher(paramPath);
        if (!matcher.find()) {
            return "?" + METHOD_NAME + "=" + method.getName();
        }
        if (urlParamNames.size() == 0) {
            return "?" + METHOD_NAME + "=" + method.getName();
        }
        StringBuilder sb = new StringBuilder(50);
        sb.append("?");
        int index = 1;
        for (String name : urlParamNames) {
            if (index != 1) {
                sb.append("&");
            }
            sb.append(name).append("=").append(matcher.group(index++));
        }
        sb.append("&").append(METHOD_NAME).append("=").append(method.getName());
        return sb.toString();
    }
}