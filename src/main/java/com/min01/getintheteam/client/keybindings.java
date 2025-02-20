package com.min01.getintheteam.client;

import com.min01.getintheteam.Getintheteam;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.realmsclient.client.Request;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public class keybindings {
    public static final keybindings INSTANCE = new keybindings();

    private keybindings(){}

    private static final String CATEGORY = "key.categories." + Getintheteam.MODID;

    public final KeyMapping openTeamMenu = new KeyMapping(
            "key." + Getintheteam.MODID + ".openTeam",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_RSHIFT,-1),
            CATEGORY
    );

    public final KeyMapping testServer = new KeyMapping(
            "key." + Getintheteam.MODID + ".testSer",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_RCONTROL,-1),
            CATEGORY
    );

}
