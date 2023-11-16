# Useless Coins
A mod/API that adds coins to the game while not changing the vanilla gameplay, 
that's why its called the Useless Coins mod. Other developers can add there own coins,
and gameplay features using the coins.

# Adding custom coin types
When adding a new coin type you should ALWAYS use a unique ID

You might want to edit the weights to be compatable with other addons because it can be used for spawning

>setIndexes() in the VisualCoinProperties class, is used to determine the amount of coin images there are in a folder
>
>For example, setIndexes(8) means the folder has 8 images

An example:

```
 public static final CoinType EXAMPLE_COIN = CoinTypeRegistryFactory.register(1241, new CoinType().setVisualCoinProperties(
            new VisualCoinProperties("textures/example_coins")
            .setIndexes(8)
            .setScale(0.020F))
            .setPickupSound(UselessCoins.COIN_COLLECT).setWeight(90));
```
