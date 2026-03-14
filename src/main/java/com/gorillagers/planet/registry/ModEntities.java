package com.gorillagers.planet.registry;

import com.gorillagers.planet.GorillagersPlanetMod;
import com.gorillagers.planet.entity.GorillagerEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
	public static final EntityType<GorillagerEntity> GORILLAGER = Registry.register(
		BuiltInRegistries.ENTITY_TYPE,
		id("gorillager"),
		FabricEntityTypeBuilder.create(MobCategory.CREATURE, GorillagerEntity::new)
			.dimensions(EntityDimensions.fixed(0.9F, 1.8F))
			.build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), id("gorillager")))
	);

	private ModEntities() {
	}

	public static void register() {
		FabricDefaultAttributeRegistry.register(GORILLAGER, GorillagerEntity.createAttributes());
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(GorillagersPlanetMod.MOD_ID, path);
	}
}
