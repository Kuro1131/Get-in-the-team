package com.min01.getintheteam.client.handler;

import com.min01.getintheteam.Getintheteam;
import com.min01.getintheteam.client.keybindings;
import com.min01.getintheteam.client.screen.ClientHook;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod.EventBusSubscriber(modid = Getintheteam.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler
{
//    public static Logger logger = LogManager.getLogger();
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event){
        if(keybindings.INSTANCE.openTeamMenu.consumeClick())
        {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHook.openScreen());
        }
    }
}
