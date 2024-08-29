package com.st0x0ef.stellaris.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.st0x0ef.stellaris.Stellaris;
import com.st0x0ef.stellaris.client.screens.components.Gauge;
import com.st0x0ef.stellaris.common.blocks.entities.machines.RadioactiveGeneratorEntity;
import com.st0x0ef.stellaris.common.menus.RadioactiveGeneratorMenu;
import com.st0x0ef.stellaris.common.systems.energy.impl.WrappedBlockEnergyContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class RadioactiveGeneratorScreen extends AbstractContainerScreen<RadioactiveGeneratorMenu> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Stellaris.MODID, "textures/gui/radioactive_generator.png");

    private final RadioactiveGeneratorEntity blockEntity = getMenu().getBlockEntity();
    private Gauge energyGauge;

    public RadioactiveGeneratorScreen(RadioactiveGeneratorMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        imageWidth = 177;
        imageHeight = 228;
        inventoryLabelY = imageHeight - 92;
    }

    @Override
    protected void init() {
        super.init();

        if (blockEntity == null) {
            return;
        }

        WrappedBlockEnergyContainer energyStorage = blockEntity.getWrappedEnergyContainer();
        energyGauge = new Gauge(leftPos + 147, topPos + 51, 13, 49, Component.translatable("stellaris.screen.energy"), GUISprites.ENERGY_FULL, GUISprites.BATTERY_OVERLAY, (int) energyStorage.getStoredEnergy(), (int) energyStorage.getMaxCapacity());
        addRenderableWidget(energyGauge);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);

        if (blockEntity == null) {
            return;
        }

        energyGauge.update((int)blockEntity.getWrappedEnergyContainer().getStoredEnergy());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        if (menu.isLit()) {
            int i = Mth.ceil(menu.getLitProgress() * 13.0F) + 1;
            graphics.blitSprite(GUISprites.LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - i, leftPos + 84, topPos + 69 + 14 - i, 14, i);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        List<Component> components = new ArrayList<>();
        components.add(Component.translatable("gauge_text.stellaris.max_generation", blockEntity.getEnergyGeneratedPT()));
        energyGauge.renderTooltips(guiGraphics, x, y, font, components);
    }
}
