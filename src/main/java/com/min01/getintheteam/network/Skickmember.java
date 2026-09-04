package com.min01.getintheteam.network;

import com.min01.getintheteam.Getintheteam;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class Skickmember {

    private String memberId;

    public Skickmember() {
        this.memberId = "";
    }

    public Skickmember(String memberId) {
        this.memberId = memberId;
    }

    public Skickmember(FriendlyByteBuf buf) {
        this.memberId = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(memberId);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayer player = contextSupplier.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel();

            // Try to resolve as entity UUID
            try {
                UUID memberUUID = UUID.fromString(memberId);
                Entity entity = level.getEntity(memberUUID);
                if (entity != null) {
                    Getintheteam.RemoveFromTeam(entity, player);
                    return;
                }
            } catch (IllegalArgumentException ignored) {
                // Not a UUID, it's a player name
            }

            // Try to resolve as player name
            for (ServerPlayer serverPlayer : level.players()) {
                if (serverPlayer.getName().getString().equals(memberId)) {
                    Getintheteam.RemoveFromTeam(serverPlayer, player);
                    return;
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}