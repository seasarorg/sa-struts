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
package org.seasar.struts.taglib;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.seasar.framework.exception.ParseRuntimeException;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.URLEncoderUtil;

/**
 * Seasar2用のファンクションです。
 * 
 * @author higa
 * 
 */
public class S2Functions {

    private static final int HIGHEST_SPECIAL = '>';

    private static char[][] specialCharactersRepresentation = new char[HIGHEST_SPECIAL + 1][];

    static {
        specialCharactersRepresentation['&'] = "&amp;".toCharArray();
        specialCharactersRepresentation['<'] = "&lt;".toCharArray();
        specialCharactersRepresentation['>'] = "&gt;".toCharArray();
        specialCharactersRepresentation['"'] = "&#034;".toCharArray();
        specialCharactersRepresentation['\''] = "&#039;".toCharArray();
    }

    /**
     * HTMLをエスケープします。
     * 
     * @param input
     *            入力値
     * @return エスケープした結果
     */
    public static String h(String input) {
        if (input == null)
            return "";
        return escape(input);
    }

    private static String escape(String buffer) {
        int start = 0;
        int length = buffer.length();
        char[] arrayBuffer = buffer.toCharArray();
        StringBuilder escapedBuffer = null;

        for (int i = 0; i < length; i++) {
            char c = arrayBuffer[i];
            if (c <= HIGHEST_SPECIAL) {
                char[] escaped = specialCharactersRepresentation[c];
                if (escaped != null) {
                    if (start == 0) {
                        escapedBuffer = new StringBuilder(length + 5);
                    }
                    if (start < i) {
                        escapedBuffer.append(arrayBuffer, start, i - start);
                    }
                    start = i + 1;
                    escapedBuffer.append(escaped);
                }
            }
        }
        if (start == 0) {
            return buffer;
        }
        if (start < length) {
            escapedBuffer.append(arrayBuffer, start, length - start);
        }
        return escapedBuffer.toString();
    }

    /**
     * URLをエスケープします。
     * 
     * @param input
     *            入力値
     * @return エスケープした結果
     */
    public static String u(String input) {
        return URLEncoderUtil.encode(input);
    }

    /**
     * 日付をフォーマットします。
     * 
     * @param input
     *            入力値
     * @param inputPattern
     *            入力パターン
     * @param outputPattern
     *            出力パターン
     * @return フォーマットした結果
     */
    public static String date(String input, String inputPattern,
            String outputPattern) {
        if (StringUtil.isEmpty(input)) {
            return "";
        }
        if (StringUtil.isEmpty(inputPattern)) {
            throw new NullPointerException(inputPattern);
        }
        if (StringUtil.isEmpty(outputPattern)) {
            throw new NullPointerException(outputPattern);
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            Date date = inputFormat.parse(input);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            return outputFormat.format(date);
        } catch (ParseException e) {
            throw new ParseRuntimeException(e);
        }
    }

    /**
     * 数値をフォーマットします。
     * 
     * @param input
     *            入力値
     * @param inputPattern
     *            入力パターン
     * @param outputPattern
     *            出力パターン
     * @return フォーマットした結果
     */
    public static String number(String input, String inputPattern,
            String outputPattern) {
        if (StringUtil.isEmpty(input)) {
            return "";
        }
        if (StringUtil.isEmpty(inputPattern)) {
            throw new NullPointerException(inputPattern);
        }
        if (StringUtil.isEmpty(outputPattern)) {
            throw new NullPointerException(outputPattern);
        }
        try {
            DecimalFormat inputFormat = new DecimalFormat(inputPattern);
            Number number = inputFormat.parse(input);
            DecimalFormat outputFormat = new DecimalFormat(outputPattern);
            return outputFormat.format(number);
        } catch (ParseException e) {
            throw new ParseRuntimeException(e);
        }
    }
}