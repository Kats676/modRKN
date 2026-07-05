package com.yourname.rknmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.yourname.rknmod.system.RknEconomyManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Pillager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RknSellCommand {
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sellkr")
            .executes(context -> {
                ServerPlayer player = context.getSource().getPlayerOrException();
                ServerLevel level = player.serverLevel();
                
                int x = (int) player.getX();
                int y = (int) player.getY();
                int z = (int) player.getZ();
                String coordsString = "X: " + x + " Y: " + y + " Z: " + z;

                player.sendSystemMessage(Component.literal("§d[Незнакомец] §a«Отлично, твои координаты (" + coordsString + ") у меня. Вот твои 15 рублей!»"));
                RknEconomyManager.changeBalance(player, 15);

                player.sendSystemMessage(Component.literal("§7§o(про себя) Я слышу шуршание в кустах…? Что там?"));

                java.util.UUID playerUuid = player.getUUID();

                SCHEDULER.schedule(() -> {
                    level.getServer().execute(() -> {
                        ServerPlayer currentPlayer = level.getPlayerByUUID(playerUuid);
                        
                        if (currentPlayer != null) {
                            BlockPos playerPos = currentPlayer.blockPosition();
                            currentPlayer.sendSystemMessage(Component.literal("§c§l[ЗАСАДА] §cКоординаты были слиты! На вас напали!"));

                            for (int i = 0; i < 2; i++) {
                                BlockPos spawnPos = playerPos.offset(level.random.nextInt(4) - 2, 0, level.random.nextInt(4) - 2);
                                Pillager assassin = EntityType.PILLAGER.create(level);
                                if (assassin != null) {
                                    assassin.moveTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.0F, 0.0F);
                                    assassin.setCustomName(Component.literal("§dПодмога от незнакомца"));
                                    assassin.setCustomNameVisible(true);
                                    assassin.setTarget(currentPlayer);
                                    level.addFreshEntity(assassin);
                                }
                            }
                        }
                    });
                }, 30, TimeUnit.SECONDS);

                return 1;
            })
        );
    }
}
