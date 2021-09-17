package org.burningwave.jvm;


import org.junit.jupiter.api.Test;


public class HybridDriverTest extends BaseTest {
	private static Driver driver;
	
	Driver getDriver() {
		if (driver == null) {
			try {
				driver = new HybridDriver();
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
