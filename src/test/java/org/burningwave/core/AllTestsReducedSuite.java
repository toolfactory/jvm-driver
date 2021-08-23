package org.burningwave.core;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@SuppressWarnings("unused")
@RunWith(JUnitPlatform.class)
//@SelectPackages("org.burningwave.core")
@SelectClasses({
	DriverTest.class
})
//@ExcludeTags("Heavy")
public class AllTestsReducedSuite {

}
