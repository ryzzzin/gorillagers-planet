package com.gorillagers.planet;

import com.gorillagers.planet.client.renderer.GorillagerRenderer;
import com.gorillagers.planet.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class GorillagersPlanetModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ModEntities.GORILLAGER, GorillagerRenderer::new);
	}
}
