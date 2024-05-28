package com.st0x0ef.stellaris.common.menus;

import com.st0x0ef.stellaris.common.entities.RocketEntity;
import com.st0x0ef.stellaris.common.menus.slot.FuelBucketSlot;
import com.st0x0ef.stellaris.common.menus.slot.ResultSlot;
import com.st0x0ef.stellaris.common.menus.slot.SpecificItemsSlot;
import com.st0x0ef.stellaris.common.registry.ItemsRegistry;
import com.st0x0ef.stellaris.common.registry.MenuTypesRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RocketMenu extends AbstractContainerMenu {

    private final Container inventory;
    private final RocketEntity rocket;

    public RocketMenu(int syncId, Inventory inventory, FriendlyByteBuf buffer) {
        this(syncId, inventory, new SimpleContainer(15), buffer.readVarInt());
    }



    public RocketMenu(int syncId, Inventory playerInventory, Container container, int entityId)
    {

        super(MenuTypesRegistry.ROCKET_MENU.get(), syncId);

        this.rocket = (RocketEntity) playerInventory.player.level().getEntity(entityId);
        checkContainerSize(container, 14);
        this.inventory = (container);

        addSlots(inventory);

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, this.inventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return newStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }


    private void addSlots(Container inventory) {
        //FUEL SLOTS
        this.addSlot(new FuelBucketSlot(inventory, 0, 20, 27));
        this.addSlot(new ResultSlot(inventory, 1, 20, 57));

        //INVENTORY SLOTS
        this.addSlot(new Slot(inventory, 2, 86, 20));
        this.addSlot(new Slot(inventory, 3, 86, 37));

        this.addSlot(new Slot(inventory, 4, 104, 20));
        this.addSlot(new Slot(inventory, 5, 104, 37));

        this.addSlot(new Slot(inventory, 6, 122, 20));
        this.addSlot(new Slot(inventory, 7, 122, 37));

        this.addSlot(new Slot(inventory, 8, 140, 20));
        this.addSlot(new Slot(inventory, 9, 140, 37));

        //UPGRADE SLOTS
        this.addSlot(new SpecificItemsSlot.Item(inventory, 10, 76, 66, ItemsRegistry.STEEL_NUGGET.get()));
        this.addSlot(new Slot(inventory, 11, 101, 66));
        this.addSlot(new Slot(inventory, 12, 126, 66));
        this.addSlot(new Slot(inventory, 13, 151, 66));

    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, (84 + i * 18) + 11));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 153));
        }
    }

    public RocketEntity getRocket() {
        return rocket;
    }
}