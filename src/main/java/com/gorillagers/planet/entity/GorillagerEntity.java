package com.gorillagers.planet.entity;

import com.gorillagers.planet.registry.ModItems;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GorillagerEntity extends Monster implements GeoEntity {
	private static final int COOKIE_MIN_AGE_TICKS = 20;
	private static final RawAnimation WALK_ANIMATION = RawAnimation.begin().thenLoop("walk");
	private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
	private static final int COOKIE_TRADE_TICKS = 60;
	private static final double COOKIE_SEARCH_RADIUS = 10.0;

	private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);
	private int tradeTicksRemaining = 0;
	private UUID tradingPlayerUuid;

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
		this.goalSelector.addGoal(1, new MoveToCookieGoal());
		this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0));
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
	}

	@Override
	public void tick() {
		super.tick();

		if (!(this.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		ItemStack mainHand = this.getItemBySlot(EquipmentSlot.MAINHAND);

		// Recover barter progress after reload: if cookie is still in hand, resume timer.
		if (this.tradeTicksRemaining <= 0 && mainHand.is(Items.COOKIE)) {
			this.tradeTicksRemaining = COOKIE_TRADE_TICKS;
		}

		if (this.tradeTicksRemaining > 0) {
			// If cookie disappeared from hand, cancel trade safely.
			if (!mainHand.is(Items.COOKIE)) {
				this.tradeTicksRemaining = 0;
				this.tradingPlayerUuid = null;
				return;
			}

			this.tradeTicksRemaining--;

			if (this.tradeTicksRemaining == 0) {
				this.finishCookieTrade(serverLevel);
			}

			return;
		}

		if (!mainHand.isEmpty()) {
			return;
		}

		List<ItemEntity> cookies = this.level().getEntitiesOfClass(
			ItemEntity.class,
			this.getBoundingBox().inflate(1.25),
			this::isTradeCookie
		);

		if (cookies.isEmpty()) {
			return;
		}

		this.startCookieTrade(cookies.getFirst());
	}

	private boolean isTradeCookie(ItemEntity itemEntity) {
		return itemEntity.isAlive()
			&& itemEntity.getItem().is(Items.COOKIE)
			&& !itemEntity.hasPickUpDelay()
			&& itemEntity.getAge() >= COOKIE_MIN_AGE_TICKS;
	}

	private void startCookieTrade(ItemEntity cookieEntity) {
		ItemStack stack = cookieEntity.getItem();
		stack.shrink(1);

		if (stack.isEmpty()) {
			cookieEntity.discard();
		} else {
			cookieEntity.setItem(stack);
		}

		this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.COOKIE));
		this.tradeTicksRemaining = COOKIE_TRADE_TICKS;

		if (cookieEntity.getOwner() instanceof Player player) {
			this.tradingPlayerUuid = player.getUUID();
		} else {
			this.tradingPlayerUuid = null;
		}
	}

	private void finishCookieTrade(ServerLevel serverLevel) {
		this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

		Player tradingPlayer = this.tradingPlayerUuid == null ? null : serverLevel.getPlayerByUUID(this.tradingPlayerUuid);
		this.tradingPlayerUuid = null;

		Vec3 start = new Vec3(this.getX(), this.getEyeY() - 0.2, this.getZ());
		Vec3 velocity = tradingPlayer == null
			? new Vec3(0.0, 0.2, 0.0)
			: tradingPlayer.position().add(0.0, 1.0, 0.0).subtract(start).normalize().scale(0.42).add(0.0, 0.12, 0.0);

		ItemEntity reward = new ItemEntity(
			serverLevel,
			start.x,
			start.y,
			start.z,
			new ItemStack(ModItems.BANANA)
		);
		reward.setDeltaMovement(velocity);
		reward.setThrower(this);
		reward.setPickUpDelay(20);
		serverLevel.addFreshEntity(reward);
	}

	private final class MoveToCookieGoal extends Goal {
		private ItemEntity target;

		@Override
		public boolean canUse() {
			if (GorillagerEntity.this.tradeTicksRemaining > 0) {
				return false;
			}

			List<ItemEntity> nearbyCookies = GorillagerEntity.this.level().getEntitiesOfClass(
				ItemEntity.class,
				GorillagerEntity.this.getBoundingBox().inflate(COOKIE_SEARCH_RADIUS),
				GorillagerEntity.this::isTradeCookie
			);

			double bestDistance = Double.MAX_VALUE;
			ItemEntity bestTarget = null;
			for (ItemEntity cookie : nearbyCookies) {
				double d = GorillagerEntity.this.distanceToSqr(cookie);
				if (d < bestDistance) {
					bestDistance = d;
					bestTarget = cookie;
				}
			}

			this.target = bestTarget;
			return this.target != null;
		}

		@Override
		public boolean canContinueToUse() {
			return this.target != null
				&& this.target.isAlive()
				&& GorillagerEntity.this.isTradeCookie(this.target)
				&& GorillagerEntity.this.tradeTicksRemaining == 0
				&& GorillagerEntity.this.distanceToSqr(this.target) > 1.5 * 1.5;
		}

		@Override
		public void tick() {
			if (this.target != null) {
				GorillagerEntity.this.getNavigation().moveTo(this.target, 1.1);
			}
		}

		@Override
		public void stop() {
			GorillagerEntity.this.getNavigation().stop();
			this.target = null;
		}
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
