/*
 * This file is part of ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2022 Luke Hutchison, Roberto Gentili
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
package io.github.toolfactory.jvm.function.catalog;


import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import io.github.toolfactory.jvm.Info;
import io.github.toolfactory.jvm.function.InitializeException;
import io.github.toolfactory.jvm.function.template.Supplier;
import io.github.toolfactory.jvm.util.ObjectProvider;
import io.github.toolfactory.jvm.util.Strings;
import io.github.toolfactory.narcissus.Narcissus;


@SuppressWarnings("all")
public interface ConsulterSupplier extends Supplier<MethodHandles.Lookup> {

	public static abstract class Abst implements ConsulterSupplier {
		protected MethodHandles.Lookup consulter;

		public Abst(Map<Object, Object> context) {
			this.consulter = MethodHandles.lookup();
		}

		@Override
		public MethodHandles.Lookup get() {
			return consulter;
		}
	}

	public static class ForJava7 extends Abst {

		public ForJava7(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			super(context);
			Field modes = MethodHandles.Lookup.class.getDeclaredField("allowedModes");
			modes.setAccessible(true);
			modes.setInt(consulter, -1);
		}

		public static class ForSemeru extends Abst {
			public static final int PACKAGE = 0x8;
			public static final int INTERNAL_PRIVILEGED = 0x80;
			public static final int FULL_ACCESS_MASK = Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED | PACKAGE;

			public ForSemeru(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
				super(context);
				Field modes = MethodHandles.Lookup.class.getDeclaredField("accessMode");
				modes.setAccessible(true);
				modes.setInt(consulter, INTERNAL_PRIVILEGED);
			}

		}

	}


	public static class ForJava9 extends Abst {

		public ForJava9(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			super(context);
		}

		public static class ForSemeru extends Abst {
			public static final int MODULE = 0x10;
			public static final int FULL_ACCESS_MASK =
					io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava7.ForSemeru.FULL_ACCESS_MASK | MODULE;

			public ForSemeru(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
				super(context);
				Field modes = MethodHandles.Lookup.class.getDeclaredField("accessMode");
				sun.misc.Unsafe unsafe = ObjectProvider.get(context).getOrBuildObject(UnsafeSupplier.class, context).get();
				Long allowedModesFieldMemoryOffset = unsafe.objectFieldOffset(modes);
				unsafe.putInt(consulter, allowedModesFieldMemoryOffset, io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava7.ForSemeru.INTERNAL_PRIVILEGED | MODULE);
			}

		}

	}


	public static interface ForJava14 extends ConsulterSupplier {

		public static class ForSemeru extends Abst {
			protected static final int MODULE = 0x10;
			private static final int FULL_ACCESS_MASK =
					io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava7.ForSemeru.FULL_ACCESS_MASK | MODULE;

			public ForSemeru(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
				super(context);
				Field modes = MethodHandles.Lookup.class.getDeclaredField("accessMode");
				sun.misc.Unsafe unsafe = ObjectProvider.get(context).getOrBuildObject(UnsafeSupplier.class, context).get();
				Long allowedModesFieldMemoryOffset = unsafe.objectFieldOffset(modes);
				unsafe.putInt(consulter, allowedModesFieldMemoryOffset, io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava7.ForSemeru.INTERNAL_PRIVILEGED);
			}

		}

	}


	public static class ForJava17 extends Abst {

		public ForJava17(Map<Object, Object> context) {
			super(context);
			sun.misc.Unsafe unsafe = ObjectProvider.get(context).getOrBuildObject(UnsafeSupplier.class, context).get();
			final long allowedModesFieldMemoryOffset = Info.Provider.getInfoInstance().is64Bit() ? 12L : 8L;
			unsafe.putInt(consulter, allowedModesFieldMemoryOffset, -1);
		}

		public static class ForSemeru extends Abst {
			public ForSemeru(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
				super(context);
				sun.misc.Unsafe unsafe = ObjectProvider.get(context).getOrBuildObject(UnsafeSupplier.class, context).get();
				unsafe.putInt(consulter, 20, -1);
			}

		}

	}


	public static interface Native extends ConsulterSupplier {

		public static abstract class Abst extends ConsulterSupplier.Abst {

			public Abst(Map<Object, Object> context) throws InitializeException {
				super(context);
				checkNativeEngine();
			}

			protected void checkNativeEngine() throws InitializeException {
				if (!Narcissus.libraryLoaded) {
					throw new InitializeException(
						Strings.compile(
							"Could not initialize the native engine {}",
							io.github.toolfactory.narcissus.Narcissus.class.getName()
						)
					);
				}
			}

		}

		public static class ForJava7 extends Abst implements Native {

			public ForJava7(Map<Object, Object> context) throws NoSuchFieldException, InitializeException {
				super(context);
				io.github.toolfactory.narcissus.Narcissus.setField(
					consulter,
					io.github.toolfactory.narcissus.Narcissus.findField(consulter.getClass(), "allowedModes"),
					-1
				);

			}

			public static class ForSemeru extends Native.Abst implements Native {

				public ForSemeru(Map<Object, Object> context) throws NoSuchFieldException, InitializeException {
					super(context);
					io.github.toolfactory.narcissus.Narcissus.setField(
						consulter,
						io.github.toolfactory.narcissus.Narcissus.findField(consulter.getClass(), "accessMode"),
						io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava7.ForSemeru.INTERNAL_PRIVILEGED
					);

				}
			}
		}

		public static interface ForJava9 extends Native, ConsulterSupplier {

			public static class ForSemeru extends Native.Abst implements Native.ForJava9 {

				public ForSemeru(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InitializeException {
					super(context);
					io.github.toolfactory.narcissus.Narcissus.setField(
						consulter,
						io.github.toolfactory.narcissus.Narcissus.findField(consulter.getClass(), "accessMode"),
						io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava7.ForSemeru.INTERNAL_PRIVILEGED |
						io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava9.ForSemeru.MODULE
					);
				}

			}
		}

		public static interface ForJava14 extends Native, ConsulterSupplier {

			public static class ForSemeru extends ConsulterSupplier.Native.ForJava7.ForSemeru implements Native.ForJava14 {

				public ForSemeru(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InitializeException {
					super(context);
				}
			}
		}

		public static interface ForJava17 extends Native, ConsulterSupplier {

			public static class ForSemeru extends Native.ForJava7 implements Native.ForJava17 {

				public ForSemeru(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InitializeException {
					super(context);
				}
			}
		}

	}


	public static interface Hybrid extends ConsulterSupplier {

		public static class ForJava17 extends Native.ForJava7 implements Hybrid {

			public ForJava17(Map<Object, Object> context) throws NoSuchFieldException, InitializeException {
				super(context);
			}

			public static class ForSemeru extends Native.ForJava17.ForSemeru implements Hybrid {

				public ForSemeru(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InitializeException {
					super(context);
				}
			}
		}
	}

}
