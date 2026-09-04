package com.min01.getintheteam;

import com.min01.getintheteam.items.ItemRegisterHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Getintheteam.MODID)
public class Getintheteam 
{
	public static final String MODID = "getintheteam";
	public static final Logger logger = LogManager.getLogger();

	// Remembers which team each entity class was added to, so re-spawned entities auto-join
	private static final Map<Class<?>, String> CLASS_TEAM_MAP = new HashMap<>();
	public Getintheteam()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		ItemRegisterHandler.register(modEventBus);

		modEventBus.addListener(this::commonSetup);

		MinecraftForge.EVENT_BUS.register(this);

		modEventBus.addListener(this::addCreative);

	}

	private void commonSetup(FMLCommonSetupEvent event) {

	}

	private void addCreative(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(ItemRegisterHandler.FLAG);
		}
	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event){

	}

	public static String IntArrayToUUID(int[] intArray) {
		if(intArray.length != 4) {
			return "Not UUID";
		}
        return String.format("%08x-%04x-%04x-%04x-%04x%08x",
				(long) intArray[0] & 0xFFFFFFFFL,
				(long) intArray[1] >> 16 & 0xFFFFL,
				(long) intArray[1] & 0xFFFFL,
				(long) intArray[2] >> 16 & 0xFFFFL,
				(long) intArray[2] & 0xFFFFL,
				(long) intArray[3] & 0xFFFFFFFFL);
	}

	public static void AddToTeam(Entity entity){
		if (entity.getServer() == null) {
			return;
		}
		PlayerTeam team = FindEntityTeam(entity);
		if (team != null)
		{
			entity.getServer().getScoreboard().addPlayerToTeam(entity.getStringUUID(), team);
			// Remember this class -> team so future re-spawns auto-join
			CLASS_TEAM_MAP.put(entity.getClass(), team.getName());
		}
		else {logger.info("Entity have no team");}

	}

	public static void AddToTeam(Entity entity, Player player){
		if (entity.getServer() == null) {
			return;
		}

		String ownerUUID;
		if (entity.getType().toString().contains("entity.modulargolems"))
		{
			ownerUUID = entity.serializeNBT().getCompound("auto-serial").getString("owner");
		}
		else{
			ownerUUID = IntArrayToUUID(entity.serializeNBT().getIntArray("Owner"));
		}

		if (!ownerUUID.equals(player.getStringUUID())) {
			player.sendSystemMessage(Component.literal("You are not the owner").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE));
			return;
		}
		PlayerTeam team = FindPlayerTeam(player);
		if(team != null) {
				player.getServer().getScoreboard().addPlayerToTeam(entity.getStringUUID(), team);
				// Remember this class -> team so future re-spawns auto-join
				RegisterClassTeam(entity.getClass(), team);
				player.sendSystemMessage(Component.literal("Entity added to your team").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE));
		}
		else{
			Getintheteam.logger.warn("AddToTeam: no team found for {} on server scoreboard", player.getScoreboardName());
			player.sendSystemMessage(Component.literal("You dont have a team").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE));
		}

	}
	public static void RemoveFromTeam(Entity entity, Player player){
		if (entity.getServer() == null) {
			return;
		}

		// Resolve the entity's team robustly (same data /team list shows)
		PlayerTeam entityTeam = FindEntityTeam(entity);

		// If the entity is a player, just remove them from the team directly
		if (entity instanceof Player) {
			if (entityTeam != null && FindPlayerTeam(player) != null) {
				// Players are usually registered by name
				String memberKey = entityTeam.getPlayers().contains(entity.getScoreboardName())
						? entity.getScoreboardName() : entity.getStringUUID();
				player.getServer().getScoreboard().removePlayerFromTeam(memberKey);
				player.sendSystemMessage(Component.literal("Player removed from your team").withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE));
			} else {
				player.sendSystemMessage(Component.literal("Player not in team").withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE));
			}
			return;
		}

		String ownerUUID;
		if (entity.getType().toString().contains("entity.modulargolems"))
		{
			ownerUUID = entity.serializeNBT().getCompound("auto-serial").getString("owner");
		}
		else{
			ownerUUID = IntArrayToUUID(entity.serializeNBT().getIntArray("Owner"));
		}

		if (!ownerUUID.equals(player.getStringUUID())) {
			player.sendSystemMessage(Component.literal("You are not the owner").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE));
			return;
		}

		if (entityTeam != null)
		{
			if(FindPlayerTeam(player) != null) {
				// Remove by whichever key the team actually uses (UUID or scoreboard name)
				String memberKey = entityTeam.getPlayers().contains(entity.getStringUUID())
						? entity.getStringUUID() : entity.getScoreboardName();
				player.getServer().getScoreboard().removePlayerFromTeam(memberKey);
				player.sendSystemMessage(Component.literal("Entity remove from your team").withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE));
			}
			else{
				player.sendSystemMessage(Component.literal("You not in a team").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE));
			}
		}
		else{
			player.sendSystemMessage(Component.literal("Entity not in team").withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE));
		}
	}

	/**
	 * Robustly finds the player's team on the server scoreboard.
	 * Tries the standard lookup, then UUID, then scans every team -
	 * this way the result always matches what /team list shows.
	 */
	@Nullable
	public static PlayerTeam FindPlayerTeam(Player player) {
		MinecraftServer server = player.getServer();
		if (server == null) return null;
		Scoreboard scoreboard = server.getScoreboard();

		// 1. Standard lookup by scoreboard member name
		PlayerTeam team = scoreboard.getPlayersTeam(player.getScoreboardName());
		if (team != null) return team;

		// 2. Some setups register players by UUID instead of name
		team = scoreboard.getPlayersTeam(player.getStringUUID());
		if (team != null) return team;

		// 3. Brute-force: scan every team for our name or UUID
		for (PlayerTeam t : scoreboard.getPlayerTeams()) {
			if (t.getPlayers().contains(player.getScoreboardName())
					|| t.getPlayers().contains(player.getStringUUID())) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Robustly finds an entity's team on the server scoreboard.
	 * Tries the standard lookup, then UUID, then scans every team's member list -
	 * this way the result always matches what /team list shows.
	 */
	@Nullable
	public static PlayerTeam FindEntityTeam(Entity entity) {
		MinecraftServer server = entity.getServer();
		if (server == null) return null;
		Scoreboard scoreboard = server.getScoreboard();

		// 1. Standard lookup by the entity's scoreboard name (its UUID for most entities)
		PlayerTeam team = scoreboard.getPlayersTeam(entity.getScoreboardName());
		if (team != null) return team;

		// 2. Fallback: lookup by UUID string
		team = scoreboard.getPlayersTeam(entity.getStringUUID());
		if (team != null) return team;

		// 3. Brute-force: scan every team's member list
		for (PlayerTeam t : scoreboard.getPlayerTeams()) {
			if (t.getPlayers().contains(entity.getScoreboardName())
					|| t.getPlayers().contains(entity.getStringUUID())) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Registers an entity class to a team name so re-spawned entities of the
	 * same class automatically join the team (see FindClassTeam).
	 */
	public static void RegisterClassTeam(Class<?> clazz, PlayerTeam team) {
		if (clazz != null && team != null) {
			CLASS_TEAM_MAP.put(clazz, team.getName());
		}
	}

	/**
	 * Finds the team registered for this entity's class (or one of its superclasses).
	 * This is what makes auto-add work: when a maid/golem re-spawns with a new UUID,
	 * its class is still registered, so it joins the team automatically.
	 */
	@Nullable
	public static PlayerTeam FindClassTeam(Entity entity) {
		MinecraftServer server = entity.getServer();
		if (server == null) return null;
		Scoreboard scoreboard = server.getScoreboard();

		Class<?> clazz = entity.getClass();
		while (clazz != null && clazz != Object.class) {
			String teamName = CLASS_TEAM_MAP.get(clazz);
			if (teamName != null) {
				PlayerTeam team = scoreboard.getPlayerTeam(teamName);
				if (team != null) return team;
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}
}
