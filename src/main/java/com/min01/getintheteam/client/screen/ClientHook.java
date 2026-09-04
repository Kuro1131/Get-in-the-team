package com.min01.getintheteam.client.screen;

import com.min01.getintheteam.network.TeamMemberData;
import net.minecraft.client.Minecraft;

import java.util.List;

public class ClientHook {
    public static void openScreen(List<TeamMemberData> teamMembers, boolean hasTeam) {
        Minecraft.getInstance().setScreen(new screen(teamMembers, hasTeam));
    }
}