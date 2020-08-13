package net.boomber.wasm.module;

import java.nio.ByteBuffer;

import org.wasmer.Memory;

public class ReturnPointer extends Pointer<ReturnPointer.DataPointer> {
	public static final int BYTES = 16;
		
	private ReturnPointer(int address, int size) {
		super(address, size);
	}
	
	public static ReturnPointer create(int address) {
		return new ReturnPointer(address, BYTES);
	}
	
	@Override
	public ReturnPointer.DataPointer get(Memory memory) {
		ByteBuffer buffer = memory.buffer();
		int ptr = buffer.getInt(address);
		int len = buffer.getInt(address + Integer.BYTES);
		return new DataPointer(ptr, len);
	}
	
	/**
	 * Intermediate pointer that hold pointer data from ReturnPointer
	 */
	public class DataPointer extends Pointer<Void> {
		public DataPointer(int address, int size) {
			super(address, size);
		}

		@Override
		public Void get(Memory memory) {
			return null;
		}

	}
}
