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
package org.seasar.struts.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 * 
 */
public class ActionMessagesUtilTest extends S2TestCase {

    /**
     * @throws Exception
     */
    public void testSaveErrors_request() throws Exception {
        ActionMessages errors = new ActionMessages();
        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("hoge",
                false));
        HttpServletRequest request = getRequest();
        ActionMessagesUtil.saveErrors(request, errors);
        assertFalse(((ActionMessages) request.getAttribute(Globals.ERROR_KEY))
                .isEmpty());
    }

    /**
     * @throws Exception
     */
    public void testSaveErrors_session() throws Exception {
        ActionMessages errors = new ActionMessages();
        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("hoge",
                false));
        HttpSession session = getRequest().getSession();
        ActionMessagesUtil.saveErrors(session, errors);
        assertFalse(((ActionMessages) session.getAttribute(Globals.ERROR_KEY))
                .isEmpty());
    }

    /**
     * @throws Exception
     */
    public void testHasErrors_request() throws Exception {
        ActionMessages errors = new ActionMessages();
        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("hoge",
                false));
        HttpServletRequest request = getRequest();
        ActionMessagesUtil.saveErrors(request, errors);
        assertTrue(ActionMessagesUtil.hasErrors(request));
    }

    /**
     * @throws Exception
     */
    public void testHasErrors_request_errorsNotExist() throws Exception {
        HttpServletRequest request = getRequest();
        assertFalse(ActionMessagesUtil.hasErrors(request));
    }

    /**
     * @throws Exception
     */
    public void testHasErrors_request_errorsEmpty() throws Exception {
        ActionMessages errors = new ActionMessages();
        HttpServletRequest request = getRequest();
        ActionMessagesUtil.saveErrors(request, errors);
        assertFalse(ActionMessagesUtil.hasErrors(request));
    }

    /**
     * @throws Exception
     */
    public void testSaveMessages_request() throws Exception {
        ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("hoge",
                false));
        HttpServletRequest request = getRequest();
        ActionMessagesUtil.saveMessages(request, messages);
        assertFalse(((ActionMessages) request.getAttribute(Globals.MESSAGE_KEY))
                .isEmpty());
    }

    /**
     * @throws Exception
     */
    public void testSaveMessages_session() throws Exception {
        ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("hoge",
                false));
        HttpSession session = getRequest().getSession();
        ActionMessagesUtil.saveMessages(session, messages);
        assertFalse(((ActionMessages) session.getAttribute(Globals.MESSAGE_KEY))
                .isEmpty());
    }

    /**
     * @throws Exception
     */
    public void testAddErrors_request() throws Exception {
        ActionMessages errors = new ActionMessages();
        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("hoge",
                false));
        HttpServletRequest request = getRequest();
        ActionMessagesUtil.addErrors(request, errors);
        assertFalse(((ActionMessages) request.getAttribute(Globals.ERROR_KEY))
                .isEmpty());
    }

    /**
     * @throws Exception
     */
    public void testAddErrors_session() throws Exception {
        ActionMessages errors = new ActionMessages();
        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("hoge",
                false));
        HttpSession session = getRequest().getSession();
        ActionMessagesUtil.addErrors(session, errors);
        assertFalse(((ActionMessages) session.getAttribute(Globals.ERROR_KEY))
                .isEmpty());
    }

    /**
     * @throws Exception
     */
    public void testAddMessages_request() throws Exception {
        ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("hoge",
                false));
        HttpServletRequest request = getRequest();
        ActionMessagesUtil.addMessages(request, messages);
        assertFalse(((ActionMessages) request.getAttribute(Globals.MESSAGE_KEY))
                .isEmpty());
    }

    /**
     * @throws Exception
     */
    public void testAddMessages_session() throws Exception {
        ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("hoge",
                false));
        HttpSession session = getRequest().getSession();
        ActionMessagesUtil.addMessages(session, messages);
        assertFalse(((ActionMessages) session.getAttribute(Globals.MESSAGE_KEY))
                .isEmpty());
    }

}