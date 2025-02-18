package com.min01.getintheteam.client.screen;

import net.minecraft.client.Minecraft;

public class ClientHook {
    public static void  openScreen(){
        Minecraft.getInstance().setScreen(new screen());
    }
}
