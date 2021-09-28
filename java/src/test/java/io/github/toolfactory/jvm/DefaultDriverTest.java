package io.github.toolfactory.jvm;


import org.junit.jupiter.api.Test;


@SuppressWarnings("unused")
public class DefaultDriverTest extends BaseTest {
	private static Driver driver;
	
	public static void main(String[] args) {
		new DefaultDriverTest().getAndSetDirectVolatileTestOne();
	}
	
	Driver getDriver() {
		if (driver == null) {
			try {
				driver = new DefaultDriver();
			} catch (Throwable exc) {
				exc.printStackTrace();
				throw new RuntimeException(exc);
			}
		}
		return driver;
	}	
	@Test
	public void getAndSetDirectVolatileTestOne() {
		super.getAndSetDirectVolatileTestOne();
	}
	
}