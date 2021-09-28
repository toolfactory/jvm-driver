package io.github.toolfactory.jvm;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeSet;



@SuppressWarnings("unchecked")
class FunctionProvider {
	private final String innerClassSuffix;
	private final static String CLASS_NAME;
	final Integer[] registeredVersions;
	
	static {
		CLASS_NAME = FunctionProvider.class.getName();
	}
	
	FunctionProvider(String innerClassSuffix, int... versions) {
		this.innerClassSuffix = innerClassSuffix;
		int jVMVersion = JVMInfo.getInstance().getVersion();
		TreeSet<Integer> registeredVersions = new TreeSet<>();
		for (int i = 0; i < versions.length; i++) {
			if (jVMVersion >= versions[i]) {
				registeredVersions.add(versions[i]);
			}
		}
		this.registeredVersions = registeredVersions.descendingSet().toArray(new Integer[registeredVersions.size()]);
	}

	
	<F> F getFunctionAdapter(Class<? super F> cls, Map<Object, Object> context) {
		String className = cls.getName();
		Collection<String> searchedClasses = new LinkedHashSet<>();
		try {
			F functionAdapter = (F) context.get(className);
			if (functionAdapter != null) {
				return functionAdapter;
			} else {
				for (Object function : context.values()) {
					if (cls.isAssignableFrom(function.getClass())) {
						return (F)function;
					}
				}
			}
			context.put(CLASS_NAME, this);
			for (int version : registeredVersions) {
				String clsName = className + "$" +  innerClassSuffix + version;
				try {
					functionAdapter = (F) Class.forName(clsName).getDeclaredConstructor(Map.class).newInstance(context);
					context.put(className, functionAdapter);
					return functionAdapter;
				} catch (ClassNotFoundException exc) {
					searchedClasses.add(clsName);
				} catch (Throwable exc) {
					return Throwables.getInstance().throwException(exc);
				}
			}
		} finally {
			context.remove(CLASS_NAME);
		}
		cls = cls.getSuperclass();
		return cls != null && !cls.equals(Object.class)? getFunctionAdapter(cls, context) : null;
	}
	
	static FunctionProvider get(Map<Object, Object> context) {
		return (FunctionProvider)context.get(CLASS_NAME);
	}
	
}