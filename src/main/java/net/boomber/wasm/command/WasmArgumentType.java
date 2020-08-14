package net.boomber.wasm.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.boomber.wasm.WasmManager;
import net.minecraft.util.Identifier;

public class WasmArgumentType implements ArgumentType<WasmArgumentType.Inner> {
	public static WasmArgumentType create() {
		return new WasmArgumentType();
	}
	
	@Override
	public WasmArgumentType.Inner parse(StringReader reader) throws CommandSyntaxException {
		Identifier id = Identifier.fromCommandInput(reader);
		
		return new WasmArgumentType.Inner() {
			@Override
			public Identifier shortId() {
				return id;
			}
			
			@Override
			public Identifier expandedId() {
				return WasmManager.expandId(shortId());
			}
		};
	}

	public interface Inner {
		public Identifier shortId();
		public Identifier expandedId();
	}
}
