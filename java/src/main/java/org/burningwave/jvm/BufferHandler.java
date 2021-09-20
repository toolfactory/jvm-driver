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


import java.nio.Buffer;
import java.nio.ByteBuffer;


@SuppressWarnings("unchecked")
class BufferHandler {
	
	static ByteBuffer shareContent(ByteBuffer byteBuffer) {
		ByteBuffer duplicated = duplicate(byteBuffer);
		if (position(byteBuffer) > 0) {
			flip(duplicated);
		}		
		return duplicated;
	}
	
	static <T extends Buffer> T flip(T buffer) {
		return (T)((Buffer)buffer).flip();
	}
	
	static <T extends Buffer> int position(T buffer) {
		return ((Buffer)buffer).position();
	}
	
	static ByteBuffer duplicate(ByteBuffer buffer) {
		return buffer.duplicate();
	}
	
	static <T extends Buffer> int limit(T buffer) {
		return ((Buffer)buffer).limit();
	}
	
	static byte[] toByteArray(ByteBuffer byteBuffer) {
    	byteBuffer = shareContent(byteBuffer);
    	byte[] result = new byte[limit(byteBuffer)];
    	byteBuffer.get(result, 0, result.length);
        return result;
	}
	
}
