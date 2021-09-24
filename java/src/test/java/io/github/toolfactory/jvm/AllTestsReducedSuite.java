package io.github.toolfactory.jvm;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@SuppressWarnings("unused")
@RunWith(JUnitPlatform.class)
//@SelectPackages("org.toolfactory.core")
@SelectClasses({
	DefaultDriverTest.class
})
//@ExcludeTags("Heavy")
public class AllTestsReducedSuite {

}
