package com.yourname.rknmod.events;

import com.yourname.rknmod.system.RknBlockManager;
import com.yourname.rknmod.system.RknWarningManager;
import com.yourname.rknmod.system.VpnManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "rknmod")
public class RknBlockEvents {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockState state = event.getState();
        
        // Если блок находится в черном списке и у игрока НЕ запущен ВПН
        if (RknBlockManager.BANNED_BLOCKS.contains(state.getBlock()) && !VpnManager.isVpnActive(event.getPlayer())) {
            event.setCanceled(true); // Отменяем ломание блока
            
            String blockName = state.getBlock().getName().getString();
            // Выдаем предупреждение (1-е, 2-е или кик на 3-е)
            RknWarningManager.addWarning(event.getPlayer(), blockName);
        }
    }
}
