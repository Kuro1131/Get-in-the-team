package com.min01.getintheteam.client.screen;

import com.min01.getintheteam.Getintheteam;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;


public class screen extends Screen {
    private static final Component TITLE = Component.literal("Team Member");
    private static final ResourceLocation Texture = new ResourceLocation(Getintheteam.MODID,"textures/gui/gui.png");
    private List<String> list;
    private final int imageWidth, imageHeigh;
    private boolean isInit = false;
    private int leftPos,topPos;

    public screen(List<String> string){
        super(TITLE);
        System.out.println("Screen construct");
        this.list = string;
        this.imageWidth = 352;
        this.imageHeigh = 322;


    }

    public screen(){
        super(TITLE);

        System.out.println("Screen blank construct");
        this.imageWidth = 352;
        this.imageHeigh = 322;


    }

    @Override
    protected void init(){
        super.init();
        isInit = true;
        System.out.println("Screen init");
        this.leftPos = (this.width - this.imageWidth) /2;
        this.topPos = (this.height - this.imageHeigh) /2;

        if (this.minecraft == null) return;
        Level level = this.minecraft.level;
        if (level == null) return;

    }

    @Override
    public void onClose() {
        isInit = false;
        super.onClose(); // Don't forget to call the superclass method
    }

    @Override
    public void render(@NotNull GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_){
        if (!isInit)
        {
            System.out.println("Screen not init");
            onClose();
            return;
        }

        System.out.println("Screen render background");

        renderBackground(p_281549_);
//        p_281549_.blit(Texture, 0, 0,0,0,this.imageWidth,this.imageHeigh);

        System.out.println("Screen render blit");
        p_281549_.blit(Texture, this.leftPos, this.topPos,0,0,0,this.imageWidth,this.imageHeigh,this.imageWidth,this.imageHeigh);
        System.out.println("Screen super.render");
        super.render(p_281549_,p_281550_,p_282878_,p_282465_);
        System.out.println("Screen render drawString");
        if (list != null) p_281549_.drawString(this.font,list.toString(),this.leftPos + 8,this.topPos + 8,0x404040,false);

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
