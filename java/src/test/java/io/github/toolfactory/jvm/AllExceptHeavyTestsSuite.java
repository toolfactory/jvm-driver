package io.github.toolfactory.jvm;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@SuppressWarnings("unused")
@RunWith(JUnitPlatform.class)
//@SelectPackages("org.toolfactory.core")
@SelectClasses({
    DefaultDriverTest.class,
    HybridDriverTest.class,
    NativeDriverTest.class
})
@ExcludeTags("Heavy")
public class AllExceptHeavyTestsSuite {

}
