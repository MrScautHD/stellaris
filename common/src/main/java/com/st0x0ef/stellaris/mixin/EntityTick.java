package com.st0x0ef.stellaris.mixin;

import com.st0x0ef.stellaris.Stellaris;
import com.st0x0ef.stellaris.common.oxygen.GlobalOxygenManager;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class EntityTick {
    @Unique
    private long stellaris$tickSinceLastOxygenCheck;

    @Inject(at = @At(value = "HEAD"), method = "tick")
    private void tick(CallbackInfo info) {
        LivingEntity entity = (LivingEntity) ((Object) this);

        if (!entity.level().isClientSide()) {
            if (stellaris$tickSinceLastOxygenCheck > 20){
                if (!GlobalOxygenManager.getInstance().getOrCreateDimensionManager(entity.level().dimension()).canBreath(entity)) {
                    //entity.hurt(DamageSourceRegistry.of(entity.level(), DamageSourceRegistry.OXYGEN), 0.5f);
                    Stellaris.LOG.error("Bobo :(");
                }

                stellaris$tickSinceLastOxygenCheck = 0;
            }

            stellaris$tickSinceLastOxygenCheck++;
        }
    }
}
