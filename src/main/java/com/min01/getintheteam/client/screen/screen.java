package com.min01.getintheteam.client.screen;

import com.min01.getintheteam.Getintheteam;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;


public class screen extends Screen {
    private static final Component TITLE = Component.literal("Team Member");
    private static final ResourceLocation Texture = new ResourceLocation(Getintheteam.MODID,"textures/gui/gui3.png");
    private Collection<String> collection;
    private final int imageWidth, imageHeigh;

    private int leftPos,topPos;

    public screen(Collection<String> collection){
        super(TITLE);

        this.collection = collection;
        this.imageWidth = 255;
        this.imageHeigh = 245;


    }

    @Override
    protected void init(){
        super.init();

        this.leftPos = (this.width - this.imageWidth) /2;
        this.topPos = (this.height - this.imageHeigh) /2;

        if (this.minecraft == null) return;
        Level level = this.minecraft.level;
        if (level == null) return;

    }

    @Override
    public void render(@NotNull GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_){
        renderBackground(p_281549_);
//        p_281549_.blit(Texture, 0, 0,0,0,this.imageWidth,this.imageHeigh);
        p_281549_.blit(Texture, this.leftPos, this.topPos,0,0,0,this.imageWidth,this.imageHeigh,this.imageWidth,this.imageHeigh);
        super.render(p_281549_,p_281550_,p_282878_,p_282465_);
        p_281549_.drawString(this.font,collection.toString(),this.leftPos + 8,this.topPos + 8,0x404040,false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
