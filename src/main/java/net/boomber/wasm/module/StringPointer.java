package net.boomber.wasm.module;

import org.wasmer.Memory;

public class StringPointer extends Pointer<String> {
	
	public StringPointer(int address, int size) {
		super(address, size);
	}

	public StringPointer(Pointer<?> ptr) {
		super(ptr);
	}

	@Override
	public String get(Memory memory) {
		SlicePointer slicePtr = new SlicePointer(address, size);
		byte[] slice = slicePtr.get(memory);
		return new String(slice);
	}
}
