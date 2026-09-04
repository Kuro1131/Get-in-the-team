package com.min01.getintheteam.network;

import com.min01.getintheteam.client.handler.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Cgetteamlist {

    private List<TeamMemberData> teamMembers;
    private boolean hasTeam;

    public Cgetteamlist() {
        this.teamMembers = new ArrayList<>();
        this.hasTeam = false;
    }

    public Cgetteamlist(List<TeamMemberData> teamMembers, boolean hasTeam) {
        this.teamMembers = teamMembers;
        this.hasTeam = hasTeam;
    }

    public Cgetteamlist(FriendlyByteBuf buf) {
        this.hasTeam = buf.readBoolean();
        int size = buf.readInt();
        this.teamMembers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String name = buf.readUtf();
            String uuid = buf.readUtf();
            String entityType = buf.readUtf();
            this.teamMembers.add(new TeamMemberData(name, uuid, entityType));
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(hasTeam);
        buf.writeInt(teamMembers.size());
        for (TeamMemberData member : teamMembers) {
            buf.writeUtf(member.getName());
            buf.writeUtf(member.getUuid());
            buf.writeUtf(member.getEntityType());
        }
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleTeamList(teamMembers, hasTeam));
        });
        contextSupplier.get().setPacketHandled(true);
    }
}