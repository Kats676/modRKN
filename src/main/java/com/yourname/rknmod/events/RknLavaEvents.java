package com.yourname.rknmod.events;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "rknmod")
public class RknLavaEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            ServerPlayer player = (ServerPlayer) event.player;

            // Если игрок залез в лаву
            if (player.isInLava()) {
                player.sendSystemMessage(Component.literal("§c§lРоскомнадзор принял решение"));
                // Мгновенный кик из мира
                player.connection.disconnect(Component.literal(
                    "§c§lПринудительное решение умереть Запрещено на территории РФ по закону №138"
                ));
            }
        }
    }
}
