package nl.sniffiandros.useless_coins.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import nl.sniffiandros.useless_coins.UselessCoins;

public class CoinType {
    public int weight = 0;
    private int id = 0;
    public int value = 1;
    public SoundEvent pickupSound = UselessCoins.COIN_COLLECT;
    public VisualCoinProperties visualCoinProperties = new VisualCoinProperties("textures/silver_coins");

    public CoinType() {}

    /**
     * This is a spawn weight
     */

    public CoinType setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    /**
     * {@link nl.sniffiandros.useless_coins.Events.OnEntityDeathEvent#afterDeath}  }
     */

    public CoinType value(int countsAs) {
        this.value = countsAs;
        return this;
    }

    public CoinType setPickupSound(SoundEvent pickupSound) {
        this.pickupSound = pickupSound;
        return this;
    }

    public CoinType setVisualCoinProperties(VisualCoinProperties visualCoinProperties) {
        this.visualCoinProperties = visualCoinProperties;
        return this;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public VisualCoinProperties getVisualCoinProperties() {
        return this.visualCoinProperties;
    }

    public SoundEvent getPickupSound() {
        return this.pickupSound;
    }
}
