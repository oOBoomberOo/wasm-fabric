package net.boomber.wasm.module;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;
import org.wasmer.Instance;
import org.wasmer.Memory;
import org.wasmer.Module;
import org.wasmer.exports.Function;

import net.boomber.wasm.module.ReturnPointer.DataPointer;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class WasmModule {
	public static final String BINDGEN_MALLOC = "__wbindgen_malloc";
	public static final String BINDGEN_FREE = "__wbindgen_free";
	public static final String MEMORY = "memory";
	
	private final Function allocator;
	private final Function deallocator;
	private final Memory memory;
	
	public Identifier id;
	public Instance instance;
	
	public WasmModule(Module module, Identifier id) {
		this(module.instantiate(), id);
	}
	
	public WasmModule(Instance instance, Identifier id) {
		this.allocator = instance.exports.getFunction(BINDGEN_MALLOC);
		this.deallocator = instance.exports.getFunction(BINDGEN_FREE);
		this.memory = instance.exports.getMemory(MEMORY);
		this.instance = instance;
		this.id = id;
	}
	
	public ByteBuffer buffer() {
		return memory.buffer();
	}
	
	public int malloc(int len) {
		return (int) allocator.apply(len)[0];
	}
	
	public void free(Pointer<?> ptr) {
		deallocator.apply(ptr.address, ptr.size);
	}
	
	public StringPointer uploadString(String content) {
		byte[] data = content.getBytes(StandardCharsets.UTF_8);
		int len = data.length;
		int ptr = malloc(len);
		
		ByteBuffer buffer = buffer();
		buffer.position(ptr);
		buffer.put(data);
		
		return new StringPointer(ptr, len);
	}
	
	public String takeString(StringPointer ptr) {
		try {			
			return ptr.get(memory);
		} finally {	
			free(ptr);
		}
	}
	
	public ReturnPointer returnPointer() {
		int ptr = malloc(ReturnPointer.BYTES);
		return ReturnPointer.create(ptr);
	}
	
	public DataPointer call(String name, Object... args) throws FunctionNotFoundException {
		ReturnPointer retPtr = returnPointer();
		List<Object> params = new ArrayList<>();
		
		params.add(retPtr.address);
		
		for (Object arg : args) {
			params.add(arg);
		}
		
		Object[] paramArrays = params.toArray();
		
		getFunction(name).apply(paramArrays);
		
		return retPtr.get(memory);
	}
	
	public Function getFunction(String name) throws FunctionNotFoundException {
		try {
			return instance.exports.getFunction(name);
		} catch (Exception e) {
			throw new FunctionNotFoundException(name);
		}
	}

	public static WasmModule fromBytes(byte[] data, Identifier id) {
		Instance instance = new Instance(data);
		return new WasmModule(instance, id);
	}

	public static WasmModule fromManager(ResourceManager manager, Identifier id) throws IOException {
		Resource resource = manager.getResource(id);
		InputStream stream = resource.getInputStream();
		byte[] data = IOUtils.toByteArray(stream);
		return fromBytes(data, id);
	}
}
