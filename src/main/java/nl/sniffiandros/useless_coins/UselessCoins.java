package nl.sniffiandros.useless_coins;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nl.sniffiandros.useless_coins.Events.CoinEvents;
import nl.sniffiandros.useless_coins.api.*;
import nl.sniffiandros.useless_coins.packet.CoinCollectS2CPacket;
import nl.sniffiandros.useless_coins.packet.FireworkS2CPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.IntFunction;

public class UselessCoins implements ModInitializer {
	public static final String MODID = "useless_coins";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final EntityType<Coin> COIN = Registry.register(Registries.ENTITY_TYPE, new Identifier(MODID,
			"coin"), FabricEntityTypeBuilder.<Coin>create(SpawnGroup.MISC, Coin::new).dimensions(EntityDimensions.fixed(0.35f, 0.7f)).build());

	public static SoundEvent COIN_BOUNCE = registerSound("coin.bounce");
	public static SoundEvent COIN_POP = registerSound("coin.pop");
	public static SoundEvent COIN_COLLECT = registerSound("coin.collect");
	public static SoundEvent GOLD_COIN_COLLECT = registerSound("coin.gold.collect");
	public static SoundEvent BLUE_COIN_COLLECT = registerSound("coin.blue.collect");
	public static SoundEvent PURPLE_COIN_COLLECT = registerSound("coin.purple.collect");

	public static final Identifier COIN_COLLECT_PACKET_ID = new Identifier(MODID, "coin_collect");
	public static final Identifier FIREWORK_PACKET_ID = new Identifier(MODID, "firework");

	private static SoundEvent registerSound(String name) {
		Identifier id = new Identifier(MODID, name);
		SoundEvent sound = SoundEvent.of(id);
		Registry.register(Registries.SOUND_EVENT, id, sound);
		return sound;
	}

	@Override
	public void onInitialize() {

		CoinTypeRegistry.reg();

		ClientPlayNetworking.registerGlobalReceiver(COIN_COLLECT_PACKET_ID, CoinCollectS2CPacket::receive);
		ClientPlayNetworking.registerGlobalReceiver(FIREWORK_PACKET_ID, FireworkS2CPacket::receive);
	}

	public static Coin spawnCoin(World world, Vec3d pos, CoinType coinType) {
		Coin coin = new Coin(world, pos.getX(), pos.getY(), pos.getZ());
		coin.setCoinType(coinType);
		world.spawnEntity(coin);
		return coin;
	}

	public static class Coin extends Entity {
		private int fallTicks;

		private static final TrackedData<Integer> TYPE_ID = DataTracker.registerData(Coin.class, TrackedDataHandlerRegistry.INTEGER);
		private static final TrackedData<Integer> REMOVAL_TICKS_ID = DataTracker.registerData(Coin.class, TrackedDataHandlerRegistry.INTEGER);
		private static final TrackedData<Boolean> USE_TIMER_ID = DataTracker.registerData(Coin.class, TrackedDataHandlerRegistry.BOOLEAN);

		public Coin(EntityType<? extends Coin> type, World world) {
			super(type, world);
			this.age = world.getRandom().nextInt(9999);
			this.UseTimer(false);
		}

		public Coin(World world, double x, double y, double z) {
			this(COIN, world);
			this.setPos(x, y, z);

			float xPos = this.random.nextFloat() * 2 - 1;
			float zPos = this.random.nextFloat() * 2 - 1;

			this.setVelocity(this.getVelocity().add(xPos/10, 0.35F, zPos/10));
			this.prevX = x;
			this.prevY = y;
			this.prevZ = z;
		}

		public static SoundEvent getBounceSound() {
			return COIN_BOUNCE;
		}

		protected MoveEffect getMoveEffect() {
			return MoveEffect.NONE;
		}

		public CoinType getCoinType() {
			return CoinTypeRegistryFactory.byId(this.dataTracker.get(TYPE_ID));
		}

