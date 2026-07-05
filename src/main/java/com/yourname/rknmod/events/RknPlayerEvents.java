package com.yourname.rknmod.events;

import com.yourname.rknmod.system.RknCourtManager;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.player.Player;

@Mod.EventBusSubscriber(modid = "rknmod")
public class RknPlayerEvents {

    @SubscribeEvent
    public static void onPlayerUpdate(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide()) {
            // Если игрок сидит в тюрьме
            if (RknCourtManager.PRISON_TIMERS.containsKey(player.getUUID())) {
                // Обнуляем горизонтальную скорость, лишая возможности ходить
                player.setDeltaMovement(0, player.getDeltaMovement().y < 0 ? player.getDeltaMovement().y : 0, 0);
            }
        }
    }
}
