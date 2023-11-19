package nl.sniffiandros.useless_coins.Events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import nl.sniffiandros.useless_coins.UselessCoins;

public class CoinEvents {

    /**
     * An event that is called when a player picks up a coin,
     * this event is called BEFORE the coin gets removed
     *
     * This is fired from {@link UselessCoins.Coin#onPickup)}
     *
     * this event is ONLY called on the server
     */
    public static final Event<CoinEvents.CoinPickup> COIN_PICKUP_EVENT = EventFactory.createArrayBacked(CoinEvents.CoinPickup.class, callbacks -> (coin, player) -> {
        for (CoinEvents.CoinPickup callback : callbacks) {
            callback.coinPickup(coin, player);
        }
    });

    @FunctionalInterface
    public interface CoinPickup {
        /**
         * Called when a player picks up a coin.
         *
         * @param coin the coin entity
         * @param player the player that picked up the coin
         */
        void coinPickup(UselessCoins.Coin coin, PlayerEntity player);
    }
}
