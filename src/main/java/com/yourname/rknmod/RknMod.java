package com.yourname.rknmod;

import com.yourname.rknmod.item.ModItems;
import com.yourname.rknmod.commands.RknCallCommand;
import com.yourname.rknmod.commands.RknSellCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("rknmod")
public class RknMod {
    public RknMod() {
        // Регистрируем предметы при старте мода
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModItems.registerItems();

        // Регистрируем обработчики событий сервера
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Базовая настройка
    }

    // Регистрация кастомных команд чата (/sellkr и скрытой /rkncall)
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        RknCallCommand.register(event.getDispatcher());
        RknSellCommand.register(event.getDispatcher());
    }
}
