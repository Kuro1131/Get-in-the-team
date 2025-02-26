package com.min01.getintheteam.client.handler;

import com.min01.getintheteam.client.screen.ClientHook;
import com.min01.getintheteam.client.screen.screen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import java.util.List;

public class ClientPacketHandler {

    public static void handle(String string) {
        System.out.println("Client Received: " + string);
        List<String> entityList = List.of(string.split(","));
        System.out.println("entityList: " + entityList);
        Minecraft.getInstance().setScreen(new screen(entityList));
    }
}
