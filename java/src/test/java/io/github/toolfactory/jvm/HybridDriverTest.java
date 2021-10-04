package io.github.toolfactory.jvm;


import org.junit.Test;

import io.github.toolfactory.util.Reflection;


public class HybridDriverTest extends BaseTest {
	
	public static void main(String[] args) {
		new HybridDriverTest().getAndSetDirectVolatileTestOne();
	}
	
	Reflection getReflection() {
		if (reflection == null) {
			try {
				reflection = Reflection.Factory.getNewWithHybridDriver();
			} catch (Throwable exc) {
				exc.printStackTrace();
				throw new RuntimeException(exc);
			}
		}
		return reflection;
	}
	
	@Test
	public void getAndSetDirectVolatileTestOne() {
		super.getAndSetDirectVolatileTestOne();
	}
	
}
