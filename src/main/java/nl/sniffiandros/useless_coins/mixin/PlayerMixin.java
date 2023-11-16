package nl.sniffiandros.useless_coins.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import nl.sniffiandros.useless_coins.UselessCoins;
import nl.sniffiandros.useless_coins.api.CoinTypeRegistryFactory;
import nl.sniffiandros.useless_coins.api.ICoinContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity implements ICoinContainer {
	/**
	 * Made this for developers
	 */


	private static final int COIN_LIMIT = 9999;
	private static final TrackedData<Integer> COINS_ID = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);

	public PlayerMixin(EntityType<? extends PlayerEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public void addCoins(int amount) {
		this.setCoins(Math.min(this.getCoins() + amount, COIN_LIMIT));
	}

	@Override
	public void removeCoins(int amount) {
		this.setCoins(Math.max(this.getCoins() - amount, 0));
	}

	@Override
	public int getCoins() {
		return this.dataTracker.get(COINS_ID);
	}

	@Override
	public void setCoins(int coins) {
		this.dataTracker.set(COINS_ID, Math.min(coins, COIN_LIMIT));
	}

	@Inject(at = @At("HEAD"), method = "initDataTracker")
	public void initDataTracker(CallbackInfo ci) {
		this.dataTracker.startTracking(COINS_ID, 0);
	}

	@Inject(at = @At("HEAD"), method = "readCustomDataFromNbt")
	public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		this.setCoins(nbt.getInt(UselessCoins.MODID + ".coins"));
	}

	@Inject(at = @At("HEAD"), method = "writeCustomDataToNbt")
	public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
		nbt.putInt(UselessCoins.MODID + ".coins", this.getCoins());
	}
}