package com.st0x0ef.stellaris.client.renderers.entities.vehicle.rover;

import com.st0x0ef.stellaris.Stellaris;
import com.st0x0ef.stellaris.client.renderers.entities.vehicle.VehicleRenderer;
import com.st0x0ef.stellaris.common.entities.vehicles.RoverEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RoverRenderer extends AbstractRoverRenderer<RoverEntity, RoverModel<RoverEntity>> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Stellaris.MODID, "textures/vehicle/rover.png");

    public RoverRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new RoverModel<>(renderManagerIn.bakeLayer(RoverModel.LAYER_LOCATION)), 0f);
    }

    @Override
    public ResourceLocation getTextureLocation(RoverEntity p_114482_) {
        return TEXTURE;
    }

    @Override
    public boolean shouldRender(RoverEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        return frustum.isVisible(entity.getBoundingBox().inflate(4));

    }
}