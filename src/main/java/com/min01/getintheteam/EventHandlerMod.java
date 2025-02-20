package com.min01.getintheteam;

import com.min01.getintheteam.network.PacketHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = Getintheteam.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandlerMod {

    public static final Logger logger = LogManager.getLogger();

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event){
        logger.info("commonSetup for network MAIN");
        event.enqueueWork(PacketHandler::registerSPackets);
    }

}
