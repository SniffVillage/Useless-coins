package nl.sniffiandros.useless_coins.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nl.sniffiandros.useless_coins.api.CoinHelper;
import nl.sniffiandros.useless_coins.api.CoinType;
import nl.sniffiandros.useless_coins.api.CoinTypeRegistryFactory;

public class FireworkS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender response) {

        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();

        if (client != null) {
            if (client.world != null) {
                spawnFirework(new Vec3d(x, y, z), client.world);
            }
        }
    }

    public static void spawnFirework(Vec3d pos, World world) {

        System.out.println("Test");

        int[] colors = {0xFF0000, 0xFF7F00, 0xFFFF00, 0x00FF00, 0x0000FF, 0x4B0082, 0x9400D3}; // Rainbow colors

        NbtCompound firework = new NbtCompound();
        NbtCompound props = new NbtCompound();

        props.putBoolean("Flicker", true);
        props.putIntArray("Colors", colors);

        NbtList explosions = new NbtList();

        explosions.add(props);
        firework.put("Explosions", explosions);

        world.addFireworkParticle(pos.x,pos.y,pos.z,0,0,0, firework);
    }
}
