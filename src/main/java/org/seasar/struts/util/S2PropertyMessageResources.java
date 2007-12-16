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

import java.util.Locale;

import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;
import org.seasar.framework.message.MessageResourceBundle;
import org.seasar.framework.message.MessageResourceBundleFactory;

/**
 * Seasar2用のプロパティメッセージリソースです。
 * 
 * @author higa
 * 
 */
public class S2PropertyMessageResources extends MessageResources {

    private static final long serialVersionUID = 1L;

    /**
     * インスタンスを構築します。
     * 
     * @param factory
     *            メッセージリソースファクトリ
     * @param config
     *            設定
     */
    public S2PropertyMessageResources(MessageResourcesFactory factory,
            String config) {
        super(factory, config);
    }

    @Override
    public String getMessage(Locale locale, String key) {
        MessageResourceBundle bundle = MessageResourceBundleFactory.getBundle(
                config, locale);
        return bundle.get(key);
    }
}