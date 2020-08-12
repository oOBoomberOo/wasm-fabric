package net.boomber.wasm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

	// TODO: Try to fix `Aborted (core dumped)` error
	public static void TestModules() {
		try {
			for (WasmModule module : WasmManager.modules.values()) {
				Collection<BoxedPointer> freePtrs = new ArrayList<>();

				WasmFabric.info("Running " + module.id + "...");

				try {
					String content = "Hello, world!";
					WasmFabric.info("Input: '" + content + "'");

					WasmFabric.info("Passing `content` into WASM's memory");
					BoxedPointer contentPtr = module.passStringToWasm(content);
					freePtrs.add(contentPtr);

					Integer retPtr = module.getReturnPointer();
					freePtrs.add(new BoxedPointer(retPtr, 16));

					WasmFabric.info("Calling `run` function with " + contentPtr);
					module.getFunction("run").apply(retPtr, contentPtr.pointer,
							contentPtr.length);

					BoxedPointer resultPtr = module.getBoxedPointer(retPtr);
					WasmFabric.info("Convert retPtr (" + retPtr + ") into boxedPointer (" + resultPtr + ")");
					freePtrs.add(resultPtr);

					WasmFabric.info("Getting result string from WASM's memory");
					String result = module.getStringFromWasm(resultPtr);
					WasmFabric.info("result: " + result);

				} catch (ClassCastException e) {
					WasmFabric.error(e.getMessage());
				} finally {
					for (BoxedPointer ptr : freePtrs) {
						module.free(ptr);
					}
				}

				module.instance.close();
			}

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
}