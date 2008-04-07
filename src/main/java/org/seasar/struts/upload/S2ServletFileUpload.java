/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.struts.upload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.struts.upload.MultipartRequestHandler;

/**
 * @author higa
 * 
 */
public class S2ServletFileUpload extends ServletFileUpload {

    /**
     * 例外のキーです。
     */
    public static final String EXCEPTION_KEY = S2ServletFileUpload.class
            .getName()
            + ".EXCEPTION";

    /**
     * コンストラクタです。
     */
    public S2ServletFileUpload() {
    }

    /**
     * コンストラクタです。
     * 
     * @param fileItemFactory
     *            ファイルアイテムファクトリです。
     */
    public S2ServletFileUpload(FileItemFactory fileItemFactory) {
        super(fileItemFactory);
    }

    @Override
    public List<FileItem> parseRequest(HttpServletRequest request)
            throws FileUploadException {
        return parseRequest(new S2ServletRequestContext(request));
    }

    @Override
    public List<FileItem> parseRequest(RequestContext ctx)
            throws FileUploadException {
        try {
            FileItemIterator iter = getItemIterator(ctx);
            List<FileItem> items = new ArrayList<FileItem>();
            FileItemFactory fac = getFileItemFactory();
            if (fac == null) {
                throw new NullPointerException(
                        "No FileItemFactory has been set.");
            }
            while (iter.hasNext()) {
                try {
                    FileItemStream item = iter.next();
                    FileItem fileItem = fac.createItem(item.getFieldName(),
                            item.getContentType(), item.isFormField(), item
                                    .getName());
                    try {
                        Streams.copy(item.openStream(), fileItem
                                .getOutputStream(), true);
                    } catch (FileUploadIOException e) {
                        throw (FileUploadException) e.getCause();
                    } catch (IOException e) {
                        throw new IOFileUploadException("Processing of "
                                + MULTIPART_FORM_DATA + " request failed. "
                                + e.getMessage(), e);
                    }
                    items.add(fileItem);
                } catch (FileUploadIOException e) {
                    FileUploadException fue = (FileUploadException) e
                            .getCause();
                    if (fue instanceof SizeException) {
                        HttpServletRequest request = ((S2ServletRequestContext) ctx).request;
                        request.setAttribute(EXCEPTION_KEY, fue);
                        request
                                .setAttribute(
                                        MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED,
                                        Boolean.TRUE);
                        continue;
                    }
                    throw fue;
                }
            }
            return items;

        } catch (FileUploadIOException e) {
            throw (FileUploadException) e.getCause();
        } catch (IOException e) {
            throw new FileUploadException(e.getMessage(), e);
        }
    }
}
