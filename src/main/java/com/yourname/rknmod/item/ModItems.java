package com.yourname.rknmod.item;

import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

public class ModItems {
    // Телефон, открывающий GUI
    public static final Item RKN_PHONE = new Item(new Item.Properties().stacksTo(1)) {
        @Override
        public InteractionResultHolder<net.minecraft.world.item.ItemStack> use(Level level, Player player, InteractionHand hand) {
            if (level.isClientSide()) {
                // На клиенте (на iPad) открываем экран телефона
                openPhoneGui();
            }
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
        }
    };
    
    public static final Item POVESTKA = new Item(new Item.Properties().stacksTo(64));
    public static final Item PROXY = new Item(new Item.Properties().stacksTo(16));

    public static void registerItems() {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("rknmod", "rkn_phone"), RKN_PHONE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("rknmod", "povestka"), POVESTKA);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("rknmod", "proxy"), PROXY);
    }

    private static void openPhoneGui() {
        // Вызов клиентского экрана (подключается на стороне клиента)
        net.minecraft.client.Minecraft.getInstance().setScreen(new com.yourname.rknmod.client.PhoneScreen());
    }
}
