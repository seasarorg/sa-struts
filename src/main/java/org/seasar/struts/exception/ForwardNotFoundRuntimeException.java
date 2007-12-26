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
package org.seasar.struts.exception;

import org.seasar.framework.exception.SRuntimeException;

/**
 * 遷移先が見つからない場合の例外です。
 * 
 * @author higa
 * 
 */
public class ForwardNotFoundRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = 1L;

    private Class<?> actionClass;

    private String forwardName;

    /**
     * インスタンスを構築します。
     * 
     * @param actionClass
     *            アクションクラス
     * @param forwardName
     *            遷移先
     */
    public ForwardNotFoundRuntimeException(Class<?> actionClass,
            String forwardName) {
        super("ESAS0007", new Object[] { actionClass.getName(), forwardName });
        this.actionClass = actionClass;
        this.forwardName = forwardName;
    }

    /**
     * アクションクラスを返します。
     * 
     * @return アクションクラス
     */
    public Class<?> getActionClass() {
        return actionClass;
    }

    /**
     * 遷移先を返します。
     * 
     * @return 遷移先
     */
    public String getForwardName() {
        return forwardName;
    }
}