package io.github.toolfactory.jvm;

import org.junit.jupiter.api.Test;

public class NativeDriverTest extends BaseTest {
	private static Driver driver;
	
	public static void main(String[] args) {
		new NativeDriverTest().getAndSetDirectVolatileTestOne();
	}
	
	Driver getDriver() {
		if (driver == null) {
			try {
				driver = new NativeDriver();
			} catch (Throwable exc) {
				exc.printStackTrace();
				return getDriver().throwException(exc);
			}
		}
		return driver;
	}
	
	@Test
	public void getAndSetDirectVolatileTestOne() {
		super.getAndSetDirectVolatileTestOne();
	}

}
