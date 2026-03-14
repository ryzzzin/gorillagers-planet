package com.gorillagers.planet.registry;

import com.gorillagers.planet.GorillagersPlanetMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public final class ModItems {
	public static final Item GORILLAGER_SPAWN_EGG = Registry.register(
		BuiltInRegistries.ITEM,
		id("gorillager_spawn_egg"),
		new SpawnEggItem(
			new Item.Properties()
				.setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), id("gorillager_spawn_egg")))
				.spawnEgg(ModEntities.GORILLAGER)
		)
	);

	private ModItems() {
	}

	public static void register() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> entries.accept(GORILLAGER_SPAWN_EGG));
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(GorillagersPlanetMod.MOD_ID, path);
	}
}
