package io.github.toolfactory.jvm;

abstract class BiFunctionWrapper<F, I, J, O> {
	
	F function;
	
	BiFunctionWrapper(F function) {
		this.function = function;
	}
	
	abstract O apply(I inputOne, J inputTwo);
}
