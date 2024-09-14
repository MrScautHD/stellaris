package com.st0x0ef.stellaris.common.rocket_upgrade;

import com.st0x0ef.stellaris.client.screens.GUISprites;
import net.minecraft.resources.ResourceLocation;

public class MotorUpgrade extends RocketUpgrade {
    private final FuelType.Type type;
    private final ResourceLocation fluidTexture;

    public MotorUpgrade(FuelType.Type type, ResourceLocation fluidTexture) {
        this.type = type;
        this.fluidTexture = fluidTexture;
    }

    public FuelType.Type getFuelType() {
        return this.type;
    }
    public ResourceLocation getFluidTexture() {
        return this.fluidTexture;
    }

    public static MotorUpgrade getBasic() {
        return new MotorUpgrade(FuelType.Type.FUEL, GUISprites.FUEL_OVERLAY);
    }
}
