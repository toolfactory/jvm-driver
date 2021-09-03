/*
 * This file is part of Burningwave JVM driver.
 *
 * Author: Roberto Gentili
 *
 * Hosted at: https://github.com/burningwave/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2021 Roberto Gentili
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.burningwave.jvm;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;


class Files {
	
	public static void extractAndExecute(Class<?> callerClass, String resourcePath, Consumer<File> extractedFileConsumer) {
        File tempFile = null;
        boolean tempFileIsPosix = false;
        try (InputStream inputSream = callerClass.getResourceAsStream(resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath);) {
            
            if (inputSream == null) {
                throw new FileNotFoundException("Could not find file: " + resourcePath);
            }
            String filename = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
            int dotIdx = filename.indexOf('.');
            String baseName = dotIdx < 0 ? filename : filename.substring(0, dotIdx);
            String extension = dotIdx < 0 ? "" : filename.substring(dotIdx);
            tempFile = File.createTempFile(baseName + "_", extension);

            try {
                if (tempFile.toPath().getFileSystem().supportedFileAttributeViews().contains("posix")) {
                    tempFileIsPosix = true;
                }
            } catch (Throwable e) {}

            byte[] buffer = new byte[1024];
            OutputStream os = new FileOutputStream(tempFile);
            try {
                for (int readBytes; (readBytes = inputSream.read(buffer)) != -1;) {
                    os.write(buffer, 0, readBytes);
                }
            } finally {
                os.close();
            }
            
            extractedFileConsumer.accept(tempFile);
        } catch (Throwable exc) {
        	Throwables.throwException(exc);
        } finally {
            if (tempFile != null) {
                boolean deleted = false;
                if (tempFileIsPosix) {
                    deleted = tempFile.delete();
                }
                if (!deleted) {
                    tempFile.deleteOnExit();
                }
            }
        }
    }
	
}
