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


/**
 * URLのルーティング用のユーティリティです。
 * 
 * @author higa
 * 
 */
public class RoutingUtil {

    /**
     * アクションのパスを返します。
     * 
     * @param names
     *            パスを/で区切った配列
     * @param index
     *            インデックス
     * @return アクションのパス
     */
    public static String getActionPath(String[] names, int index) {
        StringBuilder sb = new StringBuilder(30);
        for (int i = 0; i <= index; i++) {
            sb.append('/').append(names[i]);
        }
        return sb.toString();
    }

    /**
     * パラメータのパスを返します。
     * 
     * @param names
     *            パスを/で区切った配列
     * @param index
     *            インデックス
     * @return パラメータのパス
     */
    public static String getParamPath(String[] names, int index) {
        StringBuilder sb = new StringBuilder(30);
        for (int i = index; i < names.length; i++) {
            if (i != index) {
                sb.append('/');
            }
            sb.append(names[i]);
        }
        return sb.toString();
    }
}
