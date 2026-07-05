package com.yourname.rknmod.events;

import com.yourname.rknmod.system.RknQuestManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "rknmod")
public class RknQuestEvents {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.getLevel().isClientSide() && event.getTarget() instanceof Villager npc) {
            // Если игрок кликнул по НПС с именем Повестка
            if (npc.hasCustomName() && npc.getCustomName().getString().contains("Повестка")) {
                RknQuestManager.startQuest((ServerPlayer) event.getEntity());
                npc.discard(); // Чел выполнил задачу и исчезает
            }
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            ItemStack item = event.getCrafting();
            
            // Если игрок скрафтил любую кровать
            if (item.getItem().getDescriptionId().contains("bed")) {
                ServerPlayer player = (ServerPlayer) event.getEntity();
                java.util.UUID uuid = player.getUUID();
                
                // Проверяем, находится ли он на 3-м задании военкома
                if (RknQuestManager.PLAYER_QUEST_STAGE.getOrDefault(uuid, 0) == 3) {
                    int progress = RknQuestManager.PLAYER_QUEST_PROGRESS.getOrDefault(uuid, 0) + item.getCount();
                    
                    if (progress >= 3) {
                        RknQuestManager.PLAYER_QUEST_STAGE.put(uuid, 0); // Квест окончен
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§a[Повестка] Отлично! Все задания выполнены. Свободны!"));
                    } else {
                        RknQuestManager.PLAYER_QUEST_PROGRESS.put(uuid, progress);
                    }
                }
            }
        }
    }
}
