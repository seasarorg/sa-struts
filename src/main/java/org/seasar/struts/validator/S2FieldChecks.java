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
package org.seasar.struts.validator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.util.ValidatorUtils;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.FieldChecks;
import org.apache.struts.validator.Resources;

/**
 * Seasar2用の検証メソッドです。
 * 
 * @author Satoshi Kimura
 * @author higa
 */
public class S2FieldChecks extends FieldChecks {

    private static final long serialVersionUID = 1L;

    /**
     * バイト長が最大値より小さいかをチェックします。
     * 
     * @param bean
     *            JavaBeans
     * @param validatorAction
     *            バリデータアクション
     * @param field
     *            フィールド定義
     * @param errors
     *            エラーメッセージの入れ物
     * @param validator
     *            バリデータ
     * @param request
     *            リクエスト
     * @return 検証結果がOKかどうか
     */
    public static boolean validateMaxByteLength(Object bean,
            ValidatorAction validatorAction, Field field,
            ActionMessages errors, Validator validator,
            HttpServletRequest request) {
        String value = getValueAsString(bean, field);
        if (!GenericValidator.isBlankOrNull(value)) {
            try {
                int max = Integer.parseInt(field.getVarValue("maxbytelength"));
                String charset = field.getVarValue("charset");
                if (!S2GenericValidator.maxByteLength(value, max, charset)) {
                    addError(errors, field, validator, validatorAction, request);
                    return false;
                }
            } catch (Exception e) {
                addError(errors, field, validator, validatorAction, request);
                return false;
            }
        }
        return true;
    }

    /**
     * バイト長が最小値より大きいかをチェックします。
     * 
     * @param bean
     *            JavaBeans
     * @param validatorAction
     *            バリデータアクション
     * @param field
     *            フィールド定義
     * @param errors
     *            エラーメッセージの入れ物
     * @param validator
     *            バリデータ
     * @param request
     *            リクエスト
     * @return 検証結果がOKかどうか
     */
    public static boolean validateMinByteLength(Object bean,
            ValidatorAction validatorAction, Field field,
            ActionMessages errors, Validator validator,
            HttpServletRequest request) {
        String value = getValueAsString(bean, field);
        if (!GenericValidator.isBlankOrNull(value)) {
            try {
                int min = Integer.parseInt(field.getVarValue("minbytelength"));
                String charset = field.getVarValue("charset");
                if (!S2GenericValidator.minByteLength(value, min, charset)) {
                    addError(errors, field, validator, validatorAction, request);
                    return false;
                }
            } catch (Exception e) {
                addError(errors, field, validator, validatorAction, request);
                return false;
            }
        }
        return true;
    }

    /**
     * 長整数が指定した範囲内かどうかをチェックします。
     * 
     * @param bean
     *            JavaBeans
     * @param validatorAction
     *            バリデータアクション
     * @param field
     *            フィールド定義
     * @param errors
     *            エラーメッセージの入れ物
     * @param validator
     *            バリデータ
     * @param request
     *            リクエスト
     * @return 検証結果がOKかどうか
     */
    public static boolean validateLongRange(Object bean,
            ValidatorAction validatorAction, Field field,
            ActionMessages errors, Validator validator,
            HttpServletRequest request) {
        String value = getValueAsString(bean, field);
        if (!GenericValidator.isBlankOrNull(value)) {
            try {
                long longValue = Long.parseLong(value);
                long min = Long.parseLong(field.getVarValue("min"));
                long max = Long.parseLong(field.getVarValue("max"));
                if (!GenericValidator.isInRange(longValue, min, max)) {
                    addError(errors, field, validator, validatorAction, request);
                    return false;
                }
            } catch (Exception e) {
                addError(errors, field, validator, validatorAction, request);
                return false;
            }
        }
        return true;
    }

    /**
     * 値を文字列として返します。
     * 
     * @param bean
     *            JavaBeans
     * @param field
     *            フィールド定義
     * @return 値
     */
    protected static String getValueAsString(Object bean, Field field) {
        if (isString(bean)) {
            return (String) bean;
        }
        return ValidatorUtils.getValueAsString(bean, field.getProperty());
    }

    /**
     * エラーメッセージを追加します。
     * 
     * @param errors
     *            エラーメッセージの入れ物
     * @param field
     *            フィールド定義
     * @param validator
     *            バリデータ
     * @param validatorAction
     *            バリデータアクション
     * @param request
     *            リクエスト
     */
    protected static void addError(ActionMessages errors, Field field,
            Validator validator, ValidatorAction validatorAction,
            HttpServletRequest request) {
        errors.add(field.getKey(), Resources.getActionMessage(validator,
                request, validatorAction, field));
    }
}
