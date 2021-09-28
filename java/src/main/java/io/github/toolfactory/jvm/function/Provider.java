package io.github.toolfactory.jvm.function;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeSet;

import io.github.toolfactory.jvm.Info;



@SuppressWarnings("all")
public class Provider {
	private final String innerClassSuffix;
	private final static String CLASS_NAME;
	final Integer[] registeredVersions;
	
	static {
		CLASS_NAME = Provider.class.getName();
	}
	
	public Provider(String innerClassSuffix, int... versions) {
		this.innerClassSuffix = innerClassSuffix;
		int jVMVersion = Info.getInstance().getVersion();
		TreeSet<Integer> registeredVersions = new TreeSet<>();
		for (int i = 0; i < versions.length; i++) {
			if (jVMVersion >= versions[i]) {
				registeredVersions.add(versions[i]);
			}
		}
		this.registeredVersions = registeredVersions.descendingSet().toArray(new Integer[registeredVersions.size()]);
	}

	
	public <F> F getFunctionAdapter(Class<? super F> cls, Map<Object, Object> context) {
		String className = cls.getName();
		Collection<String> searchedClasses = new LinkedHashSet<>();
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
				ThrowExceptionFunction throwingFunction = null;
				for (Object function : context.values()) {
					if (ThrowExceptionFunction.class.isAssignableFrom(function.getClass())) {
						throwingFunction = (ThrowExceptionFunction)function;
					}
				}
				
				return throwingFunction.apply(exc);
			}
		}
		cls = cls.getSuperclass();
		return cls != null && !cls.equals(Object.class)? getFunctionAdapter(cls, context) : null;
	}
	
	public static Provider get(Map<Object, Object> context) {
		return (Provider)context.get(CLASS_NAME);
	}
	
}