package com.min01.getintheteam.client.handler;

import com.min01.getintheteam.Getintheteam;
import com.min01.getintheteam.client.keybindings;
import com.min01.getintheteam.client.screen.ClientHook;
import com.min01.getintheteam.network.PacketHandler;
import com.min01.getintheteam.network.Sgetteamlist;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Getintheteam.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler
{
    public static Logger logger = LogManager.getLogger();
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event){
        Minecraft minecraft = Minecraft.getInstance();

        if(keybindings.INSTANCE.openTeamMenu.consumeClick())
        {
            logger.info("getPlayers: {}", minecraft.player.getTeam().getPlayers());
//            ClientLevel level = minecraft.level;

            minecraft.player.displayClientMessage(Component.literal("Open team menu"), false);
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHook.openScreen());
        }
        if(keybindings.INSTANCE.testServer.consumeClick())
        {
            minecraft.player.displayClientMessage(Component.literal("Send test"), true);
            PacketHandler.sendToServer(new Sgetteamlist());
        }
    }
}
