package io.github.toolfactory.jvm;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages("org.toolfactory.core")
public class AllTestsSuite {

}
