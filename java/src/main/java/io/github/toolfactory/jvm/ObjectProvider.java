package io.github.toolfactory.jvm;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeSet;

import io.github.toolfactory.jvm.function.catalog.ThrowExceptionFunction;



@SuppressWarnings("all")
public class ObjectProvider {
	private final String classSuffix;
	private final static String CLASS_NAME;
	final Integer[] registeredVersions;
	
	static {
		CLASS_NAME = ObjectProvider.class.getName();
	}
	
	public ObjectProvider(String classSuffix, int... versions) {
		this.classSuffix = classSuffix;
		int jVMVersion = Info.getInstance().getVersion();
		TreeSet<Integer> registeredVersions = new TreeSet<>();
		for (int i = 0; i < versions.length; i++) {
			if (jVMVersion >= versions[i]) {
				registeredVersions.add(versions[i]);
			}
		}
		this.registeredVersions = registeredVersions.descendingSet().toArray(new Integer[registeredVersions.size()]);
	}

	
	public <F> F getOrBuildObject(Class<? super F> clazz, Map<Object, Object> context) {
		String className = clazz.getName();
		Collection<String> searchedClasses = new LinkedHashSet<>();
		F function = getObject(clazz, context);		
		context.put(CLASS_NAME, this);
		for (int version : registeredVersions) {
			String clsName = className + "$" +  classSuffix + version;
			try {
				Class<?> effectiveClass = null;
				try {
					effectiveClass = Class.forName(clsName);
				} catch (ClassNotFoundException exc) {
					searchedClasses.add(clsName);
					clsName = className + classSuffix + version;
					effectiveClass = Class.forName(clsName);
				}
				function = (F) effectiveClass.getDeclaredConstructor(Map.class).newInstance(context);
				context.put(className, function);
				return function;
			} catch (ClassNotFoundException exc) {
				searchedClasses.add(clsName);
			} catch (Throwable exc) {
				throw new BuildingException("Unable to build the related object of " + clazz.getName(), exc);
			}
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null && !superClass.equals(Object.class)) {
			try {
				return getOrBuildObject(superClass, context);
			} catch (BuildingException exc) {
				throw new BuildingException(
					"Unable to build the related object of " + clazz.getName() + ": " + String.join(", ", searchedClasses) + " have been searched without success",
					exc
				);
			}
		} else {
			throw new BuildingException(
				"Unable to build the related object of " + clazz.getName() + ": " + String.join(", ", searchedClasses) + " have been searched without success"
			);
		}
	}


	public static <F> F getObject(Class<? super F> clazz, Map<Object, Object> context) {
		F objectFound = (F) context.get(clazz.getName());
		if (objectFound != null) {
			return objectFound;
		} else {
			for (Object function : context.values()) {
				if (clazz.isAssignableFrom(function.getClass())) {
					return (F)function;
				}
			}
		}
		return null;
	}
	
	public static ObjectProvider get(Map<Object, Object> context) {
		return (ObjectProvider)context.get(CLASS_NAME);
	}
	
	
	public static class BuildingException extends RuntimeException {

	    public BuildingException(String message, Throwable cause) {
	        super(message, cause);
	    }
	    
	    public BuildingException(String message) {
	        super(message);
	    }

	}
}