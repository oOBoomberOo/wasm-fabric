package net.boomber.wasm.exception;

import net.minecraft.util.Identifier;

public class UnknownModuleException extends RuntimeException {
	private static final long serialVersionUID = 5559547388246310506L;

	public UnknownModuleException(Identifier id) {
		super("Unknown module: " + id);
	}
}
