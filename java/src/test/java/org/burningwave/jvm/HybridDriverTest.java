package org.burningwave.jvm;

import java.lang.invoke.MethodHandle;

import org.burningwave.jvm.Driver;
import org.burningwave.jvm.HybridDriver;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unused")
public class HybridDriverTest{

	
	@Test
	public void getDeclaredField_getFieldValueTest() {
		try {
			Driver driver = new HybridDriver();
			MethodHandle methodHandle =
				driver.getFieldValue(driver, driver.getDeclaredField(driver.getClass().getSuperclass(), "constructorInvoker"));
		} catch (Throwable exc) {
			exc.printStackTrace();
			Throwables.throwException(exc);
		}
	}
	
}
