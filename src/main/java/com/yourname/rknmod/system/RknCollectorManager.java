package com.yourname.rknmod.system;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.Piglin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RknCollectorManager {
    private static final Map<UUID, Integer> PLAYER_SPAWN_TIMERS = new HashMap<>();

    public static void serverTick(ServerLevel level) {
        if (RknMainStatus.isRknDeleted) return;

        for (ServerPlayer player : level.players()) {
            UUID uuid = player.getUUID();
            int balance = RknEconomyManager.getBalance(player);

            if (balance <= -10) {
                int maxIntervalTicks = getSpawnIntervalTicks(balance);
                int currentTicks = PLAYER_SPAWN_TIMERS.getOrDefault(uuid, 0) + 1;

                if (currentTicks >= maxIntervalTicks) {
                    PLAYER_SPAWN_TIMERS.put(uuid, 0);
                    spawnPiglinCollectors(level, player);
                } else {
                    PLAYER_SPAWN_TIMERS.put(uuid, currentTicks);
                }
            } else {
                PLAYER_SPAWN_TIMERS.remove(uuid);
            }
        }
    }

    private static int getSpawnIntervalTicks(int balance) {
        if (balance < -100) return 30 * 20; // 30 секунд
        if (balance <= -100) return 90 * 20; // 1 минута 30 секунд
        if (balance <= -50) return 3 * 60 * 20; // 3 минуты
        return 5 * 60 * 20; // 5 минут
    }

    private static void spawnPiglinCollectors(ServerLevel level, ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§6§lПиглины: §c«Отдавай деньги! Или будет больно!»"));
        BlockPos playerPos = player.blockPosition();

        for (int i = 0; i < 3; i++) {
            BlockPos spawnPos = playerPos.offset(level.random.nextInt(5) - 2, 0, level.random.nextInt(5) - 2);
            Piglin collector = EntityType.PIGLIN.create(level);
            if (collector != null) {
                collector.moveTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.0F, 0.0F);
                collector.setCustomName(Component.literal("§6(чел который должен выбить деньги)"));
                collector.setCustomNameVisible(true);
                collector.setImmuneToZombification(true);
                collector.setTarget(player);
                level.addFreshEntity(collector);
            }
        }
    }
}
