package com.st0x0ef.stellaris.client.skys.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.st0x0ef.stellaris.client.skys.record.CustomVanillaObject;
import com.st0x0ef.stellaris.client.skys.record.SkyObject;
import com.st0x0ef.stellaris.mixin.client.LevelRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class SkyHelper {
    public static void drawSky(Minecraft mc, Matrix4f matrix4f, Matrix4f projectionMatrix, ShaderInstance shaderInstance, Tesselator tesselator, PoseStack poseStack, float partialTick) {
        ((LevelRendererAccessor) mc.levelRenderer).stellaris$getSkyBuffer().bind();
        ((LevelRendererAccessor) mc.levelRenderer).stellaris$getSkyBuffer().drawWithShader(matrix4f, projectionMatrix, shaderInstance);
        VertexBuffer.unbind();
    }

    public static void drawMoonWithPhase(ClientLevel level, Tesselator tesselator, PoseStack poseStack, float y, CustomVanillaObject moon, float dayAngle) {
        if (moon.moonPhase()) {
            int moonPhase = level.getMoonPhase();
            int xCoord = moonPhase % 4;
            int yCoord = moonPhase / 4 % 2;
            float startX = xCoord / 4.0F;
            float startY = yCoord / 2.0F;
            float endX = (xCoord + 1) / 4.0F;
            float endY = (yCoord + 1) / 2.0F;

            drawCelestialBody(moon.moonTexture(), tesselator, poseStack, y, 20f, dayAngle, startX, endX, startY, endY, false);
        } else {
            drawCelestialBody(moon.moonTexture(), tesselator, poseStack, y, 20f, dayAngle, 0, 1, 0, 1, false);
        }
    }

    public static void drawCelestialBody(SkyObject skyObject, Tesselator tesselator, PoseStack poseStack, float y, float dayAngle, boolean blend) {
        drawCelestialBody(skyObject.texture(), tesselator, poseStack, y, skyObject.size(), dayAngle, blend);
    }

    public static void drawCelestialBody(ResourceLocation texture, Tesselator tesselator, PoseStack poseStack, float y, float size, float dayAngle, boolean blend) {
        drawCelestialBody(texture, tesselator, poseStack, y, size, dayAngle, 0f, 1f, 1f, 0f, blend);
    }

    public static void drawCelestialBody(ResourceLocation texture, Tesselator tesselator, PoseStack poseStack, float y, float size, float dayAngle, float startX, float endX, float startY, float endY, boolean blend) {
        if (blend) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }

        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(-90f));
        poseStack.mulPose(Axis.XP.rotationDegrees(dayAngle));

        Matrix4f matrix4f = poseStack.last().pose();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, -size, y, -size).setUv(startX, endY);
        bufferBuilder.addVertex(matrix4f, size, y, -size).setUv(endX, endY);
        bufferBuilder.addVertex(matrix4f, size, y, size).setUv(endX, startY);
        bufferBuilder.addVertex(matrix4f, -size, y, size).setUv(startX, startY);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        poseStack.popPose();

        if (blend) {
            RenderSystem.disableBlend();
        }
    }
}