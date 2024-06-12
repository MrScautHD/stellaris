package com.st0x0ef.stellaris.common.keybinds;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeyVariables {
    public static final Map<UUID, Boolean> KEY_JUMP = new HashMap<>();

    public static boolean isHoldingJump(Player player) {
        return player != null && KEY_JUMP.getOrDefault(player.getUUID(), false);
    }
}
