package com.min01.getintheteam.network;


import com.min01.getintheteam.client.handler.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

public class Sgetteamlist {

    private String Steamname;
    Collection<String> Cteamname;
    private UUID uuid;
    private boolean boo;
    public static final Logger logger = LogManager.getLogger();

    public Sgetteamlist(){}

    public Sgetteamlist(Collection<String> teamname) {
        this.Cteamname = teamname;
    }

    public Sgetteamlist(UUID uuid) {
        this.uuid = uuid;
    }
    public Sgetteamlist(boolean boo) {
        this.boo = boo;
    }
    public Sgetteamlist(String teamname) {
        this.Steamname = teamname;
    }

    public Sgetteamlist(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeBoolean(boo);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeBoolean(boo);
    }

    //Receive packet from player and print team name to system log
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        System.out.println("Handling Packet");
        contextSupplier.get().enqueueWork(() -> {
            // Work that needs to be threadsafe (most work)
            ServerPlayer player = contextSupplier.get().getSender();
            ServerLevel level = contextSupplier.get().getSender().serverLevel();
            Collection<String> collection = player.getTeam().getPlayers();
            StringBuilder entityIdString = new StringBuilder();
            collection.forEach(str -> {
                if (str.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {
                    Entity entity = level.getEntity(UUID.fromString(str));
                    if (entity != null) entityIdString.append(entity.getEncodeId()).append(",");
                }else{
                    entityIdString.append(str).append(",");
                }
            });
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handle(String.valueOf(entityIdString)));
        });
        contextSupplier.get().setPacketHandled(true);


//


    }
}
