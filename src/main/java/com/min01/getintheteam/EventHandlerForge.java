package com.min01.getintheteam;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = Getintheteam.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandlerForge
{
	public static final Map<Integer, Entity> ENTITY_MAP = new HashMap<>();
	public static final Map<Integer, Entity> ENTITY_MAP2 = new HashMap<>();
	public static final Logger logger = LogManager.getLogger();

	@SubscribeEvent
	public static void onEntityJoinLevel(EntityJoinLevelEvent event)
	{
		if (event.getEntity() instanceof Mob && event.getEntity().getServer() != null){
			String type = event.getEntity().getType().toString();
			if (!type.contains("minecraft")){
				Getintheteam.AddToTeam(event.getEntity());
			}
			ENTITY_MAP.put(event.getEntity().getClass().hashCode(), event.getEntity());
			ENTITY_MAP2.put(event.getEntity().getClass().getSuperclass().hashCode(), event.getEntity());
		}

	}

}
