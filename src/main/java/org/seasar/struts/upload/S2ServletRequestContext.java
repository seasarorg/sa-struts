/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.struts.upload;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.RequestContext;

/**
 * Seasar2用のリクエストコンテキストです。
 * 
 * @author higa
 * 
 */
public class S2ServletRequestContext implements RequestContext {

    /**
     * リクエストです。
     */
    protected HttpServletRequest request;

    /**
     * コンストラクタです。
     * 
     * @param request
     *            リクエスト
     */
    public S2ServletRequestContext(HttpServletRequest request) {
        this.request = request;
    }

    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    public int getContentLength() {
        return request.getContentLength();
    }

    public String getContentType() {
        return request.getContentType();
    }

    public InputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

    /**
     * リクエストを返します。
     * 
     * @return リクエスト
     */
    public HttpServletRequest getRequest() {
        return request;
    }
}
