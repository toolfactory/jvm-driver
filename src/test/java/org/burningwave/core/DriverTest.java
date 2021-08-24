package org.burningwave.core;

import org.burningwave.jvm.DefaultDriver;
import org.burningwave.jvm.Driver;
import org.junit.jupiter.api.Test;

public class DriverTest{

	
	@Test
	public void createAndCloseTest() {
		try {
			Driver driver = new DefaultDriver();
			sun.misc.Unsafe unsafe =
				driver.getFieldValue(driver, driver.getDeclaredField(driver.getClass(), "unsafe"));
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
	}
	
}
