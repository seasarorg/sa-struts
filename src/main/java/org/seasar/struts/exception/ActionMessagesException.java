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

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.struts.enums.SaveType;

/**
 * アクションメッセージ用の例外です。
 * 
 * @author higa
 * 
 */
public class ActionMessagesException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * アクションメッセージの集合です。
     */
    protected ActionMessages messages = new ActionMessages();

    /**
     * エラーメッセージをどこに保存するかです。
     */
    protected SaveType saveErrors = SaveType.REQUEST;

    /**
     * インスタンスを構築します。
     * 
     * @param key
     *            キー
     * @param values
     *            値の配列
     */
    public ActionMessagesException(String key, Object... values) {
        addMessage(key, values);
    }

    /**
     * インスタンスを構築します。
     * 
     * @param key
     *            キー
     * @param resource
     *            リソースかどうか
     */
    public ActionMessagesException(String key, boolean resource) {
        addMessage(key, resource);
    }

    /**
     * アクションメッセージの集合を返します。
     * 
     * @return アクションメッセージの集合
     */
    public ActionMessages getMessages() {
        return messages;
    }

    /**
     * メッセージを追加します。
     * 
     * @param property
     *            プロパティ
     * @param message
     *            メッセージ
     */
    public void addMessage(String property, ActionMessage message) {
        messages.add(property, message);
    }

    /**
     * メッセージを追加します。
     * 
     * @param key
     *            キー
     * @param values
     *            値の配列
     */
    public void addMessage(String key, Object... values) {
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(key,
                values));
    }

    /**
     * メッセージを追加します。
     * 
     * @param key
     *            キー
     * @param resource
     *            リソースかどうか
     */
    public void addMessage(String key, boolean resource) {
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(key,
                resource));
    }

    /**
     * エラーメッセージの保存場所を返します。
     * 
     * @return エラーメッセージの保存場所
     */
    public SaveType getSaveErrors() {
        return saveErrors;
    }

    /**
     * エラーメッセージの保存場所を設定します。
     * 
     * @param saveErrors
     *            エラーメッセージの保存場所
     */
    public void setSaveErrors(SaveType saveErrors) {
        this.saveErrors = saveErrors;
    }

    @Override
    public String getMessage() {
        return messages.toString();
    }
}