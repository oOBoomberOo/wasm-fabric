package net.boomber.wasm.module;

public class FunctionNotFoundException extends Exception {
	private static final long serialVersionUID = -7534438282401610159L;

	public FunctionNotFoundException(String name) {
		super("Cannot find function called `" + name + "` in the current WASM module.");
	}
}
