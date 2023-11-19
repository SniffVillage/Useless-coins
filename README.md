# Useless Coins
A mod/API that adds coins to the game while not changing the vanilla gameplay, 
that's why its called the Useless Coins mod. Other developers can add there own coins,
and gameplay features using the coins.

# Using it in your project
To include Useless Coins then add this to your ``build.gradle``
``` Gradle
repositories {
	maven {
		name = "Modrinth"
		url = "https://api.modrinth.com/maven"
	}
}

dependencies {
	modApi include("maven.modrinth:useless-coins:${project.useless_coins_version}")
}
```
And then include the version to the ``gradle.properties``
``` properties
useless_coins_version=0.0.1-1.20.1
```


# Adding custom coin types
When adding a new coin type you should ALWAYS use a unique ID

You might want to edit the weights to be compatable with other addons because it can be used for spawning

>setIndexes() in the VisualCoinProperties class, is used to determine the amount of coin images there are in a folder
>
>For example, setIndexes(8) means the folder has 8 images

An example:

``` Java
 public static final CoinType EXAMPLE_COIN = CoinTypeRegistryFactory.register(1241, new CoinType().setVisualCoinProperties(
            new VisualCoinProperties("textures/example_coins")
            .setIndexes(8)
            .setScale(0.020F))
            .setPickupSound(UselessCoins.COIN_COLLECT).setWeight(90));
```

You should always put a call method inside the registry class just like any other registry class:

``` Java
public static void reg() {}
```

# Coin helper
The coin helper has some helper methods:

Adding coins:
``` Java
CoinHelper.addCoins(player, amount);
```
Removing coins:
``` Java
CoinHelper.removeCoins(player, amount);
```
Getting the coins amount:
``` Java
CoinHelper.getCoins(player);
```

# Events
Currently there is only one event, the CoinPickup event:

``` Java
public class ExamplePickUpEvent implements CoinEvents.CoinPickup {

    @Override
    public void coinPickup(UselessCoins.Coin coin, PlayerEntity player) {
        // Add code here
    }
}
```
Always remember to register the event:
``` Java
CoinEvents.COIN_PICKUP_EVENT.register(new ExamplePickUpEvent());
```
