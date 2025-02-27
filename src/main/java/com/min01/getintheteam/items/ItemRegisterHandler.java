package com.min01.getintheteam.items;

import com.min01.getintheteam.Getintheteam;
import com.min01.getintheteam.items.item.BandageItem;
import com.min01.getintheteam.items.item.FlagItem;
import com.min01.getintheteam.items.item.GoldenBandageItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegisterHandler {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Getintheteam.MODID);

    public static final RegistryObject<Item> FLAG = ITEMS.register("flag", () -> new FlagItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BANDAGE = ITEMS.register("bandage", () -> new BandageItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> GOLDEN_BANDAGE = ITEMS.register("golden_bandage", () -> new GoldenBandageItem(new Item.Properties().stacksTo(64)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
