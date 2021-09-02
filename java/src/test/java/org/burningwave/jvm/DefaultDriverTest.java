package org.burningwave.jvm;


import java.lang.invoke.MethodHandle;

import org.junit.jupiter.api.Test;


@SuppressWarnings("unused")
public class DefaultDriverTest{

	
	@Test
	public void getDeclaredField_getFieldValueTest() {
		try {
			Driver driver = new DefaultDriver();
			MethodHandle methodHandle =
				driver.getFieldValue(driver, driver.getDeclaredField(driver.getClass(), "constructorInvoker"));
		} catch (Throwable exc) {
			exc.printStackTrace();
			Throwables.throwException(exc);
		}
	}
	
}