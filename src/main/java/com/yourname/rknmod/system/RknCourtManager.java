package com.yourname.rknmod.system;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RknCourtManager {
    public static final Map<UUID, Integer> PRISON_TIMERS = new HashMap<>();

    public static void serverTick(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            UUID uuid = player.getUUID();
            if (PRISON_TIMERS.containsKey(uuid)) {
                int timeLeft = PRISON_TIMERS.get(uuid) - 1;
                if (timeLeft <= 0) {
                    PRISON_TIMERS.remove(uuid);
                    player.sendSystemMessage(Component.literal("§a[Суд] Ваш срок заключения истек. Вы свободны!"));
                } else {
                    PRISON_TIMERS.put(uuid, timeLeft);
                    if (timeLeft % 100 == 0) {
                        player.sendSystemMessage(Component.literal("§c[Тюрьма] Вы обездвижены. Осталось: " + (timeLeft / 20) + " сек."));
                    }
                }
            }
        }
    }

    public static void applyVerdict(ServerPlayer player, int score) {
        player.sendSystemMessage(Component.literal("§c§lСуд оглашает вердикт:"));

        if (score == 3) {
            player.sendSystemMessage(Component.literal("§a«Вы ответили на все вопросы верно! Вы полностью оправданы.»"));
        } 
        else if (score == 2) {
            player.sendSystemMessage(Component.literal("§e«Вы совершили 1 ошибку. Суд назначает штраф 15 рублей.»"));
            RknEconomyManager.changeBalance(player, -15);
        } 
        else if (score == 1) {
            player.sendSystemMessage(Component.literal("§c«Вы ответили неверно на 2 вопроса! Тюрьма на 1 минуту за убийство младенца.»"));
            PRISON_TIMERS.put(player.getUUID(), 1200);
        } 
        else {
            player.sendSystemMessage(Component.literal("§4«Все ответы неверны! Максимальное наказание: Тюрьма на 3 минуты без права движения!»"));
            PRISON_TIMERS.put(player.getUUID(), 3600);
        }
    }
}
