package com.st0x0ef.stellaris.platform.fabric;

import com.st0x0ef.stellaris.client.skys.renderer.SkyRenderer;
import com.st0x0ef.stellaris.platform.ClientUtilsPlatform;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.Map;

public class ClientUtilsPlatformImpl {

    public static void registerArmor(ResourceLocation texture, ModelLayerLocation layer, ClientUtilsPlatform.ArmorFactory factory, Item... items) {
        ArmorRenderer.register((poseStack, buffer, stack, entity, slot, packedLight, original) -> {
            ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(layer);
            HumanoidModel<?> model = factory.create(root, slot, stack, original);

            model.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucent(texture)), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        }, items);
    }

    public static void registerPlanetsSkies(Map<ResourceKey<Level>, SkyRenderer> renderer)  {
        renderer.forEach( (level, skyRenderer) -> {
            DimensionRenderingRegistry.registerCloudRenderer( level, context -> {
                skyRenderer.hasCloud();
            });
        });
    }
}
