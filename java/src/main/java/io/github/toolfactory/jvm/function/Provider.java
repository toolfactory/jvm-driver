package io.github.toolfactory.jvm.function;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeSet;

import io.github.toolfactory.jvm.Info;
import io.github.toolfactory.jvm.function.catalog.ThrowExceptionFunction;



@SuppressWarnings("all")
public class Provider {
	private final String classSuffix;
	private final static String CLASS_NAME;
	final Integer[] registeredVersions;
	
	static {
		CLASS_NAME = Provider.class.getName();
	}
	
	public Provider(String classSuffix, int... versions) {
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

	
	public <F> F getOrBuildFunction(Class<? super F> functionClass, Map<Object, Object> context) {
		String className = functionClass.getName();
		Collection<String> searchedClasses = new LinkedHashSet<>();
		F function = getFunction(functionClass, context);		
		context.put(CLASS_NAME, this);
		for (int version : registeredVersions) {
			String clsName = className + "$" +  classSuffix + version;
			try {
				Class<?> functionEffectiveClass = null;
				try {
					functionEffectiveClass = Class.forName(clsName);
				} catch (ClassNotFoundException exc) {
					searchedClasses.add(clsName);
					clsName = className + classSuffix + version;
					functionEffectiveClass = Class.forName(clsName);
				}
				function = (F) functionEffectiveClass.getDeclaredConstructor(Map.class).newInstance(context);
				context.put(className, function);
				return function;
			} catch (ClassNotFoundException exc) {
				searchedClasses.add(clsName);
			} catch (Throwable exc) {
				throw new FunctionBuildingException("Unable to build the related function of " + functionClass.getName(), exc);
			}
		}
		Class<?> functionSuperClass = functionClass.getSuperclass();
		if (functionSuperClass != null && !functionSuperClass.equals(Object.class)) {
			try {
				return getOrBuildFunction(functionSuperClass, context);
			} catch (FunctionBuildingException exc) {
				throw new FunctionBuildingException(
					"Unable to build the related function of " + functionClass.getName() + ": " + String.join(", ", searchedClasses) + " have been searched without success",
					exc
				);
			}
		} else {
			throw new FunctionBuildingException(
				"Unable to build the related function of " + functionClass.getName() + ": " + String.join(", ", searchedClasses) + " have been searched without success"
			);
		}
	}


	public static <F> F getFunction(Class<? super F> functionClass, Map<Object, Object> context) {
		F functionFound = (F) context.get(functionClass.getName());
		if (functionFound != null) {
			return functionFound;
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
	
	
	public static class FunctionBuildingException extends RuntimeException {

	    public FunctionBuildingException(String message, Throwable cause) {
	        super(message, cause);
	    }
	    
	    public FunctionBuildingException(String message) {
	        super(message);
	    }

	}
}