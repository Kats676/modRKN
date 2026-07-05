package com.yourname.rknmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.yourname.rknmod.system.RknEconomyManager;
import com.yourname.rknmod.system.RknCallManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class RknCallCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("rkncall")
            .then(Commands.argument("action", StringArgumentType.word())
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    String action = StringArgumentType.getString(context, "action");

                    if ("answer".equals(action)) {
                        player.sendSystemMessage(Component.literal("§c§lРоскомнадзор принял решение"));
                        player.sendSystemMessage(Component.literal("§c«Это была проверка! Не отвечайте на не знакомые номера! Штраф 10р»"));
                        RknEconomyManager.changeBalance(player, -10);
                    } 
                    else if ("ignore".equals(action)) {
                        player.sendSystemMessage(Component.literal("§7[Телефон] Вы проигнорировали звонок. Он занесен в пропущенные."));
                    } 
                    else if ("decline".equals(action)) {
                        player.sendSystemMessage(Component.literal("§c[Телефон] Вы сбросили вызов. Купите Макс, чтобы говорить!"));
                    }
                    else if ("mom_answer".equals(action)) {
                        player.sendSystemMessage(Component.literal("§6[Телефон] Вы ответили Маме."));
                        player.sendSystemMessage(Component.literal("§d[Мама]: §f«Сыночка все хорошо?»"));
                        RknCallManager.clearMomStatus(player.getUUID());
                        RknCallManager.PLAYERS_TALKING_TO_MOM.add(player.getUUID());
                    } 
                    else if ("mom_ignore".equals(action)) {
                        player.sendSystemMessage(Component.literal("§d§l[Статус] МАМА ИЩЕТ ВАС!"));
                        player.sendSystemMessage(Component.literal("§c[Телефон] Вы не ответили Маме! Она начинает звонить каждую минуту и очень зла."));
                        RknCallManager.MOM_HUNTING_PLAYERS.add(player.getUUID());
                    }

                    return 1;
                })
            )
        );

        // Дополнительная скрытая команда для отправки результатов судебного теста
        dispatcher.register(Commands.literal("rkncourt_finish")
            .then(Commands.argument("score", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0, 3))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    int score = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "score");
                    com.yourname.rknmod.system.RknCourtManager.applyVerdict(player, score);
                    return 1;
                })
            )
        );
    }
}
