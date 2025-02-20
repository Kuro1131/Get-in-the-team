package com.min01.getintheteam.client.handler;

import com.min01.getintheteam.Getintheteam;
import com.min01.getintheteam.client.keybindings;
import com.min01.getintheteam.client.screen.ClientHook;
import com.min01.getintheteam.network.PacketHandler;
import com.min01.getintheteam.network.Sgetteamlist;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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

            Collection<String> collection = minecraft.player.getTeam().getPlayers();
        collection.forEach(str -> {
            System.out.println("UUID: " + str);
            //  bc660be7-59d7-4a79-8ab5-4211fb52292e
            if (str.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {

                Entity entity = minecraft.level.getPlayerByUUID(UUID.fromString(str));
               if (entity != null) {
                   System.out.println("Entity: " + entity.getClass());
               }

            }
        });

            minecraft.player.displayClientMessage(Component.literal("Open team menu"), false);
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHook.openScreen(minecraft.player.getTeam().getPlayers()));
        }
        if(keybindings.INSTANCE.testServer.consumeClick())
        {
            minecraft.player.displayClientMessage(Component.literal("Send test"), true);
            PacketHandler.sendToServer(new Sgetteamlist());
        }
    }
}
