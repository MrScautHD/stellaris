package com.st0x0ef.stellaris.client.renderers.entities.vehicle.rocket.tiny;

import com.st0x0ef.stellaris.Stellaris;
import com.st0x0ef.stellaris.client.renderers.entities.vehicle.VehicleRenderer;
import com.st0x0ef.stellaris.client.renderers.entities.vehicle.rocket.RocketModel;
import com.st0x0ef.stellaris.common.entities.vehicles.RocketEntity;
import com.st0x0ef.stellaris.common.rocket_upgrade.ModelUpgrade;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class TinyRocketRenderer extends VehicleRenderer<RocketEntity, TinyRocketModel<RocketEntity>> {
    public ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Stellaris.MODID, "textures/vehicle/rocket_skin/tiny/standard.png");

    public TinyRocketRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new TinyRocketModel<>(renderManagerIn.bakeLayer(TinyRocketModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(RocketEntity rocket) {
        rocket.MODEL_UPGRADE = new ModelUpgrade(RocketModel.TINY, 1);
        return rocket.getFullSkinTexture();
    }

    @Override
    protected boolean isShaking(RocketEntity rocket) {
        return rocket.getEntityData().get(RocketEntity.ROCKET_START);
    }
}