package com.min01.getintheteam.client.screen;

import net.minecraft.client.Minecraft;

import java.util.Collection;

public class ClientHook {
    public static void  openScreen(Collection<String> collection){
        Minecraft.getInstance().setScreen(new screen(collection));
    }
}
