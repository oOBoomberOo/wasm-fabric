package net.boomber.wasm.command;

import com.mojang.brigadier.CommandDispatcher;

import net.boomber.wasm.WasmFabric;
import net.boomber.wasm.WasmManager;
import net.minecraft.server.command.ServerCommandSource;
import static net.minecraft.server.command.CommandManager.*;

/**
 * WasmCommand
 */
public class WasmCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("wasm").executes((ctx) -> {
			try {
				WasmManager.TestModules();
				return 1;
			} catch (RuntimeException e) {
				WasmFabric.error(e.getMessage());
				return 0;
			}
		}));
	}
}