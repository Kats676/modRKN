package com.yourname.rknmod.events;

import com.yourname.rknmod.system.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "rknmod")
public class RknServerEvents {

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        // Если РКН удален (игрок убил дракона и нажал кнопку), полностью отключаем таймеры ограничения
        if (RknMainStatus.isRknDeleted) {
            RknBlockManager.BANNED_BLOCKS.clear();
            return; 
        }

        // Запускаем все проверки в конце серверного тика в основном мире
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel serverLevel) {
            RknBlockManager.serverTick(serverLevel);
            MaxSpyManager.serverTick(serverLevel);
            VpnManager.tick();
            RknCollectorManager.serverTick(serverLevel);
            RknCallManager.serverTick(serverLevel);
            RknCourtManager.serverTick(serverLevel);
        }
    }
}
