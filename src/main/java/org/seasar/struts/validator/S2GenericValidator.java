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

import java.io.UnsupportedEncodingException;

import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.util.StringUtil;

/**
 * Seasar2用の汎用バリデータです。
 * 
 * @author Satoshi Kimura
 * @author higa
 */
public class S2GenericValidator {

    private S2GenericValidator() {
    }

    /**
     * バイト長が最小値より大きいかをチェックします。
     * 
     * @param value
     *            値
     * @param min
     *            最小値
     * @param charset
     *            チャーセット
     * @return 検証結果
     */
    public static boolean minByteLength(String value, int min, String charset) {
        return (getBytes(value, charset).length >= min);
    }

    /**
     * バイト長が最大値より小さいかをチェックします。
     * 
     * @param value
     *            値
     * @param max
     *            最大値
     * @param charset
     *            チャーセット
     * @return 検証結果
     */
    public static boolean maxByteLength(String value, int max, String charset) {
        return (getBytes(value, charset).length <= max);
    }

    private static byte[] getBytes(String str, String charset) {
        if (StringUtil.isEmpty(charset)) {
            return str.getBytes();
        }
        try {
            return str.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new IORuntimeException(e);
        }
    }
}