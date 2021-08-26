package org.burningwave.core;

import org.burningwave.jvm.DefaultDriver;
import org.burningwave.jvm.Driver;
import org.junit.jupiter.api.Test;
import java.lang.invoke.MethodHandle;

@SuppressWarnings("unused")
public class DriverTest{

	
	@Test
	public void createAndCloseTest() {
		try {
			Driver driver = new DefaultDriver();
			MethodHandle methodHandle =
				driver.getFieldValue(driver, driver.getDeclaredField(driver.getClass(), "constructorInvoker"));
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
	}
	
}
