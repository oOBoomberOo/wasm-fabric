package net.boomber.wasm.exception;

import net.minecraft.util.Identifier;

public class DataStorageNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 8192094158192156346L;

	public DataStorageNotFoundException(Identifier id) {
		super("Cannot find data storage " + id);
	}
}
