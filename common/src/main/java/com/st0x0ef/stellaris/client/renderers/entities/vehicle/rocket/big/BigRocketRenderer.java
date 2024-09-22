package com.st0x0ef.stellaris.client.renderers.entities.vehicle.rocket.big;

import com.st0x0ef.stellaris.Stellaris;
import com.st0x0ef.stellaris.client.renderers.entities.vehicle.VehicleRenderer;
import com.st0x0ef.stellaris.client.renderers.entities.vehicle.rocket.RocketModel;
import com.st0x0ef.stellaris.common.entities.vehicles.RocketEntity;
import com.st0x0ef.stellaris.common.rocket_upgrade.ModelUpgrade;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BigRocketRenderer extends VehicleRenderer<RocketEntity, BigRocketModel<RocketEntity>> {
    public ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Stellaris.MODID, "textures/vehicle/rocket_skin/big/standard.png");

    public BigRocketRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BigRocketModel<>(renderManagerIn.bakeLayer(BigRocketModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(RocketEntity rocket) {
        rocket.MODEL_UPGRADE = new ModelUpgrade(RocketModel.BIG, 3);
        return rocket.getFullSkinTexture();
    }

    @Override
    protected boolean isShaking(RocketEntity rocket) {
        return rocket.getEntityData().get(RocketEntity.ROCKET_START);
    }
}
