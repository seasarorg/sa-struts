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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.ValidatorResources;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.container.hotdeploy.HotdeployUtil;
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
     * DTDがどこに登録されているかです。
     */
    protected static final String REGISTRATIONS[] = {
            "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.0//EN",
            "/org/apache/commons/validator/resources/validator_1_0.dtd",
            "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.0.1//EN",
            "/org/apache/commons/validator/resources/validator_1_0_1.dtd",
            "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.1//EN",
            "/org/apache/commons/validator/resources/validator_1_1.dtd",
            "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.1.3//EN",
            "/org/apache/commons/validator/resources/validator_1_1_3.dtd",
            "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.2.0//EN",
            "/org/apache/commons/validator/resources/validator_1_2_0.dtd",
            "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.3.0//EN",
            "/org/apache/commons/validator/resources/validator_1_3_0.dtd" };

    /**
     * 初期化されたかどうかです。
     */
    protected volatile boolean initialized = false;

    /**
     * フォームのマップです。
     */
    protected Map<String, Form> forms = new HashMap<String, Form>();

    /**
     * インスタンスを構築します。
     */
    public S2ValidatorResources() {
    }

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
        URL rulesUrl = ValidatorResources.class
                .getResource("digester-rules.xml");
        Digester digester = DigesterLoader.createDigester(rulesUrl);
        digester.setNamespaceAware(true);
        digester.setValidating(true);
        digester.setUseContextClassLoader(true);
        for (int i = 0; i < REGISTRATIONS.length; i += 2) {
            URL url = ValidatorResources.class
                    .getResource(REGISTRATIONS[i + 1]);
            if (url != null) {
                digester.register(REGISTRATIONS[i], url.toString());
            }
        }
        for (int i = 0; i < streams.length; i++) {
            digester.push(this);
            digester.parse(streams[i]);
        }
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
        forms.clear();
        initialized = false;
    }

    @Override
    public Form getForm(Locale locale, String formKey) {
        if (!initialized) {
            initialize();
        }
        Form form = forms.get(formKey);
        if (form == null) {
            if (HotdeployUtil.isHotdeploy() && formKey.endsWith("Form")) {
                SingletonS2ContainerFactory.getContainer().getComponentDef(
                        formKey.substring(0, formKey.length() - 4));
                form = forms.get(formKey);
            }
        }
        return form;
    }

    /**
     * フォームを追加します。
     * 
     * @param form
     *            フォーム
     */
    public void addForm(Form form) {
        forms.put(form.getName(), form);
    }

    /**
     * 定数を返します。
     * 
     * @param name
     * @return 定数
     */
    @SuppressWarnings("deprecation")
    public String getConstant(String name) {
        return (String) hConstants.get(name);
    }
}
