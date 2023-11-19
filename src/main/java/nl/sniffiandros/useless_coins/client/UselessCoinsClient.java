package nl.sniffiandros.useless_coins.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import nl.sniffiandros.useless_coins.packet.CoinCollectS2CPacket;
import nl.sniffiandros.useless_coins.UselessCoins;
import nl.sniffiandros.useless_coins.api.VisualCoinProperties;
import org.joml.Matrix4f;

public class UselessCoinsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(UselessCoins.COIN_COLLECT_PACKET_ID, CoinCollectS2CPacket::receive);
        EntityRendererRegistry.register(UselessCoins.COIN, CoinRenderer::new);
    }

    public static void renderIcon(Identifier icon, UselessCoins.Coin coin, MatrixStack matrices, EntityRenderDispatcher dispatcher) {
        VisualCoinProperties visualCoinProperties = coin.getCoinType().getVisualCoinProperties();

        double d = dispatcher.getSquaredDistanceToCamera(coin);
        if (d > 4096.0) {
            return;
        }

        matrices.push();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        matrices.translate(0, coin.getBoundingBox().getYLength()/2, 0);
        matrices.multiply(dispatcher.getRotation());
        matrices.scale(-visualCoinProperties.scale, -visualCoinProperties.scale, visualCoinProperties.scale);

        renderIcon(icon, matrices, -8, -8, 16, 16, 0, 1, 0, 1, 1);

        matrices.pop();
    }

    private static void renderIcon(Identifier icon, MatrixStack matrixStack, int x, int y, int w, int h, float u0, float u1, float v0, float v1, float alpha) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        RenderSystem.enableDepthTest();
        MinecraftClient.getInstance().getTextureManager().getTexture(icon).setFilter(false, false);
        RenderSystem.setShaderTexture(0, icon);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferbuilder.vertex(matrix, (float)x,			(float)(y + h),	0).texture(u0, v1).color(1.0f, 1.0f, 1.0f, alpha).next();
        bufferbuilder.vertex(matrix, (float)(x + w),	(float)(y + h),	0).texture(u1, v1).color(1.0f, 1.0f, 1.0f, alpha).next();
        bufferbuilder.vertex(matrix, (float)(x + w),	(float)y,		0).texture(u1, v0).color(1.0f, 1.0f, 1.0f, alpha).next();
        bufferbuilder.vertex(matrix, (float)x,			(float)y,		0).texture(u0, v0).color(1.0f, 1.0f, 1.0f, alpha).next();
        BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
        RenderSystem.disableDepthTest();
    }
}
