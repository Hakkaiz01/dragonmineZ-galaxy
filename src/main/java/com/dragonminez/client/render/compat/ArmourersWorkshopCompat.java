package com.dragonminez.client.render.compat;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;

public final class ArmourersWorkshopCompat {

    private static final boolean LOADED;
    private static Method isArmourRenderOverridden;
    private static Object skinTypeRegistry;

    static {
        boolean loaded = false;
        try {
            Class<?> apiClass = Class.forName("riskyken.armourersWorkshop.api.common.skin.type.ISkinTypeRegistry");
            Class<?> registry = Class.forName("riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry");
            skinTypeRegistry = registry.getField("INSTANCE").get(null);
            loaded = true;
        } catch (Exception ignored) {
        }
        LOADED = loaded;
    }

    private ArmourersWorkshopCompat() {
    }

    public static boolean isLoaded() {
        return LOADED;
    }

    public static boolean hasSkinInSlot(Player player, EquipmentSlot slot) {
        if (!LOADED) return false;
        try {
            Class<?> playerSkinDataClass = Class.forName("riskyken.armourersWorkshop.api.common.skin.data.ISkinData");
            Class<?> skinDataHandler = Class.forName("riskyken.armourersWorkshop.common.skin.data.SkinDataHandler");
            Method getPlayerSkinData = skinDataHandler.getMethod("getPlayerSkinData", Player.class);
            Object skinData = getPlayerSkinData.invoke(null, player);
            if (skinData == null) return false;

            Method hasSkin = playerSkinDataClass.getMethod("hasSkin", int.class);
            return (boolean) hasSkin.invoke(skinData, getSkinSlotIndex(slot));
        } catch (Exception e) {
            return false;
        }
    }

    private static int getSkinSlotIndex(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 0;
            case CHEST -> 1;
            case LEGS -> 2;
            case FEET -> 3;
            default -> -1;
        };
    }
}
