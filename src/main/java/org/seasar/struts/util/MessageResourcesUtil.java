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
package org.seasar.struts.util;

import java.util.Locale;

import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

/**
 * メッセージリソースに関するユーティリティです。
 * 
 * @author higa
 * 
 */
public final class MessageResourcesUtil {

    private MessageResourcesUtil() {
    }

    /**
     * メッセージリソースを返します。
     * 
     * @return メッセージリソース
     */
    public static MessageResources getMessageResources() {
        return (MessageResources) ServletContextUtil.getServletContext()
                .getAttribute(Globals.MESSAGES_KEY);
    }

    /**
     * メッセージを返します。
     * 
     * @param locale
     *            ロケール
     * @param key
     *            キー
     * @return メッセージ
     */
    public static String getMessage(Locale locale, String key) {
        return getMessageResources().getMessage(locale, key);
    }

    /**
     * メッセージを返します。
     * 
     * @param locale
     *            ロケール
     * @param key
     *            キー
     * @param args
     *            引数の配列
     * @return メッセージ
     */
    public static String getMessage(Locale locale, String key, Object... args) {
        return getMessageResources().getMessage(locale, key, args);
    }

    /**
     * メッセージを返します。
     * 
     * @param key
     *            キー
     * @return メッセージ
     */
    public static String getMessage(String key) {
        return getMessageResources().getMessage(key);
    }

    /**
     * メッセージを返します。
     * 
     * @param key
     *            キー
     * @param args
     *            引数の配列
     * @return メッセージ
     */
    public static String getMessage(String key, Object... args) {
        return getMessageResources().getMessage(key, args);
    }
}