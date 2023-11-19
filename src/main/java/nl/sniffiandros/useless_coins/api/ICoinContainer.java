package nl.sniffiandros.useless_coins.api;

public interface ICoinContainer {
    void addCoins(int amount);

    void removeCoins(int amount);

    int getCoins();

    void setCoins(int coins);
}
