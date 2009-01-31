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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.apache.struts.taglib.html.FormTag;
import org.apache.struts.taglib.html.SubmitTag;

/**
 * Seasar2用のSubmitTagです。
 * 
 * @author higa
 * 
 */
public class S2SubmitTag extends SubmitTag {

    /**
     * JavaScriptの検証スクリプトを出力するかどうかです
     */
    protected boolean clientValidate = false;

    private static final long serialVersionUID = 1L;

    /**
     * JavaScriptの検証スクリプトを出力するかどうかを返します。
     * 
     * @return JavaScriptの検証スクリプトを出力するかどうか
     */
    public boolean isClientValidate() {
        return clientValidate;
    }

    /**
     * JavaScriptの検証スクリプトを出力するかどうかを設定します。
     * 
     * @param clientValidate
     *            JavaScriptの検証スクリプトを出力するかどうか
     */
    public void setClientValidate(boolean clientValidate) {
        this.clientValidate = clientValidate;
    }

    @Override
    public void release() {
        super.release();
        clientValidate = false;
    }

    @Override
    public int doEndTag() throws JspException {
        if (clientValidate) {
            FormTag formTag = (FormTag) findAncestorWithClass(this,
                    FormTag.class);
            if (formTag == null) {
                throw new JspTagException("FormTag not found.");
            }
            String actionFormName = formTag.getBeanName();
            StringBuilder sb = new StringBuilder();
            sb.append("var myForm = document.forms['").append(actionFormName)
                    .append("'];");
            sb.append("myForm.id='").append(actionFormName).append("_").append(
                    property).append("'; ");
            sb.append("return validate").append(
                    actionFormName.substring(0, 1).toUpperCase()).append(
                    actionFormName.substring(1)).append("_").append(property)
                    .append("(myForm);");
            String originalOnclick = getOnclick();
            if (originalOnclick == null
                    || originalOnclick.startsWith("var myForm")) {
                setOnclick(sb.toString());
            } else {
                setOnclick(originalOnclick + ";" + sb.toString());
            }
        }
        return super.doEndTag();
    }
}