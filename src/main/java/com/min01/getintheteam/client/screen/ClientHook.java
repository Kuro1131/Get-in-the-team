package com.min01.getintheteam.client.screen;

import net.minecraft.client.Minecraft;

import java.util.List;

public class ClientHook {
    public static void  openScreen(List<String> list){
        Minecraft.getInstance().setScreen(new screen(list));
    }

    public static void  openScreen(){
        Minecraft.getInstance().setScreen(new screen());
    }
}
