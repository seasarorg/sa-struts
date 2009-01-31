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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.OutputStreamUtil;
import org.seasar.struts.upload.S2MultipartRequestHandler;

/**
 * ファイルアップロード用のユーティリティです。
 * 
 * @author higa
 * 
 */
public final class UploadUtil {

    private UploadUtil() {
    }

    /**
     * ファイルアップロードのサイズの上限を超えていないかどうかをチェックします。
     * 
     * @param request
     *            リクエスト
     * @return ファイルアップロードのサイズの上限を超えていないかどうか。 超えていない場合はtrue。
     */
    public static boolean checkSizeLimit(HttpServletRequest request) {
        SizeLimitExceededException e = (SizeLimitExceededException) request
                .getAttribute(S2MultipartRequestHandler.SIZE_EXCEPTION_KEY);
        if (e != null) {
            ActionMessages errors = new ActionMessages();
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "errors.upload.size", new Object[] { e.getActualSize(),
                            e.getPermittedSize() }));
            ActionMessagesUtil.addErrors(request, errors);
            return false;
        }
        return true;
    }

    /**
     * アップロードされたデータをファイルに書き出します。
     * 
     * @param path
     *            ファイルのパス
     * @param formFile
     *            アップロードされたデータ
     */
    public static void write(String path, FormFile formFile) {
        if (formFile == null || formFile.getFileSize() == 0) {
            return;
        }
        BufferedOutputStream out = null;
        InputStream in = null;
        try {
            in = formFile.getInputStream();
            out = new BufferedOutputStream(new FileOutputStream(path));
            InputStreamUtil.copy(in, out);
            out.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            try {
                InputStreamUtil.close(in);
            } finally {
                OutputStreamUtil.close(out);
            }
        }
    }
}
