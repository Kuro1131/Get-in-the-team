package com.min01.getintheteam;

import java.util.HashMap;
import java.util.Map;

import com.min01.getintheteam.items.item.FlagItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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
				// Auto-add: if this entity's class was registered to a team, join it.
				// This is what makes re-spawned maids/golems (new UUID) rejoin the team.
				PlayerTeam classTeam = Getintheteam.FindClassTeam(event.getEntity());
				if (classTeam != null) {
					event.getEntity().getServer().getScoreboard().addPlayerToTeam(event.getEntity().getStringUUID(), classTeam);
				} else {
					// Otherwise re-add to its own persisted team (and register the class)
					Getintheteam.AddToTeam(event.getEntity());
				}
			}
			ENTITY_MAP.put(event.getEntity().getClass().hashCode(), event.getEntity());
			ENTITY_MAP2.put(event.getEntity().getClass().getSuperclass().hashCode(), event.getEntity());
		}

	}

	/**
	 * Makes the Flag item take priority over the entity's own right-click behavior.
	 * Vanilla calls entity.interact() BEFORE item.interactLivingEntity(), so modded
	 * entities like the saintdragon (riding) consume the interaction first. This
	 * event fires before entity.interact() and cancels it when the flag is held.
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		ItemStack stack = event.getItemStack();
		if (stack.getItem() instanceof FlagItem) {
			Player player = event.getEntity();
			Entity target = event.getTarget();
			if (player.isCrouching()) {
				Getintheteam.RemoveFromTeam(target, player);
			} else {
				Getintheteam.AddToTeam(target, player);
			}
			event.setCanceled(true);
		}
	}
}