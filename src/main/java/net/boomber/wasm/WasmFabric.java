package net.boomber.wasm;

import net.boomber.wasm.command.WasmCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WasmFabric implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "wasm";
    public static final String MOD_NAME = "WASM";

    public static final WasmLoader loader = new WasmLoader();

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(loader);

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            WasmCommand.register(dispatcher);
        });
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, message);
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void error(String message) {
        log(Level.ERROR, message);
    }

    public static void debug(String message) {
        log(Level.WARN, message);
    }
}