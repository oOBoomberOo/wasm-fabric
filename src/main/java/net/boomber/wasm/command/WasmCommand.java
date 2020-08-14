package net.boomber.wasm.command;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.io.IOException;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.boomber.wasm.WasmFabric;
import net.boomber.wasm.WasmManager;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

/**
 * WasmCommand
 */
public class WasmCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
				literal("wasm").then(
						argument("path", IdentifierArgumentType.identifier()).then(
								argument("function", StringArgumentType.string()).then(
										argument("storage", IdentifierArgumentType.identifier()).executes(ctx -> {
											try {
												Identifier wasmId = IdentifierArgumentType.getIdentifier(ctx, "path");
												Identifier storageId = IdentifierArgumentType.getIdentifier(ctx, "storage");
												String functionName = StringArgumentType.getString(ctx, "function");
												
												return WasmManager.callFunction(ctx, wasmId, storageId, functionName);
											} catch (RuntimeException | IOException e) {
												WasmFabric.error(e.getMessage());
												e.printStackTrace();
												
												LiteralText message = new LiteralText(e.getMessage());
												throw new SimpleCommandExceptionType(message).create();
											}
										})
								)
						)
				)
		);
	}
}