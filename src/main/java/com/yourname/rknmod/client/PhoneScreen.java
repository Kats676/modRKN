package com.yourname.rknmod.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PhoneScreen extends Screen {
    private static final ResourceLocation PHONE_TEXTURE = new ResourceLocation("rknmod", "textures/gui/phone.png");
    private int xSize = 120;
    private int ySize = 200;

    private static long lastAdTime = 0;
    private boolean isAdShowing = false;
    private int adTicks = 0;
    private boolean isFolderOpen = false; 

    public PhoneScreen() {
        super(Component.literal("РКН Телефон"));
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAdTime >= 600000 || lastAdTime == 0) {
            this.isAdShowing = true;
            this.adTicks = 0;
            lastAdTime = currentTime;
        }
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        int left = (this.width - this.xSize) / 2;
        int top = (this.height - this.ySize) / 2;

        if (this.isAdShowing) return;

        if (!isFolderOpen) {
            // === ГЛАВНЫЙ ЭКРАН ТЕЛЕФОНА ===
            this.addRenderableWidget(Button.builder(Component.literal("ВПН"), button -> {
                com.yourname.rknmod.system.VpnManager.activateVpn(this.minecraft.player);
                this.onClose();
            }).bounds(left + 15, top + 30, 40, 20).build());

            this.addRenderableWidget(Button.builder(Component.literal("Законы"), button -> {
                this.minecraft.player.sendSystemMessage(Component.literal("§eЗакон №138: Прыгать в лаву запрещено!"));
            }).bounds(left + 65, top + 30, 40, 20).build());

            this.addRenderableWidget(Button.builder(Component.literal("Повестка"), button -> {
                String status = com.yourname.rknmod.system.RknWarningManager.getPlayerStatus(this.minecraft.player);
                this.minecraft.player.sendSystemMessage(Component.literal("§7Статус: " + status));
            }).bounds(left + 15, top + 60, 40, 20).build());

            this.addRenderableWidget(Button.builder(Component.literal("Скачать Макс"), button -> {
                com.yourname.rknmod.system.RknWarningManager.tryClearWarningWithMax(this.minecraft.player);
            }).bounds(left + 65, top + 60, 40, 20).build());

            this.addRenderableWidget(Button.builder(Component.literal("Удалить Макс"), button -> {
                this.minecraft.player.sendSystemMessage(Component.literal("§cШтраф 100р, он обратно скачался)"));
                com.yourname.rknmod.system.RknEconomyManager.changeBalance(this.minecraft.player, -100);
            }).bounds(left + 65, top + 90, 40, 20).build());

            // КНОПКА МАГАЗИНА И УДАЛЕНИЯ РКН
            this.addRenderableWidget(Button.builder(Component.literal("Удалить РКН? (0р)"), button -> {
                if (com.yourname.rknmod.events.RknDragonEvents.isDragonDefeated) {
                    this.minecraft.player.sendSystemMessage(Component.literal("§c§lРоскомнадзор принял решение"));
                    this.minecraft.player.sendSystemMessage(Component.literal("§4« О нет… Ты меня удалил.. Ты победил… »"));
                    com.yourname.rknmod.system.RknMainStatus.isRknDeleted = true;
                    this.onClose();
                } else {
                    this.minecraft.player.sendSystemMessage(Component.literal("§c[Shop] Ошибка! Чтобы удалить РКН, убейте Эндер Дракона!"));
                }
            }).bounds(left + 15, top + 120, 90, 20).build());

            this.addRenderableWidget(Button.builder(Component.literal("[ Папка ДП ]"), button -> {
                this.isFolderOpen = true;
                this.init();
            }).bounds(left + 15, top + 90, 40, 20).build());

        } else {
            // === ЭКРАН ОТКРЫТОЙ ПАПКИ ДП ===
            this.addRenderableWidget(Button.builder(Component.literal("< Назад"), button -> {
                this.isFolderOpen = false;
                this.init();
            }).bounds(left + 15, top + 150, 90, 20).build());

            this.addRenderableWidget(Button.builder(Component.literal("WhatsApp"), button -> {
                this.minecraft.player.sendSystemMessage(Component.literal("§c[РКН] Ошибка 403: Доступ к WhatsApp ограничен на территории РФ."));
            }).bounds(left + 15, top + 30, 45, 20).build());

            this.addRenderableWidget(Button.builder(Component.literal("Telegram"), button -> {
                this.minecraft.player.sendSystemMessage(Component.literal("§c[РКН] Ошибка 403: Соединение с Telegram заблокировано."));
            }).bounds(left + 65, top + 30, 45, 20).build());

            this.addRenderableWidget(Button.builder(Component.literal("Discord"), button -> {
                this.minecraft.player.sendSystemMessage(Component.literal("§c[РКН] Ошибка 403: Серверы Discord заблокированы РКН."));
            }).bounds(left + 15, top + 60, 45, 20).build());

            this.addRenderableWidget(Button.builder(Component.literal("New VPN"), button -> {
                this.minecraft.player.sendSystemMessage(Component.literal("§7[VPN] Бесконечное подключение... Данный ВПН заблокирован!"));
            }).bounds(left + 65, top + 60, 45, 20).build());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        int left = (this.width - this.xSize) / 2;
        int top = (this.height - this.ySize) / 2;
        
        guiGraphics.blit(PHONE_TEXTURE, left, top, 0, 0, this.xSize, this.ySize);
        
        if (this.isAdShowing) {
            adTicks++;
            guiGraphics.fill(left + 5, top + 5, left + this.xSize - 5, top + this.ySize - 5, 0xFF000000);

            if (adTicks < 100) {
                guiGraphics.drawCenteredString(this.font, "§c§lСкачайте Макс!", left + this.xSize / 2, top + this.ySize / 2 - 10, 0xFFFFFF);
            } else if (adTicks >= 100 && adTicks < 200) {
                guiGraphics.drawCenteredString(this.font, "§e§lСкачайте Макс", left + this.xSize / 2, top + this.ySize / 2 - 20, 0xFFFFFF);
                guiGraphics.drawCenteredString(this.font, "§a§lБесплатно", left + this.xSize / 2, top + this.ySize / 2, 0xFFFFFF);
                guiGraphics.drawCenteredString(this.font, "§d§lи без ВПН!", left + this.xSize / 2, top + this.ySize / 2 + 20, 0xFFFFFF);
            } else {
                this.isAdShowing = false;
                this.init();
            }
        } else {
            if (isFolderOpen) {
                guiGraphics.fill(left + 8, top + 25, left + this.xSize - 8, top + 140, 0x55555555);
                guiGraphics.drawCenteredString(this.font, "§8Папка ДП", left + this.xSize / 2, top + 12, 0xFFFFFF);
            }
            super.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
