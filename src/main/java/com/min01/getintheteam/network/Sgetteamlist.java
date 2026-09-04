package com.min01.getintheteam.network;

import com.min01.getintheteam.Getintheteam;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Team;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class Sgetteamlist {

    public Sgetteamlist() {}

    public Sgetteamlist(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    //Receive packet from player, gather team member data, and send response back to client
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            try {
                ServerPlayer player = contextSupplier.get().getSender();
                if (player == null) return;

                List<TeamMemberData> teamMembers = new ArrayList<>();
                boolean hasTeam = false;

                Team team = player.getTeam();
                if (team != null) {
                    hasTeam = true;
                    ServerLevel level = player.serverLevel();

                    for (String memberId : team.getPlayers()) {
                        String name = memberId;
                        String uuid = memberId;
                        String entityType = "";

                        // Try to resolve as UUID (entity)
                        try {
                            UUID memberUUID = UUID.fromString(memberId);
                            Entity entity = level.getEntity(memberUUID);
                            if (entity != null) {
                                name = entity.getName().getString();
                                entityType = entity.getType().getDescriptionId();
                            } else {
                                // UUID exists but entity is not loaded - keep UUID for display
                                name = "Unknown Entity";
                            }
                        } catch (IllegalArgumentException e) {
                            // Not a UUID, it's a player name
                            uuid = "";
                            name = memberId;
                        }

                        teamMembers.add(new TeamMemberData(name, uuid, entityType));
                    }
                }

                // Send response back to the requesting player
                PacketHandler.sendToPlayer(new Cgetteamlist(teamMembers, hasTeam), player);
            } catch (Exception e) {
                Getintheteam.logger.error("Failed to handle Sgetteamlist", e);
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}