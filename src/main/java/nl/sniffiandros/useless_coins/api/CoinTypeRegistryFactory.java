package nl.sniffiandros.useless_coins.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CoinTypeRegistryFactory {
    private static final Map<Integer, CoinType> COIN_TYPES = new HashMap<>();

    public static CoinType register(int id, CoinType coinType) {
        if (coinType.visualCoinProperties == null) {
            throw new IllegalArgumentException("VisualCoinProperties is null");
        }

        coinType.setId(id);

        COIN_TYPES.put(id, coinType);

        return coinType;
    }

    public static CoinType byId(int id) {
        CoinType coinType = COIN_TYPES.get(id);
        if (coinType == null) {
            coinType = COIN_TYPES.get(0);
        }

        return coinType;
    }

    public static List<CoinType> getCoinTypeList() {
        List<CoinType> coinTypes = new ArrayList<>();

        COIN_TYPES.forEach((integer, coinType) ->
                coinTypes.add(coinType));

        return coinTypes;
    }
}
