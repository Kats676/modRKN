package com.yourname.rknmod.system;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RknBlockManager {
    public static final Set<Block> BANNED_BLOCKS = new HashSet<>();
    private static final Random RANDOM = new Random();
    private static int tickCounter = 0;
    
    // 5 минут = 6000 тиков (20 тиков = 1 секунда)
    private static final int FIVE_MINUTES_TICKS = 6000; 

    public static void serverTick(ServerLevel level) {
        if (RknMainStatus.isRknDeleted) return;
        
        tickCounter++;
        if (tickCounter >= FIVE_MINUTES_TICKS) {
            tickCounter = 0;
            banRandomBlock(level);
        }
    }

    private static void banRandomBlock(ServerLevel level) {
        var blockRegistry = BuiltInRegistries.BLOCK;
        int size = blockRegistry.size();
        
        Block randomBlock = blockRegistry.byId(RANDOM.nextInt(size));
        
        if (randomBlock == Blocks.AIR || randomBlock == Blocks.BEDROCK) {
            return;
        }

        BANNED_BLOCKS.add(randomBlock);
        String blockName = randomBlock.getName().getString();

        level.players().forEach(player -> {
            // Вывод заголовка вверху экрана
            player.sendSystemMessage(Component.literal("§c§lРоскомнадзор принял решение"));
            // Сообщение в чат
            player.sendSystemMessage(Component.literal("§eБлок §6" + blockName + " §eзаблокирован навсегда на территории РФ!"));
        });
    }
}
