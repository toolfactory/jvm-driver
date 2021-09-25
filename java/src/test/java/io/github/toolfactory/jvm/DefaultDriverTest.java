package io.github.toolfactory.jvm;


import java.lang.invoke.MethodHandle;

import org.junit.jupiter.api.Test;

import io.github.toolfactory.jvm.DefaultDriver;
import io.github.toolfactory.jvm.Driver;
import io.github.toolfactory.jvm.Throwables;


@SuppressWarnings("unused")
public class DefaultDriverTest extends BaseTest {
	private static Driver driver;
	
	Driver getDriver() {
		if (driver == null) {
			try {
				driver = new DefaultDriver();
			} catch (Throwable exc) {
				exc.printStackTrace();
				return Throwables.getInstance().throwException(exc);
			}
		}
		return driver;
	}	
	@Test
	public void getAndSetDirectVolatileTestOne() {
		super.getAndSetDirectVolatileTestOne();
	}
	
}