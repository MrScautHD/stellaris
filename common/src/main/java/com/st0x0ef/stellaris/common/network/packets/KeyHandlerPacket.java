package com.st0x0ef.stellaris.common.network.packets;

import com.st0x0ef.stellaris.Stellaris;
import com.st0x0ef.stellaris.common.armors.JetSuit;
import com.st0x0ef.stellaris.common.entities.vehicles.LanderEntity;
import com.st0x0ef.stellaris.common.entities.vehicles.RocketEntity;
import com.st0x0ef.stellaris.common.keybinds.KeyVariables;
import com.st0x0ef.stellaris.common.menus.PlanetSelectionMenu;
import com.st0x0ef.stellaris.common.network.NetworkRegistry;
import com.st0x0ef.stellaris.common.utils.Utils;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class KeyHandlerPacket implements CustomPacketPayload {
    public final String key;
    public final boolean condition;


    public static final StreamCodec<RegistryFriendlyByteBuf, KeyHandlerPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull KeyHandlerPacket decode(RegistryFriendlyByteBuf buf) {
            return new KeyHandlerPacket(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, KeyHandlerPacket packet) {
            buf.writeUtf(packet.key);
            buf.writeBoolean(packet.condition);

        }
    };


    public KeyHandlerPacket(String key, boolean condition) {
        this.key = key;
        this.condition = condition;
    }

    public KeyHandlerPacket(RegistryFriendlyByteBuf buffer) {
        this.key = buffer.readUtf();
        this.condition = buffer.readBoolean();
    }

    public static void handle(KeyHandlerPacket packet, NetworkManager.PacketContext context) {
        Player player = context.getPlayer();
        context.queue(() -> {
            switch (packet.key) {
                case "key_up":
                    KeyVariables.KEY_UP.put(player.getUUID(), packet.condition);
                    break;

                case "key_down":
                    KeyVariables.KEY_DOWN.put(player.getUUID(), packet.condition);
                    break;

                case "key_right":
                    KeyVariables.KEY_RIGHT.put(player.getUUID(), packet.condition);
                    break;

                case "key_left":
                    KeyVariables.KEY_LEFT.put(player.getUUID(), packet.condition);
                    break;
                case "switch_jet_suit_mode":
                    if (Utils.isLivingInJetSuit(player)) {
                        ItemStack itemStack = player.getItemBySlot(EquipmentSlot.CHEST);
                        JetSuit.Suit item = (JetSuit.Suit) itemStack.getItem();
                        item.switchJetSuitMode(itemStack);
                    }
                    break;
                case "slow_lander":
                    if (player.getVehicle() instanceof LanderEntity lander) lander.slowDownLander();
                    break;
                case "rocket_start":
                    if (player.getVehicle() instanceof RocketEntity rocketEntity) rocketEntity.startRocket();
                    break;
                case "key_jump":
                    KeyVariables.KEY_JUMP.put(player.getUUID(), packet.condition);
                    break;
                case "freeze_planet_menu":
                    if (player.containerMenu instanceof PlanetSelectionMenu menu) menu.switchFreezeGui();
                    break;
                default:
                    Stellaris.LOG.error("unknown key action {}", packet.key);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return NetworkRegistry.KEY_HANDLER_ID;
    }


}
