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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.struts.upload.FormFile;
import org.seasar.framework.unit.S2TigerTestCase;
import org.seasar.framework.unit.annotation.EasyMock;
import org.seasar.framework.util.FileUtil;

/**
 * @author ooharak
 * 
 */
public class UploadUtilTest extends S2TigerTestCase {
    @EasyMock
    private FormFile formFile;

    // テストに使う一時ファイル
    private File tempFile;

    // 適当なバイト列
    private byte[] b = new byte[] { 1, 2, 3, (byte) 253, (byte) 254, (byte) 255 };

    private CheckedInputStream in;

    /**
     * @throws Exception
     */
    public void setUpWrite() throws Exception {
        this.in = new CheckedInputStream(new ByteArrayInputStream(b));
        this.tempFile = File.createTempFile(this.getClass().getName(), "tmp");
    }

    /**
     * {@link UploadUtil#write(String, FormFile)}がファイルサイズチェックとストリームの取得を行っていることを検証します。
     * 
     * @throws Exception
     */
    public void recordWrite() throws Exception {
        // ファイルサイズをチェックして
        org.easymock.EasyMock.expect(formFile.getFileSize())
                .andReturn(b.length);

        // ストリームを取得するはず
        org.easymock.EasyMock.expect(formFile.getInputStream()).andReturn(in);
    }

    /**
     * @throws Exception
     */
    public void tearDownWrite() throws Exception {
        if (this.tempFile != null) {
            tempFile.delete();
        }
    }

    /**
     * {@link UploadUtil#write(String, FormFile)} のテストです。
     * 
     * @throws Exception
     */
    public void testWrite() throws Exception {
        UploadUtil.write(tempFile.getCanonicalPath(), formFile);
        assertEquals("Uploaded file and saved file must be identical", Arrays
                .toString(b), Arrays.toString(FileUtil.getBytes(tempFile)));
        assertTrue("input stream must be closed", this.in.isClosed());
    }

    /**
     * 閉じわすれをチェックするInputStream
     * 
     * @author ooharak
     * 
     */
    static class CheckedInputStream extends FilterInputStream {
        private boolean isClosed = false;

        /**
         * @param in
         */
        protected CheckedInputStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.isClosed = true;
        }

        /**
         * @return
         */
        public boolean isClosed() {
            return this.isClosed;
        }

    }

}
