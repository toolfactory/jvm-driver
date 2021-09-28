package io.github.toolfactory.jvm.function;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeSet;

import io.github.toolfactory.jvm.Info;
import io.github.toolfactory.jvm.function.catalog.ThrowExceptionFunction;



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

	
	public <F> F getFunctionAdapter(Class<? super F> functionClass, Map<Object, Object> context) {
		String className = functionClass.getName();
		Collection<String> searchedClasses = new LinkedHashSet<>();
		F functionAdapter = find(functionClass, context);		
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
				ThrowExceptionFunction throwingFunction = find(ThrowExceptionFunction.class, context);
				if (throwingFunction != null) {
					throwingFunction.apply(exc);
				} else {
					throw new RuntimeException(exc);
				}
				
			}
		}
		functionClass = functionClass.getSuperclass();
		return functionClass != null && !functionClass.equals(Object.class)? getFunctionAdapter(functionClass, context) : null;
	}


	public static <F> F find(Class<? super F> functionClass, Map<Object, Object> context) {
		F functionAdapter = (F) context.get(functionClass.getName());
		if (functionAdapter != null) {
			return functionAdapter;
		} else {
			for (Object function : context.values()) {
				if (functionClass.isAssignableFrom(function.getClass())) {
					return (F)function;
				}
			}
		}
		return null;
	}
	
	public static Provider get(Map<Object, Object> context) {
		return (Provider)context.get(CLASS_NAME);
	}
	
}