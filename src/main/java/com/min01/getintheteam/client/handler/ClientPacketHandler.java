package com.min01.getintheteam.client.handler;

import java.util.List;

public class ClientPacketHandler {

    public static void handle(String string) {
        System.out.println("Client Received: " + string);
        List<String> entityList = List.of(string.split(","));
        System.out.println("entityList: " + entityList);
    }
}
