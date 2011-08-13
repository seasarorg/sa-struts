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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.seasar.framework.util.StringUtil;
import org.seasar.struts.enums.SaveType;
import org.seasar.struts.exception.IllegalInputPatternRuntimeException;
import org.seasar.struts.exception.IllegalUrlPatternRuntimeException;
import org.seasar.struts.util.URLEncoderUtil;

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
     * 検証メソッドです。
     */
    protected List<S2ValidationConfig> validationConfigs;

    /**
     * エラーメッセージの保存場所です。
     */
    protected SaveType saveErrors = SaveType.REQUEST;

    /**
     * 検証エラー時の遷移先です。
     */
    protected String input;

    /**
     * 検証エラー時遷移先のパラメータ名のリストです。
     */
    protected List<String> inputParamNames = new ArrayList<String>();

    /**
     * URLのパターンです。
     */
    protected String urlPattern;

    /**
     * URLのパターンのコンパイル結果です。
     */
    protected Pattern urlPatternRegexp;

    /**
     * URLパターンが{id}のように全選択かどうかです。
     */
    protected boolean urlPatternAllSelected = false;

    /**
     * URLのパラメータ名のリストです。
     */
    protected List<String> urlParamNames = new ArrayList<String>();

    /**
     * ロールの配列です。
     */
    protected String[] roles;

    /**
     * trueの場合、バリデータや検証メソッドで検証エラーがあるとそこで検証がとまります。
     * falseの場合、検証エラーがあっても後続の検証を続行します。 どちらの場合も検証エラーがあると実行メソッドは呼び出されません。
     */
    protected boolean stopOnValidationError = true;

    /**
     * 実行メソッドの正常終了時にセッションからアクションフォームを削除するかどうかです。 trueの場合、アクションフォームを削除します。
     */
    protected boolean removeActionForm = false;

    /**
     * リセットメソッドです。
     */
    protected Method resetMethod;

    /**
     * 正常終了時に遷移先にリダイレクトするかどうかです。
     */
    protected boolean redirect = false;

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
    public S2ExecuteConfig() {
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
     * メソッドを設定します。
     * 
     * @param method
     *            メソッド
     */
    public void setMethod(Method method) {
        this.method = method;
        setUrlPattern(method.getName());
    }

    /**
     * バリデータを呼び出すかどうかを返します。
     * 
     * @return バリデータを呼び出すかどうか
     */
    public boolean isValidator() {
        if (validationConfigs != null) {
            for (S2ValidationConfig cfg : validationConfigs) {
                if (cfg.isValidator()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 検証設定のリストを返します。
     * 
     * @return 検証設定のリスト
     */
    public List<S2ValidationConfig> getValidationConfigs() {
        return validationConfigs;
    }

    /**
     * 検証設定のリストを設定します。
     * 
     * @param validationConfigs
     *            検証設定のリスト
     */
    public void setValidationConfigs(List<S2ValidationConfig> validationConfigs) {
        this.validationConfigs = validationConfigs;
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
     * エラーメッセージの保存場所を設定します。
     * 
     * @param saveErrors
     *            エラーメッセージの保存場所
     */
    public void setSaveErrors(SaveType saveErrors) {
        this.saveErrors = saveErrors;
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
     * 検証エラー時の遷移先を設定します。
     * 
     * @param input
     *            検証エラー時の遷移先
     */
    public void setInput(String input) {
        this.input = input;
        if (StringUtil.isEmpty(input)) {
            return;
        }
        char[] chars = input.toCharArray();
        int length = chars.length;
        int index = -1;
        for (int i = 0; i < length; i++) {
            if (chars[i] == '{') {
                index = i;
            } else if (chars[i] == '}') {
                if (index >= 0) {
                    inputParamNames.add(input.substring(index + 1, i));
                    index = -1;
                } else {
                    throw new IllegalInputPatternRuntimeException(input);
                }
            }
        }
        if (index >= 0) {
            throw new IllegalInputPatternRuntimeException(input);
        }
    }

    /**
     * パラメータを解決した検証エラー時の遷移先を返します。
     * 
     * @param actionMapping
     *            アクションマッピング
     * 
     * @return 検証エラー時の遷移先
     */
    public String resolveInput(S2ActionMapping actionMapping) {
        String s = input;
        for (String name : inputParamNames) {
            s = s.replace("{" + name + "}", actionMapping
                    .getPropertyAsString(name));
        }
        return s;
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
    public void setUrlPattern(String urlPattern) {
        if (StringUtil.isEmpty(urlPattern)) {
            return;
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
                    sb.append("([^/]+)");
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
        String pattern = sb.toString();
        urlPatternAllSelected = pattern.equals("([^/]+)");
        urlPatternRegexp = Pattern.compile("^" + pattern + "$");
    }

    /**
     * URLパターンが{id}のように全選択かどうかを返します。
     * 
     * @return URLパターンが{id}のように全選択かどうか
     */
    public boolean isUrlPatternAllSelected() {
        return urlPatternAllSelected;
    }

    /**
     * ロールの配列を返します。
     * 
     * @return ロールの配列
     */
    public String[] getRoles() {
        return roles;
    }

    /**
     * ロールの配列を設定します。
     * 
     * @param roles
     *            ロールの配列
     */
    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    /**
     * 検証エラーがあった場合にそこで検証をやめるかどうかを返します。
     * 
     * @return 検証エラーがあった場合にそこで検証をやめるかどうか
     */
    public boolean isStopOnValidationError() {
        return stopOnValidationError;
    }

    /**
     * 検証エラーがあった場合にそこで検証をやめるかどうかを設定します。
     * 
     * @param stopOnValidationError
     *            検証エラーがあった場合にそこで検証をやめるかどうか
     */
    public void setStopOnValidationError(boolean stopOnValidationError) {
        this.stopOnValidationError = stopOnValidationError;
    }

    /**
     * 実行メソッドの正常終了時にセッションからアクションフォームを削除するかどうか返します。
     * 
     * @return 実行メソッドの正常終了時にセッションからアクションフォームを削除するかどうか
     */
    public boolean isRemoveActionForm() {
        return removeActionForm;
    }

    /**
     * 実行メソッドの正常終了時にセッションからアクションフォームを削除するかどうかを設定します。
     * 
     * @param removeActionForm
     *            実行メソッドの正常終了時にセッションからアクションフォームを削除するかどうか
     */
    public void setRemoveActionForm(boolean removeActionForm) {
        this.removeActionForm = removeActionForm;
    }

    /**
     * リセットメソッドを返します。
     * 
     * @return リセットメソッド
     */
    public Method getResetMethod() {
        return resetMethod;
    }

    /**
     * リセットメソッドを設定します。
     * 
     * @param resetMethod
     *            リセットメソッド
     */
    public void setResetMethod(Method resetMethod) {
        this.resetMethod = resetMethod;
    }

    /**
     * 正常終了時に遷移先にリダイレクトするかどうかを返します。
     * 
     * @return 正常終了時に遷移先にリダイレクトするかどうか
     */
    public boolean isRedirect() {
        return redirect;
    }

    /**
     * 正常終了時に遷移先にリダイレクトするかどうかを設定します。
     * 
     * @param redirect
     *            正常終了時に遷移先にリダイレクトするかどうか
     */
    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
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
        return "index".equals(urlPattern);
    }

    /**
     * 実行メソッドの対象かどうかを返します。
     * 
     * @param request
     *            リクエスト
     * @return リクエストが実行メソッドの対象かどうか
     */
    public boolean isTarget(HttpServletRequest request) {
        String[] methodNames = request.getParameterValues(METHOD_NAME);
        if (methodNames != null && methodNames.length > 0) {
            return methodNames[0].equals(method.getName());
        }
        return !StringUtil.isEmpty(request.getParameter(method.getName()))
                || !StringUtil.isEmpty(request.getParameter(method.getName()
                        + ".x"))
                || !StringUtil.isEmpty(request.getParameter(method.getName()
                        + ".y"));
    }

    /**
     * クエリストリングを返します。
     * 
     * @param paramPath
     *            パラメータ用のパス
     * @return クエリストリング
     */
    public String getQueryString(String paramPath) {
        String urlEncodedMethodName = URLEncoderUtil.encode(method.getName());
        if (StringUtil.isEmpty(paramPath)) {
            return "?" + METHOD_NAME + "=" + urlEncodedMethodName;
        }
        Matcher matcher = urlPatternRegexp.matcher(paramPath);
        if (!matcher.find()) {
            return "?" + METHOD_NAME + "=" + urlEncodedMethodName;
        }
        if (urlParamNames.size() == 0) {
            return "?" + METHOD_NAME + "=" + urlEncodedMethodName;
        }
        StringBuilder sb = new StringBuilder(50);
        sb.append("?");
        int index = 1;
        for (String name : urlParamNames) {
            if (index != 1) {
                sb.append("&");
            }
            sb.append(URLEncoderUtil.encode(name)).append("=").append(
                    URLEncoderUtil.encode(matcher.group(index++)));
        }
        sb.append("&").append(METHOD_NAME).append("=").append(
                urlEncodedMethodName);
        return sb.toString();
    }
}