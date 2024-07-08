package com.st0x0ef.stellaris.platform;

import com.st0x0ef.stellaris.client.skys.renderer.SkyRenderer;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;

public class ClientUtilsPlatform {

    @FunctionalInterface
    public interface ArmorFactory {

        HumanoidModel<?> create(ModelPart root, EquipmentSlot slot, ItemStack stack, HumanoidModel<LivingEntity> parentModel);
    }


    @ExpectPlatform
    public static void registerArmor(ResourceLocation texture, ModelLayerLocation layer, ArmorFactory factory, Item... items) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerPlanetsSkies(Map<ResourceKey<Level>, SkyRenderer> renderer) {
        throw new AssertionError();
    }

}
