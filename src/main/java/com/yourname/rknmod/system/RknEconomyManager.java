package com.yourname.rknmod.system;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RknEconomyManager {
    private static final Map<UUID, Integer> PLAYER_BALANCES = new HashMap<>();

    public static int getBalance(Player player) {
        return PLAYER_BALANCES.getOrDefault(player.getUUID(), 0);
    }

    public static void changeBalance(Player player, int amount) {
        if (player.level().isClientSide()) return;
        
        int currentBalance = getBalance(player);
        int newBalance = currentBalance + amount;
        PLAYER_BALANCES.put(player.getUUID(), newBalance);

        player.sendSystemMessage(Component.literal("§7[Кошелек] Ваш баланс: §6" + newBalance + " руб."));

        if (newBalance <= -10) {
            player.sendSystemMessage(Component.literal("§c§lРоскомнадзор принял решение"));
            player.sendSystemMessage(Component.literal("§c[РКН] Вы стали Должником! За вами выехали сотрудники по выбиванию долгов."));
        }
    }

    public static boolean isDebtor(Player player) {
        return getBalance(player) <= -10;
    }
}
