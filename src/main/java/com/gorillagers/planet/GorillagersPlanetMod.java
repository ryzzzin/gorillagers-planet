package com.gorillagers.planet;

import com.gorillagers.planet.registry.ModEntities;
import com.gorillagers.planet.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GorillagersPlanetMod implements ModInitializer {
	public static final String MOD_ID = "gorillagers_planet";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModEntities.register();
		ModItems.register();
		LOGGER.info("Gorillagers Planet initialized with GeckoLib");
	}
}
