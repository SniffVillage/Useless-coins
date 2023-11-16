package nl.sniffiandros.useless_coins.api;

public class VisualCoinProperties {
    public final String spritesLocation;
    public float scale = 0.025F;
    public int indexes = 7;

    public VisualCoinProperties(String spritesLocation) {
        this.spritesLocation = spritesLocation;
    }

    public VisualCoinProperties setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public VisualCoinProperties setIndexes(int indexes) {
        this.indexes = indexes;
        return this;
    }
}
