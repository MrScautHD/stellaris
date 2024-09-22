package com.st0x0ef.stellaris.mixin;

import com.st0x0ef.stellaris.common.armors.AbstractSpaceArmor;
import com.st0x0ef.stellaris.common.armors.JetSuit;
import com.st0x0ef.stellaris.common.registry.EntityData;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerDataMixin extends LivingEntity {

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Unique
    Player stellaris$player = (Player) ((Object) this);

    protected PlayerDataMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(EntityData.DATA_PLANET_MENU_OPEN, false);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if(this.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof AbstractSpaceArmor.AbstractSpaceChestplate suit) {
            suit.onArmorTick(this.getItemBySlot(EquipmentSlot.CHEST), this.level(), stellaris$player);
        }
    }
}
