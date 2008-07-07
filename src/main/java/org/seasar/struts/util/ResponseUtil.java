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
package org.seasar.struts.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.exception.IORuntimeException;

/**
 * レスポンスに関するユーティリティです。
 * 
 * @author higa
 * 
 */
public final class ResponseUtil {

    private ResponseUtil() {
    }

    /**
     * レスポンスを返します。
     * 
     * @return レスポンス
     */
    public static HttpServletResponse getResponse() {
        return SingletonS2Container.getComponent(HttpServletResponse.class);
    }

    /**
     * ダウンロードします。
     * 
     * @param fileName
     *            ファイル名
     * @param data
     *            ダウンロードするデータ
     */
    public static void download(String fileName, byte[] data) {
        HttpServletResponse response = getResponse();
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment; filename=\""
                    + fileName + "\"");
            OutputStream out = response.getOutputStream();
            try {
                out.write(data);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}