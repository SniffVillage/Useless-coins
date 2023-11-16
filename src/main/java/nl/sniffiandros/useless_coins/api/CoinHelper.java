package nl.sniffiandros.useless_coins.api;

import net.minecraft.entity.player.PlayerEntity;

public class CoinHelper {
    public static void addCoins(PlayerEntity player, int amount) {
        ((ICoinContainer)player).addCoins(amount);
    }
    public static void removeCoins(PlayerEntity player, int amount) {
        ((ICoinContainer)player).removeCoins(amount);
    }

    public static int getCoins(PlayerEntity player) {
        return ((ICoinContainer)player).getCoins();
    }
}
