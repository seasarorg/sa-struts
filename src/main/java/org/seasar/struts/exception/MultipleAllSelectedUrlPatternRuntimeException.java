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
package org.seasar.struts.exception;

import org.seasar.framework.exception.SRuntimeException;

/**
 * {id}のような全選択のURLパターンが複数指定された場合の例外です。
 * 
 * @author higa
 * 
 */
public class MultipleAllSelectedUrlPatternRuntimeException extends
        SRuntimeException {

    private static final long serialVersionUID = 1L;

    private String urlPattern;

    private String urlPattern2;

    /**
     * インスタンスを構築します。
     * 
     * @param urlPattern
     *            URLパターン
     * @param urlPattern2
     *            URLパターン2
     * 
     */
    public MultipleAllSelectedUrlPatternRuntimeException(String urlPattern,
            String urlPattern2) {
        super("ESAS0009", new Object[] { urlPattern, urlPattern2 });
        this.urlPattern = urlPattern;
        this.urlPattern2 = urlPattern2;
    }

    /**
     * URLパターンを返します。
     * 
     * @return URLパターン
     */
    public String getUrlPattern() {
        return urlPattern;
    }

    /**
     * URLパターン2を返します。
     * 
     * @return URLパターン2
     */
    public String getUrlPattern2() {
        return urlPattern2;
    }
}