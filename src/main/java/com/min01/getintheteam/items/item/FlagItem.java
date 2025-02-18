package com.min01.getintheteam.items.item;

import com.min01.getintheteam.Getintheteam;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class FlagItem extends Item{

    public FlagItem(Item.Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack p_41398_, Player p_41399_, @NotNull LivingEntity p_41400_, InteractionHand p_41401_) {
        if(p_41399_.isCrouching()) {
            Getintheteam.RemoveFromTeam(p_41400_,p_41399_);
        }
        else {
            Getintheteam.AddToTeam(p_41400_,p_41399_);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        p_41423_.add(Component.translatable("tooltip.getintheteam.flag.tooltip"));
        p_41423_.add(Component.translatable("tooltip.getintheteam.flag.tooltip2"));
        p_41423_.add(Component.translatable("tooltip.getintheteam.flag.tooltip3"));
        super.appendHoverText(p_41421_,p_41422_,p_41423_,p_41424_);
    }

}
