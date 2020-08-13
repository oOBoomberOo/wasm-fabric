package net.boomber.wasm.module;

import org.wasmer.Memory;

/**
 * Pointer interface to WASM's memory
 */
public abstract class Pointer<T> {
	public final int address;
	public final int size;
	
	public Pointer(int address, int size) {
		this.address = address;
		this.size = size;
	}
	
	public Pointer(Pointer<?> other) {
		this(other.address, other.size);
	}
	
	/**
	 * Get underlying value from memory
	 */
	public abstract T get(Memory memory);
	
	public String toString() {
		return "{ ptr: " + address + ", len: " + size + " }";
	}
}
