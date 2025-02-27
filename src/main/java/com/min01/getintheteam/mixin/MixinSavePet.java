package com.min01.getintheteam.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
    @Shadow protected boolean dead;
    private long woundedTime;
    private int woundedTicks;

    @Unique
    private boolean isDying(LivingEntity entity) {
//        return entity.getPose() == Pose.DYING && isTame(entity) && !entity.isRemoved();
        return entity.getPose() == Pose.DYING ||entity.serializeNBT().toString().contains("GITT_BleedOut") && !entity.isRemoved();
    }

    @Unique
    private boolean isTame(LivingEntity entity) {
        CompoundTag nbt = entity.serializeNBT();
        return nbt.contains("Owner") || nbt.getCompound("auto-serial").toString().contains("owner");
    }

    @Inject(at = @At(value = "HEAD"), method = "die")
    private void die(DamageSource pDamageSource, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.level().isClientSide()) {System.out.println("Client");}
        System.out.println("isDead: " +this.dead);
        if (isTame(entity))
        {

            entity.addTag("GITT_BleedOut");
            entity.setHealth(0.0F);
            entity.deathTime = 20;
//            System.out.println(entity.serializeNBT());
            System.out.println("isDead: " +this.dead);
            return;
        }
        else{
            System.out.println("Is Not Tame");
        }
        System.out.println(entity.getType());
//        System.out.println(entity.serializeNBT());
    }

    @Redirect(method = "tickDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isRemoved()Z", opcode = Opcodes.GETFIELD))
    private boolean isRemoved(LivingEntity entity) {
//        System.out.println(entity.getType());

        if (isDying(entity))
        {
            if (entity.serializeNBT().toString().contains("GITT_Healed"))
            {
//                System.out.println(entity.serializeNBT());
                if (entity.level().isClientSide()) {System.out.println("Client");}
                System.out.println("Healing from Mixin");
                System.out.println("isDead: " +this.dead);
                if (entity.getHealth() <=0.0F )entity.setHealth(2.0F);
                entity.setPose(Pose.STANDING);
                entity.deathTime = 0;

                if (entity.serializeNBT().toString().contains("GITT_Healed2"))
                {
                    entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 3));
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 5));

                }
                else{
                    entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 1));

                }
                entity.removeTag("GITT_Healed2");
                entity.removeTag("GITT_Healed");
                entity.removeTag("GITT_BleedOut");
                System.out.println("isDead: " +this.dead);
                this.dead = false;
                return true;
            }
            woundedTicks =(entity.serializeNBT().getShort("DeathTime") - 20)/20;
            if (woundedTicks < 60) return true;
        }
        return entity.getRemovalReason() != null;
    }

}
