package com.yourname.rknmod.events;

import com.yourname.rknmod.system.RknCourtManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "rknmod")
public class RknCourtEvents {

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        Entity victim = event.getEntity();
        Entity killer = event.getSource().getEntity();

        if (killer instanceof ServerPlayer player) {
            boolean isBaby = false;
            String victimName = victim.getType().getDescription().getString();

            if (victim instanceof AgeableMob animal && animal.isBaby()) {
                isBaby = true;
            }
            else if (victim instanceof Zombie zombie && zombie.isBaby()) {
                isBaby = true;
                victimName = "Зомби-младенца";
            }

            if (isBaby) {
                player.sendSystemMessage(Component.literal("§c§lСУД РОССИЙСКОЙ ФЕДЕРАЦИИ"));
                player.sendSystemMessage(Component.literal(
                    "§c«Вас вызвали на суд! За убийство младенца " + victimName + ". Суд решит через 1 минуту!»"
                ));
                RknCourtManager.startCourtCase(player);
            }
        }
    }
}
