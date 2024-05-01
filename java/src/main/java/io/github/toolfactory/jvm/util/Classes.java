/*
 * This file is part of ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Luke Hutchison, Roberto Gentili
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
package io.github.toolfactory.jvm.util;

import java.nio.ByteBuffer;

import io.github.toolfactory.jvm.function.template.Function;


public class Classes {

	public static boolean isAssignableFrom(Class<?> clsOne, Class<?> clsTwo) {
		return getClassOrWrapper(clsOne).isAssignableFrom(getClassOrWrapper(clsTwo));
	}

	public static Class<?> getClassOrWrapper(Class<?> cls) {
		if (cls.isPrimitive()) {
			if (cls == short.class) {
				return Short.class;
			} else if (cls == int.class) {
				return Integer.class;
			} else if (cls == long.class) {
				return Long.class;
			} else if (cls == float.class) {
				return Float.class;
			} else if (cls == double.class) {
				return Double.class;
			} else if (cls == boolean.class) {
				return Boolean.class;
			} else if (cls == byte.class) {
				return Byte.class;
			} else if (cls == char.class) {
				return Character.class;
			}
		}
		return cls;
	}


	public static class RawInfo {
		int modifiers;
		String name;
		String superClassName;
		String[] interfaceNames;

		private RawInfo(int modifiers, String rawName, String superClassRawName, String[] implementedInterfaceRawNames) {
			this.modifiers = modifiers;
			this.name = rawName;
			this.superClassName = superClassRawName;
			this.interfaceNames = implementedInterfaceRawNames;
		}

		public int getModifiers() {
			return modifiers;
		}

		public String getName() {
			return name;
		}

		public String getSuperClassName() {
			return superClassName;
		}

		public String[] getInterfaceNames() {
			return interfaceNames;
		}


	}


	public static class File {
		private static class Symbol{
			static class Tag {
				static final byte UTF8 = 1;
				static final byte INTEGER = 3;
				static final byte FLOAT = 4;
				static final byte LONG = 5;
				static final byte DOUBLE = 6;
				static final byte CLASS = 7;
				static final byte STRING = 8;
				static final byte FIELD_REF = 9;
				static final byte METHOD_REF = 10;
				static final byte INTERFACE_METHOD_REF = 11;
				static final byte NAME_AND_TYPE = 12;
				static final byte METHOD_HANDLE = 15;
				static final byte METHOD_TYPE = 16;
				static final byte DYNAMIC = 17;
				static final byte INVOKE_DYNAMIC = 18;
				static final byte MODULE = 19;
				static final byte PACKAGE = 20;

		    }
		}

		public static class Reader {

			public static RawInfo retrieveInfo(
				final byte[] classFileBuffer
			) {
				return retrieveInfo(new Function<Integer, Byte>() {
					@Override
					public Byte apply(Integer index) {
						return classFileBuffer[index];
					}
				});
			}

			public static RawInfo retrieveInfo(
				final ByteBuffer classFileBuffer
			) {
				return retrieveInfo(new Function<Integer, Byte>() {
					@Override
					public Byte apply(Integer index) {
						return classFileBuffer.get(index);
					}
				});
			}

			private static RawInfo retrieveInfo(
				final Function<Integer, Byte> byteSupplier
			) {
				int classFileOffset = 0;
				int constantPoolCount = readUnsignedShort(byteSupplier, classFileOffset + 8);
				int[] cpInfoOffsets = new int[constantPoolCount];
				String[] constantUtf8Values = new String[constantPoolCount];
				int currentCpInfoIndex = 1;
				int currentCpInfoOffset = classFileOffset + 10;
				int currentMaxStringLength = 0;
				while (currentCpInfoIndex < constantPoolCount) {
					cpInfoOffsets[currentCpInfoIndex++] = currentCpInfoOffset + 1;
					int cpInfoSize;
					byte currentCpInfoValue = byteSupplier.apply(currentCpInfoOffset);
					if (currentCpInfoValue == Symbol.Tag.INTEGER ||
						currentCpInfoValue == Symbol.Tag.FLOAT ||
						currentCpInfoValue == Symbol.Tag.FIELD_REF ||
						currentCpInfoValue == Symbol.Tag.METHOD_REF ||
						currentCpInfoValue == Symbol.Tag.INTERFACE_METHOD_REF ||
						currentCpInfoValue == Symbol.Tag.NAME_AND_TYPE ||
						currentCpInfoValue == Symbol.Tag.DYNAMIC ||
						currentCpInfoValue == Symbol.Tag.INVOKE_DYNAMIC
					) {
						cpInfoSize = 5;
					} else if (currentCpInfoValue == Symbol.Tag.LONG ||
						currentCpInfoValue == Symbol.Tag.DOUBLE
					) {
						cpInfoSize = 9;
						currentCpInfoIndex++;
					} else if (currentCpInfoValue == Symbol.Tag.UTF8) {
						cpInfoSize = 3 + readUnsignedShort(byteSupplier, currentCpInfoOffset + 1);
						if (cpInfoSize > currentMaxStringLength) {
							currentMaxStringLength = cpInfoSize;
						}
					} else if (currentCpInfoValue == Symbol.Tag.METHOD_HANDLE) {
						cpInfoSize = 4;
					} else if (currentCpInfoValue == Symbol.Tag.CLASS ||
						currentCpInfoValue == Symbol.Tag.STRING ||
						currentCpInfoValue == Symbol.Tag.METHOD_TYPE ||
						currentCpInfoValue == Symbol.Tag.MODULE ||
						currentCpInfoValue == Symbol.Tag.PACKAGE
					) {
						cpInfoSize = 3;
					} else {
						throw new IllegalArgumentException();
					}
					currentCpInfoOffset += cpInfoSize;
				}
				int maxStringLength = currentMaxStringLength;
				int header = currentCpInfoOffset;
				int modifiers = readUnsignedShort(byteSupplier, header);
				if ((modifiers & 0x8000) == 0) {
					return new RawInfo(
							modifiers,
						readUTF8(
							byteSupplier,
							cpInfoOffsets[readUnsignedShort(byteSupplier, header + 2)],
							new char[maxStringLength],
							constantUtf8Values,
							cpInfoOffsets
						),
						readUTF8(
							byteSupplier,
							cpInfoOffsets[readUnsignedShort(byteSupplier, header + 4)],
							new char[maxStringLength],
							constantUtf8Values,
							cpInfoOffsets
						),
						getInterfaceNames(byteSupplier, header, maxStringLength, constantUtf8Values, cpInfoOffsets)
					);
				}
				return null;

			}

			private static String[] getInterfaceNames(
				Function<Integer, Byte> byteSupplier,
				int header,
				int maxStringLength,
				String[] constantUtf8Values,
				int[] cpInfoOffsets
			) {
				int currentOffset = header + 6;
				int interfacesCount = readUnsignedShort(byteSupplier, currentOffset);
				String[] interfaces = new String[interfacesCount];
				if (interfacesCount > 0) {
					char[] charBuffer = new char[maxStringLength];
					for (int i = 0; i < interfacesCount; ++i) {
						currentOffset += 2;
						interfaces[i] = readUTF8(byteSupplier, cpInfoOffsets[readUnsignedShort(byteSupplier, currentOffset)],
								charBuffer, constantUtf8Values, cpInfoOffsets);
					}
				}
				return interfaces;
			}

			private static String readUTF8(
				Function<Integer, Byte> byteSupplier,
				final int offset,
				final char[] charBuffer,
				String[] constantUtf8Values,
				int[] cpInfoOffsets
			) {
				int constantPoolEntryIndex = readUnsignedShort(byteSupplier, offset);
				if (offset == 0 || constantPoolEntryIndex == 0) {
					return null;
				}
				return readUtf(byteSupplier, constantPoolEntryIndex, charBuffer, constantUtf8Values, cpInfoOffsets);
			}

			private static String readUtf(
				Function<Integer, Byte> byteSupplier,
				final int constantPoolEntryIndex,
				final char[] charBuffer,
				String[] constantUtf8Values,
				int[] cpInfoOffsets
			) {
				String value = constantUtf8Values[constantPoolEntryIndex];
				if (value != null) {
					return value;
				}
				int cpInfoOffset = cpInfoOffsets[constantPoolEntryIndex];
				return constantUtf8Values[constantPoolEntryIndex] = readUtf(byteSupplier, cpInfoOffset + 2, readUnsignedShort(byteSupplier, cpInfoOffset),
						charBuffer);
			}

			private static int readUnsignedShort(
				Function<Integer, Byte> byteSupplier,
				final int offset
			) {
				return ((byteSupplier.apply(offset) & 0xFF) << 8) | (byteSupplier.apply(offset + 1) & 0xFF);
			}

			private static String readUtf(Function<Integer, Byte> byteSupplier, final int utfOffset, final int utfLength, final char[] charBuffer) {
				int currentOffset = utfOffset;
				int endOffset = currentOffset + utfLength;
				int strLength = 0;
				while (currentOffset < endOffset) {
					int currentByte = byteSupplier.apply(currentOffset++);
					if ((currentByte & 0x80) == 0) {
						charBuffer[strLength++] = (char) (currentByte & 0x7F);
					} else if ((currentByte & 0xE0) == 0xC0) {
						charBuffer[strLength++] = (char) (((currentByte & 0x1F) << 6) + (byteSupplier.apply(currentOffset++) & 0x3F));
					} else {
						charBuffer[strLength++] = (char) (((currentByte & 0xF) << 12)
								+ ((byteSupplier.apply(currentOffset++) & 0x3F) << 6) + (byteSupplier.apply(currentOffset++) & 0x3F));
					}
				}
				return new String(charBuffer, 0, strLength);
			}
		}
	}
}
