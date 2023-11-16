package nl.sniffiandros.useless_coins.Events;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.FireworkStarItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nl.sniffiandros.useless_coins.UselessCoins;
import nl.sniffiandros.useless_coins.api.CoinType;
import nl.sniffiandros.useless_coins.api.CoinTypeRegistryFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class OnEntityDeathEvent implements ServerLivingEntityEvents.AfterDeath {
    private static final float MAX_HEALTH_THRESHOLD = 40.0F;

    @Override
    public void afterDeath(LivingEntity killedEntity, DamageSource damageSource) {
        float health = killedEntity.getMaxHealth();
        int amount = (int) Math.floor(health / 7.5);

        List<Integer> weightedTypes = getWeightedTypes(CoinTypeRegistryFactory.getCoinTypeList(), health);

        Vec3d pos = killedEntity.getPos();

        boolean bonus = killedEntity.getRandom().nextFloat() > 0.85F;

        if (bonus) {
            PacketByteBuf packetByteBuf = PacketByteBufs.create();
            packetByteBuf.writeDouble(pos.x);
            packetByteBuf.writeDouble(pos.y + killedEntity.getHeight());
            packetByteBuf.writeDouble(pos.z);

            killedEntity.getWorld().getPlayers().forEach(player -> {
                if (killedEntity.distanceTo(player) < 50) {
                    UselessCoins.sendDataToClient(player, UselessCoins.FIREWORK_PACKET_ID, packetByteBuf);
                }
            });
        }

        for (int i = 0; i < amount + (bonus ? 20 : 0); i++) {

            int rnd = killedEntity.getRandom().nextInt(weightedTypes.size());
            int selectedType = weightedTypes.get(rnd);
            CoinType coinType = CoinTypeRegistryFactory.byId(selectedType);

            int c = coinType.value;

            if (i + c <= amount) {
                i += c;
            }

            UselessCoins.Coin coin = UselessCoins.spawnCoin(killedEntity.getWorld(), pos.add(0, killedEntity.getHeight() / 2, 0), coinType);
            coin.UseTimer(true);
            coin.setRemovalTicks(220);
            if (!bonus) coin.playSound(UselessCoins.COIN_POP, 2.0F, 1.0F + killedEntity.getRandom().nextFloat());

        }
    }

    private static List<Integer> getWeightedTypes(List<CoinType> coinTypes, float maxHealth) {
        List<Integer> weightedTypes = new ArrayList<>();

        for (CoinType coinType : coinTypes) {
            int baseWeight = coinType.weight;
            addWeightedTypes(weightedTypes, coinType.getId(), baseWeight, maxHealth);
        }

        return weightedTypes;
    }

    private static void addWeightedTypes(List<Integer> list, int type, int baseWeight, float maxHealth) {
        int adjustedWeight = Math.max(1, (int) (baseWeight * (maxHealth / MAX_HEALTH_THRESHOLD)));

        for (int i = 0; i < adjustedWeight; i++) {
            list.add(type);
        }
    }


}
