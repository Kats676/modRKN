package com.yourname.rknmod.events;

import com.yourname.rknmod.system.RknCallManager;
import com.yourname.rknmod.system.RknEconomyManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "rknmod")
public class RknChatEvent {

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        java.util.UUID uuid = player.getUUID();

        // Если игрок в этот момент разговаривает с Мамой по телефону
        if (RknCallManager.PLAYERS_TALKING_TO_MOM.contains(uuid)) {
            RknCallManager.PLAYERS_TALKING_TO_MOM.remove(uuid); // Завершаем режим звонка

            // Мама шлет перевод на булку в ответ на любое сообщение
            player.sendSystemMessage(Component.literal("§d[Мама]: §f«Хорошо сыночка! Я тебе скину 30 рублей на булочку!»"));
            RknEconomyManager.changeBalance(player, 30);
        }
    }
}
