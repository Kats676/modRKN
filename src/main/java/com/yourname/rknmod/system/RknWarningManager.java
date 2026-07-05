package com.yourname.rknmod.system;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RknWarningManager {
    private static final Map<UUID, Integer> PLAYER_WARNINGS = new HashMap<>();
    // Список игроков, за которыми шпионит Макс
    public static final Set<UUID> WATCHED_PLAYERS = new HashSet<>();

    public static void addWarning(Player player, String blockName) {
        if (player.level().isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        UUID uuid = player.getUUID();
        int warnings = PLAYER_WARNINGS.getOrDefault(uuid, 0) + 1;
        PLAYER_WARNINGS.put(uuid, warnings);

        serverPlayer.sendSystemMessage(Component.literal("§c§lРоскомнадзор принял решение"));

        if (warnings == 1) {
            player.sendSystemMessage(Component.literal("§c[РКН] Вы вскопали блок §6" + blockName + "§c, который заблокирован. Роскомнадзор принял решение сделать вас подозрительным, и отправить повестку. §eСкачайте Макс чтобы, обнулить нарушение!"));
        } else if (warnings == 2) {
            player.sendSystemMessage(Component.literal("§c[РКН] Вы вскопали 2 раз блок §6" + blockName + "§c Роскомнадзор принял решение сделать вас Очень подозрительным, При 3 нарушение Мир заблокируется!"));
        } else if (warnings >= 3) {
            PLAYER_WARNINGS.put(uuid, 0);
            serverPlayer.connection.disconnect(Component.literal("§c§lРоскомнадзор принял решение выгнать вас из РФ."));
        }
    }

    public static boolean tryClearWarningWithMax(Player player) {
        UUID uuid = player.getUUID();
        int warnings = PLAYER_WARNINGS.getOrDefault(uuid, 0);

        if (warnings == 1) {
            PLAYER_WARNINGS.put(uuid, 0);
            WATCHED_PLAYERS.add(uuid); // Включаем слежку Макса
            return true;
        }
        return false;
    }

    public static int getWarningCount(Player player) {
        return PLAYER_WARNINGS.getOrDefault(player.getUUID(), 0);
    }

    public static String getPlayerStatus(Player player) {
        if (RknCallManager.MOM_HUNTING_PLAYERS.contains(player.getUUID())) {
            return "§4§lМАМА ИЩЕТ!";
        }
        if (RknEconomyManager.isDebtor(player)) {
            return "§cДолжник";
        }
        int warnings = PLAYER_WARNINGS.getOrDefault(player.getUUID(), 0);
        if (warnings == 1) return "§eПодозрительный";
        if (warnings == 2) return "§cОчень Подозрительный";
        return "§aЗаконопослушный";
    }
}
