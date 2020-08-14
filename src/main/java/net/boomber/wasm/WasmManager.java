package net.boomber.wasm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.mojang.brigadier.context.CommandContext;

import net.boomber.wasm.exception.DataStorageNotFoundException;
import net.boomber.wasm.exception.FunctionNotFoundException;
import net.boomber.wasm.exception.UnknownModuleException;
import net.boomber.wasm.module.ReturnPointer.DataPointer;
import net.boomber.wasm.module.SlicePointer;
import net.boomber.wasm.module.StringPointer;
import net.boomber.wasm.module.WasmModule;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

/**
 * WasmManager
 */
public class WasmManager {

	public static final Map<Identifier, WasmModule> modules = new HashMap<>();
	public static final String PREFIX = "wasm/";
	public static final String EXTENSION = ".wasm";

	public static WasmModule get(Identifier id) throws UnknownModuleException {
		WasmModule module = modules.get(id);
		
		if (module == null) {
			throw new UnknownModuleException(id);
		}
		
		return module;
	}

	public static void add(Identifier id, WasmModule module) {
		modules.put(id, module);
	}

	public static void clear() {
		modules.clear();
	}

	public static void TestModules() {
		for (WasmModule module : WasmManager.modules.values()) {
			WasmFabric.info("");
			WasmFabric.info("Running " + module.id + "...");

			try {
				String content = "Hello, world!";
				WasmFabric.info("Input: '" + content + "'");
				
				StringPointer strPtr = module.uploadString(content);
				DataPointer retPtr = module.call("reverse", strPtr.address, strPtr.size);
				StringPointer resPtr = new StringPointer(retPtr);
				
				String result = module.takeString(resPtr);
				WasmFabric.info("Result: " + result);
				
			} catch (FunctionNotFoundException e) {
				WasmFabric.error(e.getMessage());
			}
		}
	}
	
	public static Identifier expandId(Identifier shortId) {
		String namespace = shortId.getNamespace();
		String path = PREFIX + shortId.getPath() + EXTENSION;
		return new Identifier(namespace, path);
	}
	
	public static CompoundTag getStorage(CommandContext<ServerCommandSource> ctx, Identifier id) throws DataStorageNotFoundException {
		DataCommandStorage storages = ctx.getSource().getMinecraftServer().getDataCommandStorage();
		CompoundTag result = storages.get(id);
		
		if (result == null) {
			throw new DataStorageNotFoundException(id);
		}
		
		return result;
	}
	
	public static final String INPUT = "input";
	public static final String OUTPUT = "output";
	
	public static int callFunction(CommandContext<ServerCommandSource> ctx, Identifier wasmId, Identifier storageId, String functionName) throws RuntimeException, IOException {
		Identifier id = expandId(wasmId);
		
		CompoundTag storage = getStorage(ctx, storageId);
		WasmModule module = get(id);
		
		CompoundTag input = storage.getCompound(INPUT);
		
		byte[] inputBytes = nbtToBytes(input);
		
		SlicePointer inputPtr = module.uploadBytes(inputBytes);
		DataPointer retPtr = module.call(functionName, inputPtr.address, inputPtr.size);
		SlicePointer resultPtr = new SlicePointer(retPtr.address, retPtr.size);
		
		byte[] result = module.takeBytes(resultPtr);
		CompoundTag resultNbt = bytesToNbt(result);
		
		storage.put(OUTPUT, resultNbt);
		return resultNbt.getSize();
	}
	
	private static byte[] nbtToBytes(CompoundTag input) throws IOException {
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		DataOutput outputStream = new DataOutputStream(output);
		NbtIo.write(input, outputStream);
		
		return output.toByteArray();
	}
	
	private static CompoundTag bytesToNbt(byte[] bytes) throws IOException {
		ByteArrayInputStream input = new ByteArrayInputStream(bytes);
		DataInputStream stream = new DataInputStream(input);
		return NbtIo.read(stream);
	}
}
