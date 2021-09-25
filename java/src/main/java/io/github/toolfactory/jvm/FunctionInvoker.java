package io.github.toolfactory.jvm;

abstract class FunctionWrapper<F, I, O> {
	
	F function;
	
	FunctionWrapper(F function) {
		this.function = function;
	}
	
	abstract O apply(I input);
}
