package fuzs.overflowingbars.common.client.helper;

import fuzs.overflowingbars.common.OverflowingBars;
import fuzs.overflowingbars.common.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class ChatOffsetHelper {

    public static int getChatOffsetY() {
        Player player = Minecraft.getInstance().player;
        ClientConfig config = OverflowingBars.CONFIG.get(ClientConfig.class);
        int leftOffset = 0;
        int rightOffset = 0;
        if (twoHealthRows(player)) {
            leftOffset++;
        }

        if (armorRow(player)) {
            if (config.armor.rightSide) {
                rightOffset++;
            } else {
                leftOffset++;
            }
        }

        if (toughnessRow(player)) {
            if (config.toughness.leftSide) {
                leftOffset++;
            } else {
                rightOffset++;
            }
        }

        return Math.max(leftOffset, rightOffset) * 10;
    }

    public static boolean twoHealthRows(Player player) {
        return player.getAbsorptionAmount() > 0.0F && player.getMaxHealth() + player.getAbsorptionAmount() > 20.0F;
    }

    public static boolean armorRow(Player player) {
        return player.getArmorValue() > 0;
    }

    public static boolean toughnessRow(Player player) {
        return OverflowingBars.CONFIG.get(ClientConfig.class).toughness.armorToughnessBar
                && Mth.floor(player.getAttributeValue(Attributes.ARMOR_TOUGHNESS)) > 0;
    }
}
