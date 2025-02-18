package com.min01.getintheteam;

import com.min01.getintheteam.items.ItemRegisterHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Getintheteam.MODID)
public class Getintheteam 
{
	public static final String MODID = "getintheteam";
	public static final Logger logger = LogManager.getLogger();
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
		if(entity.getTeam() != null)
		{
			PlayerTeam team = entity.getServer().getScoreboard().getPlayerTeam(entity.getTeam().getName());
			if (team != null)
			{
				entity.getServer().getScoreboard().addPlayerToTeam(entity.getStringUUID(), team);}
			else {logger.info("Entity have no team");}
		}

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
		if(player.getTeam() != null) {
				player.getServer().getScoreboard().addPlayerToTeam(entity.getStringUUID(), player.getServer().getScoreboard().getPlayerTeam(player.getTeam().getName()));
				player.sendSystemMessage(Component.literal("Entity added to your team").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE));
		}
		else{
			player.sendSystemMessage(Component.literal("You dont have a team").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE));
		}

	}
	public static void RemoveFromTeam(Entity entity, Player player){
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

		if (entity.getTeam() != null)
		{
			if(player.getTeam() != null) {
				player.getServer().getScoreboard().removePlayerFromTeam(entity.getStringUUID());
				player.sendSystemMessage(Component.literal("Entity remove from your team").withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE));
			}
			else{
				player.sendSystemMessage(Component.literal("You dont have a team").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE));
			}
		}
		else{
			player.sendSystemMessage(Component.literal("Entity not in team").withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE));
		}
	}
}
