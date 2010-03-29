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
package org.seasar.struts.upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.struts.Globals;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.upload.CommonsMultipartRequestHandler;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;
import org.apache.struts.upload.MultipartRequestWrapper;

/**
 * Seasar2用のマルチパートリクエストハンドラです。
 * 
 * @author higa
 * 
 */
public class S2MultipartRequestHandler extends CommonsMultipartRequestHandler {

    /**
     * ファイルアップロードでサイズオーバーの例外をリクエストの属性に格納するときのキーです。
     */
    public static final String SIZE_EXCEPTION_KEY = S2MultipartRequestHandler.class
            .getName()
            + ".EXCEPTION";

    /**
     * テキストとファイルのパラメータです。
     */
    @SuppressWarnings("unchecked")
    protected Hashtable elementsAll;

    /**
     * ファイルのパラメータです。
     */
    @SuppressWarnings("unchecked")
    protected Hashtable elementsFile;

    /**
     * テキストのパラメータです。
     */
    @SuppressWarnings("unchecked")
    protected Hashtable elementsText;

    @SuppressWarnings("unchecked")
    @Override
    public void handleRequest(HttpServletRequest request)
            throws ServletException {
        ModuleConfig ac = (ModuleConfig) request
                .getAttribute(Globals.MODULE_KEY);
        ServletFileUpload upload = new ServletFileUpload(
                new DiskFileItemFactory((int) getSizeThreshold(ac), new File(
                        getRepositoryPath(ac))));
        upload.setHeaderEncoding(request.getCharacterEncoding());
        upload.setSizeMax(getSizeMax(ac));
        elementsText = new Hashtable();
        elementsFile = new Hashtable();
        elementsAll = new Hashtable();
        List items = null;
        try {
            items = upload.parseRequest(request);
        } catch (SizeLimitExceededException e) {
            request.setAttribute(
                    MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED,
                    Boolean.TRUE);
            request.setAttribute(SIZE_EXCEPTION_KEY, e);
            try {
                InputStream is = request.getInputStream();
                try {
                    byte[] buf = new byte[1024];
                    @SuppressWarnings("unused")
                    int len = 0;
                    while ((len = is.read(buf)) != -1) {
                    }
                } catch (Exception ignore) {
                } finally {
                    try {
                        is.close();
                    } catch (Exception ignore) {
                    }
                }
            } catch (Exception ignore) {
            }
            return;
        } catch (FileUploadException e) {
            log.error("Failed to parse multipart request", e);
            throw new ServletException(e);
        }

        // Partition the items into form fields and files.
        Iterator iter = items.iterator();
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();

            if (item.isFormField()) {
                addTextParameter(request, item);
            } else {
                addFileParameter(item);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Hashtable getTextElements() {
        return elementsText;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Hashtable getFileElements() {
        return elementsFile;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Hashtable getAllElements() {
        return elementsAll;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void rollback() {
        Iterator iter = elementsFile.values().iterator();
        while (iter.hasNext()) {
            FormFile formFile = (FormFile) iter.next();
            formFile.destroy();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTextParameter(HttpServletRequest request, FileItem item) {
        String name = item.getFieldName();
        String value = null;
        boolean haveValue = false;
        String encoding = request.getCharacterEncoding();
        if (encoding != null) {
            try {
                value = item.getString(encoding);
                haveValue = true;
            } catch (Exception e) {
            }
        }
        if (!haveValue) {
            try {
                value = item.getString("ISO-8859-1");
            } catch (java.io.UnsupportedEncodingException uee) {
                value = item.getString();
            }
            haveValue = true;
        }
        if (request instanceof MultipartRequestWrapper) {
            MultipartRequestWrapper wrapper = (MultipartRequestWrapper) request;
            wrapper.setParameter(name, value);
        }
        String[] oldArray = (String[]) elementsText.get(name);
        String[] newArray;
        if (oldArray != null) {
            newArray = new String[oldArray.length + 1];
            System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
            newArray[oldArray.length] = value;
        } else {
            newArray = new String[] { value };
        }
        elementsText.put(name, newArray);
        elementsAll.put(name, newArray);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addFileParameter(FileItem item) {
        FormFile formFile = new S2FormFile(item);

        elementsFile.put(item.getFieldName(), formFile);
        elementsAll.put(item.getFieldName(), formFile);
    }

    /**
     * Seasar2用のフォームファイルです。
     * 
     */
    protected static class S2FormFile implements FormFile, Serializable {

        private static final long serialVersionUID = 1L;

        FileItem fileItem;

        /**
         * コンストラクタです。
         * 
         * @param fileItem
         *            ファイルアイテム
         */
        public S2FormFile(FileItem fileItem) {
            this.fileItem = fileItem;
        }

        public String getContentType() {
            return fileItem.getContentType();
        }

        public void setContentType(String contentType) {
            throw new UnsupportedOperationException(
                    "The setContentType() method is not supported.");
        }

        public int getFileSize() {
            return (int) fileItem.getSize();
        }

        public void setFileSize(int filesize) {
            throw new UnsupportedOperationException(
                    "The setFileSize() method is not supported.");
        }

        public String getFileName() {
            return getBaseFileName(fileItem.getName());
        }

        public void setFileName(String fileName) {
            throw new UnsupportedOperationException(
                    "The setFileName() method is not supported.");
        }

        public byte[] getFileData() throws FileNotFoundException, IOException {
            return fileItem.get();
        }

        public InputStream getInputStream() throws FileNotFoundException,
                IOException {
            return fileItem.getInputStream();
        }

        public void destroy() {
            fileItem.delete();
        }

        /**
         * 基準となるファイル名を返します。
         * 
         * @param filePath
         *            ファイルパス
         * @return ファイル名
         */
        protected String getBaseFileName(String filePath) {

            // First, ask the JDK for the base file name.
            String fileName = new File(filePath).getName();

            // Now check for a Windows file name parsed incorrectly.
            int colonIndex = fileName.indexOf(":");
            if (colonIndex == -1) {
                // Check for a Windows SMB file path.
                colonIndex = fileName.indexOf("\\\\");
            }
            int backslashIndex = fileName.lastIndexOf("\\");

            if (colonIndex > -1 && backslashIndex > -1) {
                // Consider this filename to be a full Windows path, and parse
                // it
                // accordingly to retrieve just the base file name.
                fileName = fileName.substring(backslashIndex + 1);
            }

            return fileName;
        }

        @Override
        public String toString() {
            return getFileName();
        }
    }
}
