package nl.sniffiandros.useless_coins.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import nl.sniffiandros.useless_coins.UselessCoins;
import nl.sniffiandros.useless_coins.api.VisualCoinProperties;

public class CoinRenderer extends EntityRenderer<UselessCoins.Coin> {
    protected CoinRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.15F;
    }

    @Override
    public void render(UselessCoins.Coin coin, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        VisualCoinProperties visualCoinProperties = coin.getCoinType().getVisualCoinProperties();

        int index = coin.age % visualCoinProperties.indexes;
        boolean visible = (coin.getRemovalTicks() >= 40 || coin.getRemovalTicks() % 4 >= 2) || !coin.UsesTimer();

        if (visible) {
            matrices.push();
            matrices.translate(0,Math.abs(Math.sin((float) coin.age/10)/7), 0);
            UselessCoinsClient.renderIcon(
                    getTexture(index, visualCoinProperties),
                    coin,
                    matrices,
                    this.dispatcher);
            matrices.pop();
        }

        super.render(coin, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    public static Identifier getTexture(int index, VisualCoinProperties visualCoinProperties) {
        return new Identifier(UselessCoins.MODID, visualCoinProperties.spritesLocation + String.format("/coin_%s.png", index));
    }

    @Override
    public Identifier getTexture(UselessCoins.Coin entity) {
        return null;
    }
}
