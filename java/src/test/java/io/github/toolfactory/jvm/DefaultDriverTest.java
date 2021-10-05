package io.github.toolfactory.jvm;


import org.junit.Test;

import io.github.toolfactory.util.Reflection;


public class DefaultDriverTest extends BaseTest {
	
	public static void main(String[] args) {
		new DefaultDriverTest().getAndSetDirectVolatileTestOne();
	}
	
	Reflection getReflection() {
		if (reflection == null) {
			try {
				reflection = Reflection.Factory.getNewWithDefaultDriver();
			} catch (Throwable exc) {
				exc.printStackTrace();
				throw new RuntimeException(exc);
			}
		}
		return reflection;
	}
	
	
	@Test
	public void getConsulterTestOne() {
		super.getConsulterTestOne();
	}
	
	
	@Test
	public void getAndSetDirectVolatileTestOne() {
		super.getAndSetDirectVolatileTestOne();
	}
	
	
	@Test
	public void getDeclaredFieldsTestOne() {
		super.getDeclaredFieldsTestOne();
	}
	
	
	@Test
	public void getDeclaredMethodsTestOne() {
		super.getDeclaredMethodsTestOne();
	}
	
	
	@Test
	public void getDeclaredConstructorsTestOne() {
		super.getDeclaredConstructorsTestOne();
	}
	
	
	@Test
	public void allocateInstanceTestOne() {
		super.allocateInstanceTestOne();
	}
	
	
	@Test
	public void setAccessibleTestOne() {
		super.setAccessibleTestOne();
	}
	
	
	@Test
	public void setInvokeTestOne() {
		super.setInvokeTestOne();
	}
	
	
	@Test
	public void retrieveLoadedClassesTestOne() {
		super.retrieveLoadedClassesTestOne();
	}
	
	
	@Test
	public void retrieveLoadedPackagesTestOne() {
		super.retrieveLoadedPackagesTestOne();
	}
	
}