package com.yourname.rknmod.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class CourtScreen extends Screen {
    private int currentQuestion = 1;
    private int correctAnswersCount = 0;

    public CourtScreen() {
        super(Component.literal("Суд Российской Федерации"));
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if (currentQuestion == 1) {
            this.addRenderableWidget(Button.builder(Component.literal("ДА"), button -> nextQuestion(false))
                .bounds(centerX - 95, centerY + 20, 90, 20).build());
            this.addRenderableWidget(Button.builder(Component.literal("НЕТ"), button -> nextQuestion(true))
                .bounds(centerX + 5, centerY + 20, 90, 20).build());
        } 
        else if (currentQuestion == 2) {
            this.addRenderableWidget(Button.builder(Component.literal("ДА"), button -> nextQuestion(true))
                .bounds(centerX - 95, centerY + 20, 90, 20).build());
            this.addRenderableWidget(Button.builder(Component.literal("НЕТ"), button -> {
                this.minecraft.player.sendSystemMessage(Component.literal("§c[Суд] Принудительная установка мессенджера Макс!"));
                com.yourname.rknmod.system.RknWarningManager.WATCHED_PLAYERS.add(this.minecraft.player.getUUID());
                nextQuestion(false);
            }).bounds(centerX + 5, centerY + 20, 90, 20).build());
        } 
        else if (currentQuestion == 3) {
            this.addRenderableWidget(Button.builder(Component.literal("6"), button -> finishCourt(true))
                .bounds(centerX - 95, centerY + 20, 90, 20).build());
            this.addRenderableWidget(Button.builder(Component.literal("8"), button -> finishCourt(false))
                .bounds(centerX + 5, centerY + 20, 90, 20).build());
        }
    }

    private void nextQuestion(boolean wasCorrect) {
        if (wasCorrect) correctAnswersCount++;
        currentQuestion++;
        this.init();
    }

    private void finishCourt(boolean wasLastCorrect) {
        if (wasLastCorrect) correctAnswersCount++;
        this.minecraft.player.connection.sendCommand("rkncourt_finish " + correctAnswersCount);
        this.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0xFF000000);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        guiGraphics.drawCenteredString(this.font, "§c§lСУД РОССИЙСКОЙ ФЕДЕРАЦИИ", centerX, centerY - 60, 0xFFFFFF);

        if (currentQuestion == 1) {
            guiGraphics.drawCenteredString(this.font, "Вы признаете свою вину?", centerX, centerY - 20, 0xFFFFFF);
        } else if (currentQuestion == 2) {
            guiGraphics.drawCenteredString(this.font, "Вы скачивали мессенджер Макс?", centerX, centerY - 20, 0xFFFFFF);
        } else if (currentQuestion == 3) {
            guiGraphics.drawCenteredString(this.font, "Сколько будет 2+2•2 ?", centerX, centerY - 20, 0xFFFFFF);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}
