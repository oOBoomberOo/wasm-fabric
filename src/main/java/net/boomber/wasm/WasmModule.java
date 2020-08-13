package net.boomber.wasm;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.wasmer.Instance;
import org.wasmer.Memory;
import org.wasmer.exports.Function;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class WasmModule {
	public final Identifier id;
	public final Instance instance;
	
	public final Memory memory;

	public static final int RET_POINTER_SIZE = 16;
	public static final String BINDGEN_MALLOC = "__wbindgen_malloc";
	public static final String BINDGEN_REALLOC = "__wbindgen_realloc";
	public static final String BINDGEN_FREE = "__wbindgen_free";

	public int WASM_VECTOR_LEN = 0;

	public WasmModule(ResourceManager manager, Identifier id, String memoryName) throws IOException, RuntimeException {
		this.id = id;
		this.instance = getInstance(manager, id);
		this.memory = getMemory(this.instance, memoryName);
	}

	private static Instance getInstance(ResourceManager manager, Identifier id) throws IOException, RuntimeException {
		Resource resource = manager.getResource(id);
		InputStream stream = resource.getInputStream();
		byte[] data = IOUtils.toByteArray(stream);
		return new Instance(data);
	}

	public static Memory getMemory(Instance instance, String name) {
		return instance.exports.getMemory(name);
	}

	public BoxedPointer passStringToWasm(String content) {
		byte[] byteData = content.getBytes(StandardCharsets.UTF_8);
		int len = byteData.length;
		int ptr = malloc(len);
		
		{
			ByteBuffer buffer = memory.buffer();

			buffer.position(ptr);
			buffer.put(byteData);
		}

		WASM_VECTOR_LEN = len;

		return new BoxedPointer(ptr, len);
	}

	public String getStringFromWasm(BoxedPointer boxedPtr) {
		byte[] byteData = new byte[boxedPtr.length];

		{
			ByteBuffer buffer = memory.buffer();
			buffer.position(boxedPtr.pointer);
			buffer.get(byteData);
		}

		return new String(byteData);
	}

	public int getReturnPointer() {
		return malloc(RET_POINTER_SIZE);
	}

	public BoxedPointer getBoxedPointer(Integer retPtr) {
		ByteBuffer buffer = memory.buffer();
		
		Integer ptr = buffer.getInt(retPtr);
		Integer len = buffer.getInt(retPtr + Integer.BYTES);

		return new BoxedPointer(ptr, len);
	}

	public int malloc(Integer byteSize) {
		Object pointer = getFunction(BINDGEN_MALLOC).apply(byteSize)[0];
		return (int) pointer;
	}

	public int realloc(Integer pointer, Integer oldSize, Integer newSize) {
		Object result = getFunction(BINDGEN_REALLOC).apply(pointer, oldSize, newSize)[0];
		return (int) result;
	}

	public void free(BoxedPointer rangePtr) {
		getFunction(BINDGEN_FREE).apply(rangePtr.pointer, rangePtr.length);
	}

	public Function getFunction(String name) throws ClassCastException {
		return instance.exports.getFunction(name);
	}
}
