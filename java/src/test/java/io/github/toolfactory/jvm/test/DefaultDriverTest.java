package io.github.toolfactory.jvm.test;


import org.junit.Test;

import io.github.toolfactory.util.Reflection;


public class DefaultDriverTest extends BaseTest {

	public static void main(String[] args) {
		new DefaultDriverTest().getAndSetDirectVolatileTestOne();
	}

	@Override
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


	@Override
	@Test
	public void getConsulterTestOne() {
		super.getConsulterTestOne();
	}


	@Override
	@Test
	public void getAndSetDirectVolatileTestOne() {
		super.getAndSetDirectVolatileTestOne();
	}


	@Override
	@Test
	public void getDeclaredFieldsTestOne() {
		super.getDeclaredFieldsTestOne();
	}


	@Override
	@Test
	public void getDeclaredMethodsTestOne() {
		super.getDeclaredMethodsTestOne();
	}


	@Override
	@Test
	public void getDeclaredConstructorsTestOne() {
		super.getDeclaredConstructorsTestOne();
	}


	@Override
	@Test
	public void allocateInstanceTestOne() {
		super.allocateInstanceTestOne();
	}


	@Override
	@Test
	public void setAccessibleTestOne() {
		super.setAccessibleTestOne();
	}


	@Override
	@Test
	public void setInvokeTestOne() {
		super.setInvokeTestOne();
	}


	@Override
	@Test
	public void newInstanceTestOne() {
		super.newInstanceTestOne();
	}


	@Override
	@Test
	public void retrieveLoadedClassesTestOne() {
		super.retrieveLoadedClassesTestOne();
	}


	@Override
	@Test
	public void retrieveLoadedPackagesTestOne() {
		super.retrieveLoadedPackagesTestOne();
	}


	@Override
	@Test
	public void retrieveResourcesTestOne() {
		super.retrieveResourcesTestOne();
	}

	@Override
	@Test
	public void getClassByNameTestOne() {
		super.getClassByNameTestOne();
	}


}