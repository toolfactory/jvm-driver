package io.github.toolfactory.jvm.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Test;

import io.github.toolfactory.jvm.util.Properties;


public class PropertiesTest {
	private static final String FILE_NAME = "properties-test.properties";
	private static final String PRIORITY_PROPERTY = "priority-of-this-configuration";

	//For JDK 7 testing
	public static void main(String[] args) throws Throwable {
		new PropertiesTest().executeTests();
	}

	void executeTests() throws Throwable {
		mergeTest();
	}


	@Test
	public void mergeTest() throws Throwable {
		File[] folders = new File[] {
			createConfiguration(PRIORITY_PROPERTY + "=1\nfirst=1a\nsecond=1a\n"),
			//Before version 10.0.0 a file with the same priority of another one replaced it
			createConfiguration(PRIORITY_PROPERTY + "=1\nthird=1b\n"),
			createConfiguration(PRIORITY_PROPERTY + "=2\nsecond=2\n"),
			//The files without priority have the lowest one
			createConfiguration("first=none\nfourth=none-a\n"),
			createConfiguration("fifth=none-b\n")
		};
		URL[] urls = new URL[folders.length];
		for (int i = 0; i < folders.length; i++) {
			urls[i] = folders[i].toURI().toURL();
		}
		URLClassLoader classLoader = new URLClassLoader(urls, null);
		try {
			java.util.Properties properties = Properties.loadFromResourcesAndMerge(
				FILE_NAME, PRIORITY_PROPERTY, Arrays.<ClassLoader>asList(classLoader)
			);
			assertEquals("1a", properties.getProperty("first"));
			assertEquals("2", properties.getProperty("second"));
			assertEquals("1b", properties.getProperty("third"));
			assertEquals("none-a", properties.getProperty("fourth"));
			assertEquals("none-b", properties.getProperty("fifth"));
			assertNull(properties.getProperty(PRIORITY_PROPERTY));
		} finally {
			classLoader.close();
			for (File folder : folders) {
				new File(folder, FILE_NAME).delete();
				folder.delete();
			}
		}
	}


	private File createConfiguration(String content) throws IOException {
		File folder = new File(System.getProperty("java.io.tmpdir"), "jvm-driver-properties-test-" + UUID.randomUUID());
		if (!folder.mkdirs()) {
			throw new IOException("Could not create " + folder);
		}
		try (OutputStream outputStream = new FileOutputStream(new File(folder, FILE_NAME))) {
			outputStream.write(content.getBytes("ISO-8859-1"));
		}
		return folder;
	}

}
