package com.min01.getintheteam.client.handler;

import com.min01.getintheteam.Getintheteam;
import com.min01.getintheteam.client.keybindings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Getintheteam.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientModHandler {
    @SubscribeEvent
    public static void registerKeysBinding(RegisterKeyMappingsEvent event){
        event.register(keybindings.INSTANCE.openTeamMenu);
        event.register(keybindings.INSTANCE.testServer);
    }
}