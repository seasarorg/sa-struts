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
package org.seasar.struts.taglib;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.exception.ParseRuntimeException;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionUtil;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ResponseUtil;
import org.seasar.struts.util.RoutingUtil;
import org.seasar.struts.util.URLEncoderUtil;

/**
 * Seasar2用のファンクションです。
 * 
 * @author higa
 * 
 */
public class S2Functions {

    private static final int HIGHEST_SPECIAL = '>';

    private static String BR = "<br />";

    private static String NBSP = "&nbsp;";

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
    public static String h(Object input) {
        if (input == null) {
            return "";
        }
        String str = "";
        if (input.getClass().isArray()) {
            Class<?> clazz = input.getClass().getComponentType();
            if (clazz == String.class) {
                str = Arrays.toString((Object[]) input);
            } else if (clazz == boolean.class) {
                str = Arrays.toString((boolean[]) input);
            } else if (clazz == int.class) {
                str = Arrays.toString((int[]) input);
            } else if (clazz == long.class) {
                str = Arrays.toString((long[]) input);
            } else if (clazz == byte.class) {
                str = Arrays.toString((byte[]) input);
            } else if (clazz == short.class) {
                str = Arrays.toString((short[]) input);
            } else if (clazz == float.class) {
                str = Arrays.toString((float[]) input);
            } else if (clazz == double.class) {
                str = Arrays.toString((double[]) input);
            } else if (clazz == char.class) {
                str = Arrays.toString((char[]) input);
            } else {
                str = Arrays.toString((Object[]) input);
            }
        } else {
            str = input.toString();
        }
        return escape(str);
    }

    /**
     * 文字列をHTMLエスケープします。
     * 
     * @param buffer
     *            文字列
     * @return エスケープした結果
     */
    public static String escape(String buffer) {
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
     * URLを計算します。
     * 
     * @param input
     *            入力値
     * @return エスケープした結果
     */
    public static String url(String input) {
        String contextPath = RequestUtil.getRequest().getContextPath();
        StringBuilder sb = new StringBuilder();
        if (contextPath.length() > 1) {
            sb.append(contextPath);
        }
        if (StringUtil.isEmpty(input)) {
            sb.append(ActionUtil.calcActionPath());
        } else if (!input.startsWith("/")) {
            sb.append(ActionUtil.calcActionPath()).append(input);
        } else {
            String[] names = StringUtil.split(input, "/");
            S2Container container = SingletonS2ContainerFactory.getContainer();
            StringBuilder sb2 = new StringBuilder(50);
            String input2 = input;
            for (int i = 0; i < names.length; i++) {
                if (container.hasComponentDef(sb2 + names[i] + "Action")) {
                    String actionPath = RoutingUtil.getActionPath(names, i);
                    String paramPath = RoutingUtil.getParamPath(names, i + 1);
                    if (StringUtil.isEmpty(paramPath)) {
                        input2 = actionPath + "/";
                        break;
                    }
                }
                sb2.append(names[i] + "_");
            }
            sb.append(input2);
        }
        return ResponseUtil.getResponse().encodeURL(sb.toString());
    }

    /**
     * 日付に変換します。
     * 
     * @param input
     *            入力値
     * @param pattern
     *            パターン
     * @return 変換した結果
     */
    public static Date date(String input, String pattern) {
        if (StringUtil.isEmpty(input)) {
            return null;
        }
        if (StringUtil.isEmpty(pattern)) {
            throw new NullPointerException("pattern");
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(input);
        } catch (ParseException e) {
            throw new ParseRuntimeException(e);
        }
    }

    /**
     * 数値に変換します。
     * 
     * @param input
     *            入力値
     * @param pattern
     *            パターン
     * @return 変換した結果
     */
    public static Number number(String input, String pattern) {
        if (StringUtil.isEmpty(input)) {
            return null;
        }
        if (StringUtil.isEmpty(pattern)) {
            throw new NullPointerException("pattern");
        }
        try {
            DecimalFormat format = new DecimalFormat(pattern);
            return format.parse(input);
        } catch (ParseException e) {
            throw new ParseRuntimeException(e);
        }
    }

    /**
     * 改行をbrタグに変換します。
     * 
     * @param input
     *            入力値
     * @return 変換した結果
     */
    public static String br(String input) {
        if (StringUtil.isEmpty(input)) {
            return "";
        }
        return input.replaceAll("\r\n", BR).replaceAll("\r", BR).replaceAll(
                "\n", BR);
    }

    /**
     * 空白を&nbsp;に変換します。
     * 
     * @param input
     *            入力値
     * @return 変換した結果
     */
    public static String nbsp(String input) {
        if (StringUtil.isEmpty(input)) {
            return "";
        }
        return input.replaceAll(" ", NBSP);
    }

    /**
     * 値をラベルに変換します。
     * 
     * @param value
     *            値
     * @param dataList
     *            JavaBeansあるいはMapのリスト
     * @param valueName
     *            値用のプロパティ名
     * @param labelName
     *            ラベル用のプロパティ名
     * 
     * @param input
     *            入力値
     * @return ラベル
     */
    @SuppressWarnings("unchecked")
    public static String label(Object value, List dataList, String valueName,
            String labelName) {
        if (valueName == null) {
            throw new NullPointerException("valueName");
        }
        if (labelName == null) {
            throw new NullPointerException("labelName");
        }
        if (dataList == null) {
            throw new NullPointerException("dataList");
        }
        for (Object o : dataList) {
            if (o instanceof Map) {
                Map<String, Object> m = (Map<String, Object>) o;
                Object v = m.get(valueName);
                if (equals(value, v)) {
                    return (String) m.get(labelName);
                }
            } else {
                BeanDesc beanDesc = BeanDescFactory.getBeanDesc(o.getClass());
                Object v = beanDesc.getPropertyDesc(valueName).getValue(o);
                if (equals(value, v)) {
                    return (String) beanDesc.getPropertyDesc(labelName)
                            .getValue(o);
                }
            }
        }
        return "";
    }

    /**
     * 2つのオブジェクトの値が等しいかどうかを返します。
     * 
     * @param o1
     *            オブジェクト1
     * @param o2
     *            オブジェクト2
     * @return 2つのオブジェクトの値が等しいかどうか
     */
    private static boolean equals(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        }
        if (o1 == null) {
            if (o2 instanceof String && StringUtil.isEmpty((String) o2)) {
                return true;
            }
            return false;
        }
        if (o2 == null) {
            if (o1 instanceof String && StringUtil.isEmpty((String) o1)) {
                return true;
            }
            return false;
        }
        if (o1.getClass() == o2.getClass()) {
            return o1.equals(o2);
        }
        return o1.toString().equals(o2.toString());
    }
}