		public void setCoinType(CoinType type) {
			this.dataTracker.set(TYPE_ID, type.getId());
		}

		public boolean UsesTimer() {
			return this.dataTracker.get(USE_TIMER_ID);
		}

		public void UseTimer(boolean useTimer) {
			this.dataTracker.set(USE_TIMER_ID, useTimer);
		}

		public int getRemovalTicks() {
			return this.dataTracker.get(REMOVAL_TICKS_ID);
		}

		public void setRemovalTicks(int ticks) {
			this.dataTracker.set(REMOVAL_TICKS_ID, ticks);
		}

		@Override
		public void onPlayerCollision(PlayerEntity player) {
			this.onPickup(player);
		}

		public void onPickup(PlayerEntity player) {
			if (!this.getWorld().isClient()) {
				if (player.distanceTo(this) < 1) {

					CoinType coinType = this.getCoinType();

					PacketByteBuf packetByteBuf = PacketByteBufs.create();
					packetByteBuf.writeInt(coinType.getId());

					sendDataToClient(player, COIN_COLLECT_PACKET_ID, packetByteBuf);

					CoinHelper.addCoins(player, coinType.value);

					CoinEvents.COIN_PICKUP_EVENT.invoker().coinPickup(this, player);

					this.remove(RemovalReason.DISCARDED);
				}
			}
		}

		@Override
		public boolean isFireImmune() {
			return true;
		}

		@Override
		public void tick() {
			if (!this.hasNoGravity()) {
				this.setVelocity(this.getVelocity().add(0.0D, -0.04D, 0.0D));

				if (this.isOnGround()) {
					float bounceFactor = (float) ((float)this.fallTicks/2.1);

					if (this.getVelocity().y < 0.0D) {

						boolean canBounce = bounceFactor > 6;

						if (canBounce) {
							this.playSound(getBounceSound(), 0.75F, 0.8F + Math.min((bounceFactor - 6)/10, 1));
						}

						this.setVelocity(this.getVelocity().multiply(1.0D, canBounce ? -bounceFactor : 0, 1.0D));
						this.fallTicks = 0;
					}
				}
			}

			this.move(MovementType.SELF, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.98D));

			if (!this.isOnGround()) {
				++this.fallTicks;
			}

			this.setRemovalTicks(Math.max(this.getRemovalTicks() - 1,0));

			if (this.getRemovalTicks() <= 0 && this.UsesTimer() && !this.getWorld().isClient()) {
				this.discard();
			}
		}

		@Override
		protected void initDataTracker() {
			this.dataTracker.startTracking(TYPE_ID, 0);
			this.dataTracker.startTracking(REMOVAL_TICKS_ID, 0);
			this.dataTracker.startTracking(USE_TIMER_ID, false);
		}

		@Override
		protected void readCustomDataFromNbt(NbtCompound nbt) {
			this.setCoinType(CoinTypeRegistryFactory.byId(nbt.getInt("CoinType")));
			this.setRemovalTicks(nbt.getInt("RemovalTicks"));
			this.UseTimer(nbt.getBoolean("UseTimer"));
		}

		@Override
		protected void writeCustomDataToNbt(NbtCompound nbt) {
			nbt.putInt("CoinType", this.getCoinType().getId());
			nbt.putInt("RemovalTicks", this.getRemovalTicks());
			nbt.putBoolean("UseTimer", this.UsesTimer());
		}
	}

	public static void sendDataToClient(PlayerEntity player, Identifier packet, PacketByteBuf buf) {
		MinecraftServer server = player.getServer();
		if (server != null) {
			PlayerManager manager = server.getPlayerManager();
			if (manager != null) {

				ServerPlayerEntity serverPlayer = manager.getPlayer(player.getUuid());
				if (serverPlayer != null) {
					ServerPlayNetworking.send(serverPlayer, packet, buf);
				}
			}
		}
	}
}