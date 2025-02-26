package com.min01.getintheteam.mixin;


import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface MixinAccessor {
    @Accessor("DATA_POSE")
    EntityDataAccessor<Pose> getDATA_POSE();
}