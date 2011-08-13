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

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;

import org.apache.struts.Globals;
import org.apache.struts.taglib.html.Constants;
import org.apache.struts.taglib.html.LinkTag;

/**
 * Seasar2用のLinkTagです。
 * 
 * @author higa
 * 
 */
public class S2LinkTag extends LinkTag {

    private static final long serialVersionUID = 1L;

    @Override
    protected String calculateURL() throws JspException {
        if (href != null) {
            int index = href.indexOf(':');
            if (index > -1) {
                return super.calculateURL();
            }
            String url = S2Functions.url(href);
            if (transaction) {
                HttpSession session = pageContext.getSession();
                if (session != null) {
                    String token = (String) session
                            .getAttribute(Globals.TRANSACTION_TOKEN_KEY);
                    if (token != null) {
                        String c = url != null && url.indexOf('?') >= 0 ? "&"
                                : "?";
                        url = url + c + Constants.TOKEN_KEY + "=" + token;
                    }
                }
            }
            return url;
        }
        return super.calculateURL();
    }
}