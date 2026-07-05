package com.yourname.rknmod.system;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VpnManager {
    private static final Map<UUID, Integer> ACTIVE_VPNS = new HashMap<>();
    private static final Map<UUID, Integer> VPN_COOLDOWNS = new HashMap<>();
    private static final Map<UUID, Integer> VPN_USE_COUNT = new HashMap<>();

    public static void activateVpn(Player player) {
        if (player.level().isClientSide()) return;
        
        UUID uuid = player.getUUID();

        // Проверка кулдауна на 3 минуты
        if (VPN_COOLDOWNS.getOrDefault(uuid, 0) > 0) {
            int secondsLeft = VPN_COOLDOWNS.get(uuid) / 20;
            player.sendSystemMessage(Component.literal("§c[VPN] Ошибка! Подождите " + secondsLeft + " сек. перед повторным запуском."));
            return;
        }

        ACTIVE_VPNS.put(uuid, 200); // Работает 10 секунд
        VPN_COOLDOWNS.put(uuid, 3600); // Кулдаун 3 минуты (3600 тиков)

        int useCount = VPN_USE_COUNT.getOrDefault(uuid, 0) + 1;
        VPN_USE_COUNT.put(uuid, useCount);

        player.sendSystemMessage(Component.literal("§a[VPN] Соединение установлено! У вас есть 10 секунд."));

        if (useCount == 1) {
            player.sendSystemMessage(Component.literal("§7[VPN] Первое использование прошло незамеченным."));
        } 
        else if (useCount == 2) {
            player.sendSystemMessage(Component.literal("§c§lРоскомнадзор принял решение"));
            player.sendSystemMessage(Component.literal("§c[РКН] Зафиксировано использование ВПН! Штраф 10 рублей."));
            RknEconomyManager.changeBalance(player, -10);
        } 
        else if (useCount == 3) {
            player.sendSystemMessage(Component.literal("§7[VPN] Третье использование. Обход систем прошел успешно."));
        }
    }

    public static boolean isVpnActive(Player player) {
        return ACTIVE_VPNS.containsKey(player.getUUID()) && ACTIVE_VPNS.get(player.getUUID()) > 0;
    }

    public static void tick() {
        ACTIVE_VPNS.replaceAll((uuid, ticks) -> ticks - 1);
        ACTIVE_VPNS.entrySet().removeIf(entry -> entry.getValue() <= 0);

        VPN_COOLDOWNS.replaceAll((uuid, ticks) -> ticks - 1);
        VPN_COOLDOWNS.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }
}
