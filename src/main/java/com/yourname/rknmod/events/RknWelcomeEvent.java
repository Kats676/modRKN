package com.yourname.rknmod.events;

import com.yourname.rknmod.system.RknEconomyManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "rknmod")
public class RknWelcomeEvent {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            
            String tagKey = "rkn_received_welcome_bonus";
            
            // Если игрок еще не получал стартовый бонус в этом мире
            if (!player.getPersistentData().getBoolean(tagKey)) {
                player.getPersistentData().putBoolean(tagKey, true);

                // Приветственное СМС
                player.sendSystemMessage(Component.literal(
                    "§aПривет! Вы попали в мир Роскомнадзор! Тут будет Блокировки,Задания, и тд! За вход в мир +5 рублей! И бесплатную булочку)"
                ));

                // Даем 5 рублей
                RknEconomyManager.changeBalance(player, 5);

                // Создаем ту самую булочку со скоростью
                ItemStack bun = new ItemStack(Items.BREAD);
                bun.setHoverName(Component.literal("§6Бесплатная булочка"));

                FoodProperties bunFood = new FoodProperties.Builder()
                    .nutrition(10) // Насыщение на 5 сердец (10 ХП)
                    .saturationMod(0.6F)
                    // Скорость 2 (уровень 1) на 30 секунд (600 тиков) за счет сахара в булке
                    .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1), 1.0F)
                    .alwaysEat() // Можно съесть даже если сыт
                    .build();

                bun.getOrCreateTag().putInt("CustomNutrition", 10);
                player.getInventory().add(bun);
            }
        }
    }
}
