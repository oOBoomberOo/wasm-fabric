package net.boomber.wasm;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.boomber.wasm.module.WasmModule;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

/**
 * WasmLoader
 */
public class WasmLoader implements IdentifiableResourceReloadListener {
	private static final Identifier id = new Identifier("wasm:loader");
	private static final String PREFIX = "wasm";
	private static final String SUFFIX = ".wasm";

	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler,
			Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {

		CompletableFuture<Collection<Identifier>> wasmResources = CompletableFuture.supplyAsync(() -> {
			return manager.findResources(PREFIX, (path) -> path.endsWith(SUFFIX));
		}, prepareExecutor);

		CompletableFuture<Collection<Identifier>> preparedResources = wasmResources
				.thenCompose(synchronizer::whenPrepared);

		return preparedResources.thenRunAsync(() -> {
			// Is this *really* the right place?
			Collection<Identifier> moduleIds = wasmResources.join();

			WasmManager.clear();

			for (Identifier id : moduleIds) {
				try {
					WasmModule module = WasmModule.fromManager(manager, id);
					WasmFabric.info("Added WASM Module: " + id);
					WasmManager.add(id, module);
				} catch (IOException e) {
					WasmFabric.error(e.getMessage());
				}
			}
		});
	}

	@Override
	public Identifier getFabricId() {
		return id;
	}
}
