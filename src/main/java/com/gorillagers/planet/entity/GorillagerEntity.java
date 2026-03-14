package com.gorillagers.planet.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GorillagerEntity extends Monster implements GeoEntity {
	private static final RawAnimation WALK_ANIMATION = RawAnimation.begin().thenLoop("walk");
	private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");

	private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

	public GorillagerEntity(EntityType<? extends Monster> entityType, Level world) {
		super(entityType, world);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 20.0)
			.add(Attributes.MOVEMENT_SPEED, 0.22)
			.add(Attributes.ATTACK_DAMAGE, 3.0)
			.add(Attributes.FOLLOW_RANGE, 24.0);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1.0));
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<GorillagerEntity>("main_controller", 0, this::walkPredicate));
	}

	private PlayState walkPredicate(AnimationTest<GorillagerEntity> state) {
		if (state.isMoving()) {
			return state.setAndContinue(WALK_ANIMATION);
		}

		return state.setAndContinue(IDLE_ANIMATION);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.animatableCache;
	}
}
