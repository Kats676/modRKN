package com.yourname.rknmod.system;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RknQuestManager {
    private static int globalTimer = 0;
    private static final int TEN_MINUTES_TICKS = 12000; 

    public static final Map<UUID, Integer> PLAYER_QUEST_STAGE = new HashMap<>();
    public static final Map<UUID, Integer> PLAYER_QUEST_PROGRESS = new HashMap<>();
    private static final Map<UUID, Integer> PLAYER_TASK_TIMER = new HashMap<>();

    public static void serverTick(ServerLevel level) {
        if (RknMainStatus.isRknDeleted) return;

        globalTimer++;
        if (globalTimer >= TEN_MINUTES_TICKS) {
            globalTimer = 0;
            spawnPovestkaMan(level);
        }

        for (ServerPlayer player : level.players()) {
            UUID uuid = player.getUUID();
            if (PLAYER_QUEST_STAGE.getOrDefault(uuid, 0) > 0) {
                int timeLeft = PLAYER_TASK_TIMER.getOrDefault(uuid, 6000) - 1;
                if (timeLeft <= 0) {
                    PLAYER_TASK_TIMER.put(uuid, 6000);
                    RknEconomyManager.changeBalance(player, -35);
                    player.sendSystemMessage(Component.literal("§c[Повестка] Вы не успели выполнить задание за 5 минут! Штраф 35 рублей."));
                } else {
                    PLAYER_TASK_TIMER.put(uuid, timeLeft);
                }
            }
        }
    }

    private static void spawnPovestkaMan(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            // Программная выдача предмета повестки (бумаги)
            player.getInventory().add(new ItemStack(Items.PAPER));

            BlockPos pos = player.blockPosition().offset(level.random.nextInt(3) - 1, 0, level.random.nextInt(3) - 1);
            Villager npc = EntityType.VILLAGER.create(level);
            if (npc != null) {
                npc.moveTo(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
                npc.setCustomName(Component.literal("§c§lПовестка"));
                npc.setCustomNameVisible(true);
                npc.setNoAi(true);
                level.addFreshEntity(npc);
                player.sendSystemMessage(Component.literal("§c[Повестка] Привет тебя вызвали на повестку! Кликни на меня чтобы подписать!"));
            }
        }
    }

    public static void startQuest(ServerPlayer player) {
        UUID uuid = player.getUUID();
        if (PLAYER_QUEST_STAGE.getOrDefault(uuid, 0) == 0) {
            PLAYER_QUEST_STAGE.put(uuid, 1);
            PLAYER_QUEST_PROGRESS.put(uuid, 0);
            PLAYER_TASK_TIMER.put(uuid, 6000);

            player.sendSystemMessage(Component.literal("§c[Повестка] Вы подписали повестку."));
            player.sendSystemMessage(Component.literal("§c[Повестка] Ваше 1 задание: Накопать 50 булыжников"));
        }
    }

    public static void advanceQuest(ServerPlayer player, String blockType) {
        UUID uuid = player.getUUID();
        int stage = PLAYER_QUEST_STAGE.getOrDefault(uuid, 0);
        int progress = PLAYER_QUEST_PROGRESS.getOrDefault(uuid, 0) + 1;

        if (stage == 1 && blockType.equals("cobblestone")) {
            if (progress >= 50) {
                PLAYER_QUEST_STAGE.put(uuid, 2);
                PLAYER_QUEST_PROGRESS.put(uuid, 0);
                PLAYER_TASK_TIMER.put(uuid, 6000);
                player.sendSystemMessage(Component.literal("§c[Повестка] Хорошо, 2 задание накопать 15 золота"));
            } else {
                PLAYER_QUEST_PROGRESS.put(uuid, progress);
            }
        } else if (stage == 2 && blockType.equals("gold_ore")) {
            if (progress >= 15) {
                PLAYER_QUEST_STAGE.put(uuid, 3);
                PLAYER_QUEST_PROGRESS.put(uuid, 0);
                PLAYER_TASK_TIMER.put(uuid, 6000);
                player.sendSystemMessage(Component.literal("§c[Повестка] Хорошо последнее задание сделать 3 кровати"));
            } else {
                PLAYER_QUEST_PROGRESS.put(uuid, progress);
            }
        }
    }
}
