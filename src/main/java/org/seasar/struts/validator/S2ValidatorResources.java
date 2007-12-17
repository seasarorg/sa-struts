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
package org.seasar.struts.validator;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.validator.ValidatorResources;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;
import org.xml.sax.SAXException;

/**
 * Seasar2用の検証リソースです。
 * 
 * @author higa
 * 
 */
public class S2ValidatorResources extends ValidatorResources implements
        Disposable {

    private static final long serialVersionUID = 1L;

    /**
     * 初期化されたかどうかです。
     */
    protected volatile boolean initialized = false;

    /**
     * インスタンスを構築します。
     * 
     * @param streams
     *            入力ストリームの配列
     * @throws IOException
     *             IO例外が発生した場合。
     * @throws SAXException
     *             SAX例外が発生した場合。
     */
    public S2ValidatorResources(InputStream[] streams) throws IOException,
            SAXException {
        super(streams);
        initialize();
    }

    /**
     * 初期化します。
     */
    public void initialize() {
        DisposableUtil.add(this);
        initialized = true;
    }

    public void dispose() {
        hFormSets.clear();
        initialized = false;
    }
}
