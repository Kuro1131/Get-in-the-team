package com.min01.getintheteam.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinSavePet {
    private long woundedTime;
    private int woundedTicks;

    @Unique
    private boolean isDying(LivingEntity entity) {
//        return entity.getPose() == Pose.DYING && isTame(entity) && !entity.isRemoved();
        return entity.serializeNBT().toString().contains("BleedOut");
    }

    @Unique
    private boolean isTame(LivingEntity entity) {
        CompoundTag nbt = entity.serializeNBT();
        return nbt.contains("Owner") || nbt.getCompound("auto-serial").toString().contains("owner");
    }

    @Inject(at = @At(value = "HEAD"), method = "die")
    private void die(DamageSource pDamageSource, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (isTame(entity))
        {

            entity.addTag("BleedOut");
            CompoundTag nbt = entity.serializeNBT();
            woundedTime = entity.level().getGameTime();
            entity.getEntityData().set(EntityDataSerializers.COMPOUND_TAG, new CompoundTag().putLong("WoundedTime", woundedTime))
            EntityDataManager dataManager = entity.getEntityData();
            dataManager.set(EntityDataSerializers.COMPOUND_TAG, new CompoundTag().putLong("WoundedTime", woundedTime));
            dataManager.set(EntityDataSerializers.INT, new IntTag((int) (entity.level().getGameTime() - woundedTime)));
            nbt.putLong("WoundedTime", woundedTime);
            nbt.putInt("WoundedTicks", (int) (entity.level().getGameTime() - woundedTime));
            entity.deserializeNBT(nbt);
            entity.setHealth(0.0F);
            entity.deathTime = 20;
            System.out.println((short) entity.level().getGameTime());
            System.out.println(entity.level().getGameTime());

            System.out.println(entity.serializeNBT());
            return;
        }
        else{
            System.out.println("Is Not Tame");
        }
        System.out.println(entity.getType());
//        System.out.println(entity.serializeNBT());
    }

//    @Inject(at = @At(value = "HEAD"), method = "tickDeath")
//    private void tickDeath(CallbackInfo ci) {
//        LivingEntity entity = (LivingEntity) (Object) this;
//        if (isDying(entity)) {
//            System.out.println("Is Dying");
//
//        }
//        return;
//    }

    @Redirect(method = "tickDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isRemoved()Z", opcode = Opcodes.GETFIELD))
    private boolean isRemoved(LivingEntity entity) {
        System.out.println(entity.getType());

        if (isDying(entity))
        {

            woundedTicks = (int) (entity.level().getGameTime() - entity.serializeNBT().getLong("WoundedTime"));
            CompoundTag nbt = entity.serializeNBT();
            nbt.putInt("WoundedTicks", woundedTicks);
            entity.deserializeNBT(nbt);

            System.out.println(entity.level().getGameTime());
            System.out.println(entity.serializeNBT().getLong("WoundedTime"));
            System.out.println(entity.serializeNBT().getInt("WoundedTicks"));
            return true;
        }
        return entity.getRemovalReason() != null;
    }

}
