package com.yourname.rknmod.system;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class MaxSpyManager {
    private static int spyTickCounter = 0;
    private static final int FIVE_MINUTES_TICKS = 6000; 

    public static void serverTick(ServerLevel level) {
        if (RknMainStatus.isRknDeleted) return;

        spyTickCounter++;
        if (spyTickCounter >= FIVE_MINUTES_TICKS) {
            spyTickCounter = 0;
            sendSpyCoordinates(level);
        }
    }

    private static void sendSpyCoordinates(ServerLevel level) {
        for (java.util.UUID uuid : RknWarningManager.WATCHED_PLAYERS) {
            ServerPlayer player = level.getPlayerByUUID(uuid);
            if (player != null) {
                int x = (int) player.getX();
                int y = (int) player.getY();
                int z = (int) player.getZ();
                String coordsString = "X: " + x + " Y: " + y + " Z: " + z;

                player.sendSystemMessage(Component.literal("§6§lМакс:"));
                player.sendSystemMessage(Component.literal("§e«Макс узнал ваши координаты они: §b" + coordsString + "§e»"));
            }
        }
    }
}
