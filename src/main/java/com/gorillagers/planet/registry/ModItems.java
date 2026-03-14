package com.gorillagers.planet.registry;

import com.gorillagers.planet.GorillagersPlanetMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public final class ModItems {
	public static final Item BANANA = Registry.register(
		BuiltInRegistries.ITEM,
		id("banana"),
		new Item(
			new Item.Properties()
				.setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), id("banana")))
				.food(new FoodProperties(6, 16.0F, false))
		)
	);

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
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> entries.accept(BANANA));
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(GorillagersPlanetMod.MOD_ID, path);
	}
}
