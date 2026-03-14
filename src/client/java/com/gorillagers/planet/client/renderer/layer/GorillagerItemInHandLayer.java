package com.gorillagers.planet.client.renderer.layer;

import com.gorillagers.planet.entity.GorillagerEntity;
import java.util.List;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer.RenderData;
import software.bernie.geckolib.renderer.layer.builtin.ItemInHandGeoLayer;

public class GorillagerItemInHandLayer extends ItemInHandGeoLayer<GorillagerEntity, Void, EntityRenderState> {
	public GorillagerItemInHandLayer(GeoRenderer<GorillagerEntity, Void, EntityRenderState> renderer) {
		super(renderer, "locator", "locator");
	}

	@Override
	protected List<RenderData<EntityRenderState>> getRelevantBones(EntityRenderState renderState, BakedGeoModel model) {
		return List.of(renderDataForHand("locator", renderState));
	}
}
