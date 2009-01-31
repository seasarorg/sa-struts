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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.validator.ValidatorPlugIn;
import org.seasar.framework.util.InputStreamUtil;
import org.xml.sax.SAXException;

/**
 * Seasar2用の検証プラグインです。
 * 
 * @author higa
 * 
 */
public class S2ValidatorPlugIn extends ValidatorPlugIn {

    /**
     * ログです。
     */
    protected static Log log = LogFactory.getLog(S2ValidatorPlugIn.class);

    /**
     * リソースのデリミタです。
     */
    protected final static String RESOURCE_DELIM = ",";

    /**
     * アクションサーブレットです。
     */
    protected ActionServlet actionServlet = null;

    /**
     * モジュール設定です。
     */
    protected ModuleConfig moduleConfig;

    @Override
    public void init(ActionServlet actionServlet, ModuleConfig moduleConfig)
            throws ServletException {
        this.actionServlet = actionServlet;
        this.moduleConfig = moduleConfig;
        super.init(actionServlet, moduleConfig);
    }

    @Override
    protected void initResources() throws IOException, ServletException {
        String pathnames = getPathnames();
        if (pathnames == null || pathnames.length() <= 0) {
            return;
        }
        StringTokenizer st = new StringTokenizer(pathnames, RESOURCE_DELIM);
        List<InputStream> streamList = new ArrayList<InputStream>();
        try {
            while (st.hasMoreTokens()) {
                String validatorRules = st.nextToken().trim();
                if (log.isInfoEnabled()) {
                    log.info("Loading validation rules file from '"
                            + validatorRules + "'");
                }
                InputStream input = actionServlet.getServletContext()
                        .getResourceAsStream(validatorRules);
                if (input == null) {
                    input = getClass().getResourceAsStream(validatorRules);
                }

                if (input != null) {
                    BufferedInputStream bis = new BufferedInputStream(input);
                    streamList.add(bis);
                } else {
                    throw new ServletException(
                            "Skipping validation rules file from '"
                                    + validatorRules
                                    + "'.  No stream could be opened.");
                }
            }
            int streamSize = streamList.size();
            InputStream[] streamArray = streamList
                    .toArray(new InputStream[streamSize]);
            resources = new S2ValidatorResources(streamArray);
        } catch (SAXException sex) {
            log.error("Skipping all validation", sex);
            throw new ServletException(sex);
        } finally {
            Iterator<InputStream> streamIterator = streamList.iterator();
            while (streamIterator.hasNext()) {
                InputStreamUtil.close(streamIterator.next());
            }
        }
    }
}