package net.boomber.wasm;

import java.util.HashMap;
import java.util.Map;

import net.boomber.wasm.module.FunctionNotFoundException;
import net.boomber.wasm.module.ReturnPointer.DataPointer;
import net.boomber.wasm.module.StringPointer;
import net.boomber.wasm.module.WasmModule;
import net.minecraft.util.Identifier;

/**
 * WasmManager
 */
public class WasmManager {

	public static final Map<Identifier, WasmModule> modules = new HashMap<>();

	public static WasmModule get(Identifier id) {
		return modules.get(id);
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
}
