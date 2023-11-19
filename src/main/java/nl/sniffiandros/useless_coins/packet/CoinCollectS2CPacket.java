package nl.sniffiandros.useless_coins.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import nl.sniffiandros.useless_coins.UselessCoins;
import nl.sniffiandros.useless_coins.api.CoinHelper;
import nl.sniffiandros.useless_coins.api.CoinType;
import nl.sniffiandros.useless_coins.api.CoinTypeRegistryFactory;
import nl.sniffiandros.useless_coins.api.ICoinContainer;

public class CoinCollectS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender response) {

        int id = buf.readInt();

        if (client != null) {

            float add = 0;

            CoinType type = CoinTypeRegistryFactory.byId(id);
            PlayerEntity player = client.player;
            if (player != null) {
                add = player.getRandom().nextFloat()/10;
                CoinHelper.addCoins(player, type.value);
            }

            SoundInstance soundInstance = PositionedSoundInstance.master(type.getPickupSound(), 1.0F + add, 1.0F);
            client.getSoundManager().play(soundInstance);
        }
    }
}
