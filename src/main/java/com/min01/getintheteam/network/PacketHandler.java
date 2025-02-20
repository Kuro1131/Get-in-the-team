package com.min01.getintheteam.network;

import com.min01.getintheteam.Getintheteam;
import com.min01.getintheteam.client.handler.ClientPacketHandler;
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
        INSTANCE.messageBuilder(Sgetteamlist.class, NetworkDirection.PLAY_TO_SERVER.ordinal())
                .encoder(Sgetteamlist::encode)
                .decoder(Sgetteamlist::new)
                .consumerMainThread(Sgetteamlist::handle)
                .add();
    }

    //Utility
    public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }

    public static void sendToPlayers(Object message, ServerPlayer player) {
        INSTANCE.send((PacketDistributor.PacketTarget) message, PacketDistributor.PLAYER.with(() -> player));
    }

    public static void sendToAll(Object message) {
        INSTANCE.send((PacketDistributor.PacketTarget) message, PacketDistributor.ALL.noArg());
    }



}
