package com.min01.getintheteam.mixin;

import com.min01.getintheteam.EventHandlerForge;
import java.util.function.Supplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.scores.PlayerTeam;


@Mixin(ServerLevel.class)
public abstract class MixinServerLevel extends Level
{
	protected MixinServerLevel(WritableLevelData p_270739_, ResourceKey<Level> p_270683_, RegistryAccess p_270200_, Holder<DimensionType> p_270240_, Supplier<ProfilerFiller> p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_) 
	{
		super(p_270739_, p_270683_, p_270200_, p_270240_, p_270692_, p_270904_, p_270470_, p_270248_, p_270466_);
	}

	@Inject(at = @At("HEAD"), method = "addFreshEntity")
	private void addFreshEntity(Entity p_8837_, CallbackInfoReturnable<Boolean> ci)
	{
		MySecurityManager manager = new MySecurityManager();
		Class<?>[] ctx = manager.getContext();
		for(Class<?> clazz : ctx)
		{
			if(EventHandlerForge.ENTITY_MAP.containsKey(clazz.hashCode()))
			{
				Entity entity = EventHandlerForge.ENTITY_MAP.get(clazz.hashCode());
				if(entity != null)
				{
					if(entity.getTeam() != null && entity.getServer() != null)
					{
						PlayerTeam team = entity.getServer().getScoreboard().getPlayerTeam(entity.getTeam().getName());
						if (team != null)
						{entity.getServer().getScoreboard().addPlayerToTeam(p_8837_.getStringUUID(), team);}
					}
				}
			}
			else if(EventHandlerForge.ENTITY_MAP2.containsKey(clazz.hashCode()))
			{
				Entity entity = EventHandlerForge.ENTITY_MAP2.get(clazz.hashCode());
				if(entity != null)
				{
					if(entity.getTeam() != null && entity.getServer() != null)
					{
						PlayerTeam team = entity.getServer().getScoreboard().getPlayerTeam(entity.getTeam().getName());
						if (team != null)
						{entity.getServer().getScoreboard().addPlayerToTeam(p_8837_.getStringUUID(), team);}
					}
				}
			}
		}
	}

	@SuppressWarnings("removal")
	private static class MySecurityManager extends SecurityManager
	{
		public Class<?>[] getContext()
		{
			return this.getClassContext();
		}
	}
}