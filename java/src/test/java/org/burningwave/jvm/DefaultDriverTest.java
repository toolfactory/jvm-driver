package org.burningwave.jvm;


import java.lang.invoke.MethodHandle;

import org.junit.jupiter.api.Test;


@SuppressWarnings("unused")
public class DefaultDriverTest extends BaseTest {
	private static Driver driver;
	
	Driver getDriver() {
		if (driver == null) {
			try {
				driver = new DefaultDriver();
			} catch (Throwable exc) {
				exc.printStackTrace();
				return Throwables.throwException(exc);
			}
		}
		return driver;
	}	
	@Test
	public void getAndSetDirectVolatileTestOne() {
		super.getAndSetDirectVolatileTestOne();
	}
	
}