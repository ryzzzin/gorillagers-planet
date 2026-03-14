package com.gorillagers.planet.client.renderer;

import com.gorillagers.planet.GorillagersPlanetMod;
import com.gorillagers.planet.entity.GorillagerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GorillagerRenderer extends GeoEntityRenderer<GorillagerEntity, EntityRenderState> {
	public GorillagerRenderer(EntityRendererProvider.Context context) {
		super(context, new DefaultedEntityGeoModel<>(Identifier.fromNamespaceAndPath(GorillagersPlanetMod.MOD_ID, "gorillager")));
	}
}
