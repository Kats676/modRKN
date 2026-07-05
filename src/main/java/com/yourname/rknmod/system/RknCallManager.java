package com.yourname.rknmod.system;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RknCallManager {
    private static final Map<UUID, Integer> INDIVIDUAL_CALL_TIMERS = new HashMap<>();
    public static final Set<UUID> MOM_HUNTING_PLAYERS = new HashSet<>();
    private static final Map<UUID, Integer> MOM_MISSED_CALLS = new HashMap<>();
    public static final Set<UUID> PLAYERS_TALKING_TO_MOM = new HashSet<>();

    public static void serverTick(ServerLevel level) {
        if (RknMainStatus.isRknDeleted) return;

        for (ServerPlayer player : level.players()) {
            UUID uuid = player.getUUID();
            boolean isHunting = MOM_HUNTING_PLAYERS.contains(uuid);

            int interval = isHunting ? 600 : 3600; // 30 сек или 3 минуты
            int currentTicks = INDIVIDUAL_CALL_TIMERS.getOrDefault(uuid, 0) + 1;

            if (currentTicks >= interval) {
                INDIVIDUAL_CALL_TIMERS.put(uuid, 0);

                if (isHunting) {
                    int missed = MOM_MISSED_CALLS.getOrDefault(uuid, 0) + 1;
                    MOM_MISSED_CALLS.put(uuid, missed);

                    if (missed >= 3) {
                        MOM_HUNTING_PLAYERS.remove(uuid);
                        MOM_MISSED_CALLS.remove(uuid);
                        player.connection.disconnect(Component.literal("§c§lМама нашла вас, и дала сковородкой по голове"));
                        continue;
                    }
                    sendMomCall(player);
                } else {
                    int randomChoice = level.random.nextInt(3);
                    if (randomChoice == 0) sendRknCall(player);
                    else if (randomChoice == 1) sendMomCall(player);
                    else sendStrangerSms(player);
                }
            } else {
                INDIVIDUAL_CALL_TIMERS.put(uuid, currentTicks);
            }
        }
    }

    private static void sendRknCall(ServerPlayer player) {
        boolean hasMax = RknWarningManager.WATCHED_PLAYERS.contains(player.getUUID());
        Component callText = Component.literal("§6[Телефон] Вам звонит номер +7 777 777 77 77 ");

        if (hasMax) {
            Component answerBtn = Component.literal("§a§l[ОТВЕТИТЬ]").withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rkncall answer")));
            Component ignoreBtn = Component.literal(" §c§l[НЕ ОТВЕЧАТЬ]").withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rkncall ignore")));
            player.sendSystemMessage(callText.copy().append(answerBtn).append(ignoreBtn));
        } else {
            Component noMaxText = Component.literal("§7(Скачать Макс для Звонков) ");
            Component declineBtn = Component.literal("§c§l[ОТКЛОНИТЬ]").withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rkncall decline")));
            player.sendSystemMessage(callText.copy().append(noMaxText).append(declineBtn));
        }
    }

    private static void sendMomCall(ServerPlayer player) {
        Component callText = Component.literal("§6[Телефон] Вам звонит номер §d§l«Мама» ");
        Component answerBtn = Component.literal("§a§l[ОТВЕТИТЬ]").withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rkncall mom_answer")));
        Component ignoreBtn = Component.literal(" §c§l[НЕ ОТВЕЧАТЬ]").withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rkncall mom_ignore")));
        player.sendSystemMessage(callText.copy().append(answerBtn).append(ignoreBtn));
    }

    private static void sendStrangerSms(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§d[Незнакомец] §f«Псс... продать свои координаты за 15 рублей? Введи команду §b/sellkr§f»"));
    }

    public static void clearMomStatus(UUID uuid) {
        MOM_HUNTING_PLAYERS.remove(uuid);
        MOM_MISSED_CALLS.remove(uuid);
    }
}
