package net.boomber.wasm.module;

import java.nio.ByteBuffer;

import org.wasmer.Memory;

public class SlicePointer extends Pointer<byte[]> {
	public SlicePointer(int address, int size) {
		super(address, size);
	}

	@Override
	public byte[] get(Memory memory) {
		byte[] result = new byte[size];
		
		ByteBuffer buffer = memory.buffer();
		buffer.position(address);
		buffer.get(result);
		
		return result;
	}
}
