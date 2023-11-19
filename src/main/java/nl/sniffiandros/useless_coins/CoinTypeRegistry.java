package nl.sniffiandros.useless_coins;

import nl.sniffiandros.useless_coins.api.CoinType;
import nl.sniffiandros.useless_coins.api.CoinTypeRegistryFactory;
import nl.sniffiandros.useless_coins.api.VisualCoinProperties;

public class CoinTypeRegistry {

    /**
     * This is how you register a new coin type.
     *
     * When registering a new coin type you must ALWAYS use a unique id,
     * If you give it an id of 2 for example, it will conflict with the Blue Coin
     *
     * I recommend a number with more than four digits.
     */


    public static final CoinType SILVER_COIN = CoinTypeRegistryFactory.register(0, new CoinType().setVisualCoinProperties(
            new VisualCoinProperties("textures/silver_coins")).setPickupSound(UselessCoins.COIN_COLLECT).setWeight(90));

    public static final CoinType GOLD_COIN = CoinTypeRegistryFactory.register(1, new CoinType().setVisualCoinProperties(
            new VisualCoinProperties("textures/gold_coins").setIndexes(9).setScale(0.030F))
            .setPickupSound(UselessCoins.GOLD_COIN_COLLECT).setWeight(60).value(2));

    public static final CoinType BLUE_COIN = CoinTypeRegistryFactory.register(2, new CoinType().setVisualCoinProperties(
            new VisualCoinProperties("textures/blue_coins").setIndexes(9).setScale(0.030F))
            .setPickupSound(UselessCoins.BLUE_COIN_COLLECT).setWeight(25).value(3));

    public static final CoinType PURPLE_COIN = CoinTypeRegistryFactory.register(3, new CoinType().setVisualCoinProperties(
            new VisualCoinProperties("textures/purple_coins").setScale(0.030F))
            .setPickupSound(UselessCoins.PURPLE_COIN_COLLECT).setWeight(15).value(4));

    public static void reg() {
    }
}


