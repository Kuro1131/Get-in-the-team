package com.min01.getintheteam.mixin;

import com.min01.getintheteam.Getintheteam;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(LivingEntity.class)
public class MixinSavePet {
    @Shadow protected boolean dead;
    private int woundedTicks;

    @Unique
    private boolean isDying(LivingEntity entity) {
        return entity.serializeNBT().toString().contains("GITT_BleedOut") && !entity.isRemoved();
    }

    @Unique
    private boolean isTame(LivingEntity entity) {
        CompoundTag nbt = entity.serializeNBT();
        Player player = null;
        if (nbt.contains("Owner") ){
            player = entity.level().getPlayerByUUID(UUID.fromString(Getintheteam.IntArrayToUUID(nbt.getIntArray("Owner"))));
        }
        else if (nbt.getCompound("auto-serial").toString().contains("owner"))
        {
            player = entity.level().getPlayerByUUID(UUID.fromString(nbt.getCompound("auto-serial").toString()));
        }
        System.out.println(player);
        return player != null;
    }

    @Inject(at = @At(value = "HEAD"), method = "die")
    private void die(DamageSource pDamageSource, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (isTame(entity))
        {
            entity.addTag("GITT_BleedOut");
            entity.setHealth(0.0F);
            entity.setPose(Pose.DYING);
            entity.deathTime = 20;
        }
    }

    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;tickDeath()V", opcode = Opcodes.GETFIELD))
    private void tickDeath(LivingEntity entity) {
        if (isDying(entity))
        {
            if (entity.serializeNBT().toString().contains("GITT_Healed"))
            {
                if (entity.level().isClientSide()) {System.out.println("Client");}
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
                this.dead = false;
                return;
            }
            ++entity.deathTime;
            woundedTicks =(entity.serializeNBT().getShort("DeathTime") - 20)/20;
            if (woundedTicks < 60) return;
        }
        ++entity.deathTime;
        if (entity.deathTime >= 20 && !entity.level().isClientSide() && !entity.isRemoved()) {
            entity.level().broadcastEntityEvent(entity, (byte)60);
            entity.remove(Entity.RemovalReason.KILLED);
        }
    }
}
