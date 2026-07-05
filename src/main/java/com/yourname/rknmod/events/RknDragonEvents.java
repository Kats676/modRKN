package com.yourname.rknmod.events;

import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "rknmod")
public class RknDragonEvents {
    public static boolean isDragonDefeated = false;

    @SubscribeEvent
    public static void onDragonDeath(LivingDeathEvent event) {
        Entity victim = event.getEntity();

        if (victim instanceof EnderDragon) {
            isDragonDefeated = true;
            
            victim.level().players().forEach(player -> {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§a§l[СИСТЕМА] Эндер Дракон повержен! Функция удаления РКН теперь доступна в Shop!"
                ));
            });
        }
    }
}
