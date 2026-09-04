package com.min01.getintheteam.network;

import com.min01.getintheteam.Getintheteam;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(
            new ResourceLocation(Getintheteam.MODID, "main"))
            .serverAcceptedVersions((version) -> true)
            .clientAcceptedVersions((version) -> true)
            .networkProtocolVersion(() -> String.valueOf(1))
            .simpleChannel();

    public static void registerSPackets() {
        INSTANCE.messageBuilder(Sgetteamlist.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(Sgetteamlist::encode)
                .decoder(Sgetteamlist::new)
                .consumerMainThread(Sgetteamlist::handle)
                .add();

        INSTANCE.messageBuilder(Skickmember.class, 1, NetworkDirection.PLAY_TO_SERVER)
                .encoder(Skickmember::encode)
                .decoder(Skickmember::new)
                .consumerMainThread(Skickmember::handle)
                .add();

        INSTANCE.messageBuilder(Cgetteamlist.class, 2, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(Cgetteamlist::encode)
                .decoder(Cgetteamlist::new)
                .consumerMainThread(Cgetteamlist::handle)
                .add();
    }

    //Utility
    public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }

    public static void sendToPlayer(Object message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToAll(Object message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}