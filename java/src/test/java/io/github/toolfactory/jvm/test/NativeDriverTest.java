package io.github.toolfactory.jvm.test;


import org.junit.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import io.github.toolfactory.util.Reflection;


public class NativeDriverTest extends BaseTest {

	//For JDK 7 testing
	public static void main(String[] args) {
		new NativeDriverTest().executeTests();
	}

	@Override
	Reflection getReflection() {
		if (reflection == null) {
			try {
				reflection = Reflection.Factory.getNewWithNativeDriver();
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
	public void invokeTestOne() {
		super.invokeTestOne();
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
	public void retrieveResourcesAsStreamsTestOne() {
		super.retrieveResourcesAsStreamsTestOne();
	}

	@Override
	@Test
	public void getClassByNameTestOne() {
		super.getClassByNameTestOne();
	}

	@Override
	@Test
	public void convertToBuiltinClassLoader() {
		super.convertToBuiltinClassLoader();
	}

	@Override
	@Test
	@EnabledForJreRange(max = JRE.JAVA_19)
	public void stopThread() {
		super.stopThread();
	}
}
