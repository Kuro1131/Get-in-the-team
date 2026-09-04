package com.min01.getintheteam.client.handler;

import com.min01.getintheteam.client.screen.ClientHook;
import com.min01.getintheteam.client.screen.TeamListHelper;
import com.min01.getintheteam.network.TeamMemberData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.List;

public class ClientPacketHandler {

    public static void handleTeamList(List<TeamMemberData> teamMembers, boolean hasTeam) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null || minecraft.level == null) return;

            // The client scoreboard is authoritative for display - same data /team list uses.
            // The server packet only tells us the player requested a refresh.
            Team team = minecraft.player.getTeam();
            ClientLevel level = minecraft.level;
            List<TeamMemberData> members = TeamListHelper.buildClientTeamList(team, level);
            boolean clientHasTeam = team != null;

            // If the screen is already open, refresh it in place; otherwise open it
            if (minecraft.screen instanceof com.min01.getintheteam.client.screen.screen teamScreen) {
                teamScreen.updateData(members, clientHasTeam);
            } else {
                ClientHook.openScreen(members, clientHasTeam);
            }
        });
    }
}