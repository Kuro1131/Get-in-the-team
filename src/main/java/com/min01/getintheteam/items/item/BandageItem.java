package com.min01.getintheteam.items.item;

import com.min01.getintheteam.Getintheteam;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class BandageItem extends Item {

    public BandageItem(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public InteractionResult interactLivingEntity(ItemStack pItem, Player pPlayer, @NotNull LivingEntity pEntity, InteractionHand pHand) {
        if (pEntity.serializeNBT().toString().contains("BleedOut") && Getintheteam.isOwner(pEntity, pPlayer) )
        {
            pEntity.addTag("GITT_Healed");
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        p_41423_.add(Component.translatable("tooltip.getintheteam.bandage.tooltip"));
        super.appendHoverText(p_41421_,p_41422_,p_41423_,p_41424_);
    }
}
