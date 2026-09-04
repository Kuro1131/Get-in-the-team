package com.min01.getintheteam.client.handler;

import com.min01.getintheteam.Getintheteam;
import com.min01.getintheteam.client.keybindings;
import com.min01.getintheteam.client.screen.ClientHook;
import com.min01.getintheteam.client.screen.TeamListHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = Getintheteam.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler
{
    public static Logger logger = LogManager.getLogger();
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event){
        // Only run once per tick (at the START phase)
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) return;

        if(keybindings.INSTANCE.openTeamMenu.consumeClick())
        {
            // Read the client-synced scoreboard directly - same data /team list uses
            Team team = minecraft.player.getTeam();
            logger.info("openTeamMenu: player team = {}", team == null ? "null" : team.getName());
            if (team != null) {
                logger.info("openTeamMenu: members = {}", team.getPlayers());
            }

            ClientLevel level = minecraft.level;
            ClientHook.openScreen(TeamListHelper.buildClientTeamList(team, level), team != null);
        }
        if(keybindings.INSTANCE.testServer.consumeClick())
        {
            minecraft.player.displayClientMessage(Component.literal("Send test"), true);
            // Ask the server for an authoritative refresh (updates the open screen if any)
            com.min01.getintheteam.network.PacketHandler.sendToServer(new com.min01.getintheteam.network.Sgetteamlist());
        }
    }
}