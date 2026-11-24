package fuzs.overflowingbars.common.client.gui;

import fuzs.overflowingbars.common.OverflowingBars;
import fuzs.overflowingbars.common.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class ArmorBarRenderer {
    /**
     * Use vanilla sprites for regular armor points, so that resource packs changing them also apply here.
     */
    private static final Identifier VANILLA_ARMOR_EMPTY = Identifier.withDefaultNamespace("hud/armor_empty");
    private static final Identifier VANILLA_ARMOR_HALF = Identifier.withDefaultNamespace("hud/armor_half");
    private static final Identifier VANILLA_ARMOR_FULL = Identifier.withDefaultNamespace("hud/armor_full");
    private static final ArmorType ARMOR = new ArmorType(VANILLA_ARMOR_EMPTY,
            VANILLA_ARMOR_HALF,
            VANILLA_ARMOR_FULL,
            OverflowingBars.id("hud/armor/layer_half"),
            OverflowingBars.id("hud/armor/layer_full"),
            OverflowingBars.id("hud/armor/mixed"));
    private static final ArmorType ARMOR_INVERSE = new ArmorType(VANILLA_ARMOR_EMPTY,
            OverflowingBars.id("hud/armor/layer_half"),
            OverflowingBars.id("hud/armor/layer_full"),
            VANILLA_ARMOR_HALF,
            VANILLA_ARMOR_FULL,
            OverflowingBars.id("hud/armor/mixed_mirrored"));
    private static final ArmorType ARMOR_MIRRORED = new ArmorType(VANILLA_ARMOR_EMPTY,
            VANILLA_ARMOR_HALF,
            VANILLA_ARMOR_FULL,
            OverflowingBars.id("hud/armor/layer_half_mirrored"),
            OverflowingBars.id("hud/armor/layer_full"),
            OverflowingBars.id("hud/armor/mixed_mirrored"));
    private static final ArmorType ARMOR_MIRRORED_INVERSE = new ArmorType(VANILLA_ARMOR_EMPTY,
            OverflowingBars.id("hud/armor/layer_half_mirrored"),
            OverflowingBars.id("hud/armor/layer_full"),
            VANILLA_ARMOR_HALF,
            VANILLA_ARMOR_FULL,
            OverflowingBars.id("hud/armor/mixed"));
    private static final ArmorType TOUGHNESS =new ArmorType(OverflowingBars.id("hud/toughness/empty"),
            OverflowingBars.id("hud/toughness/half"),
            OverflowingBars.id("hud/toughness/full"),
            OverflowingBars.id("hud/toughness/layer_half"),
            OverflowingBars.id("hud/toughness/layer_full"),
            OverflowingBars.id("hud/toughness/mixed"));
    private static final ArmorType TOUGHNESS_INVERSE = new ArmorType(OverflowingBars.id("hud/toughness/empty"),
            OverflowingBars.id("hud/toughness/layer_half"),
            OverflowingBars.id("hud/toughness/layer_full"),
            OverflowingBars.id("hud/toughness/half"),
            OverflowingBars.id("hud/toughness/full"),
            OverflowingBars.id("hud/toughness/mixed_mirrored"));
    private static final ArmorType TOUGHNESS_MIRRORED = new ArmorType(OverflowingBars.id("hud/toughness/empty"),
            OverflowingBars.id("hud/toughness/half_mirrored"),
            OverflowingBars.id("hud/toughness/full"),
            OverflowingBars.id("hud/toughness/layer_half_mirrored"),
            OverflowingBars.id("hud/toughness/layer_full"),
            OverflowingBars.id("hud/toughness/mixed_mirrored"));
    private static final ArmorType TOUGHNESS_MIRRORED_INVERSE = new ArmorType(OverflowingBars.id("hud/toughness/empty"),
            OverflowingBars.id("hud/toughness/layer_half_mirrored"),
            OverflowingBars.id("hud/toughness/layer_full"),
            OverflowingBars.id("hud/toughness/half_mirrored"),
            OverflowingBars.id("hud/toughness/full"),
            OverflowingBars.id("hud/toughness/mixed"));

    public static void renderArmorBar(GuiGraphicsExtractor guiGraphics, int posX, int posY, Player player, boolean leftSide) {
        Profiler.get().push(OverflowingBars.id("armor").toString());
        ClientConfig.AbstractArmorRowConfig config = OverflowingBars.CONFIG.get(ClientConfig.class).armor;
        int armorPoints = player.getArmorValue();
        ArmorType armorType;
        if (config.inverseColoring) {
            armorType = leftSide ? ARMOR_INVERSE : ARMOR_MIRRORED_INVERSE;
        } else {
            armorType = leftSide ? ARMOR : ARMOR_MIRRORED;
        }

        renderArmorBar(guiGraphics, posX, posY, armorType, armorPoints, leftSide, false, config);
        Profiler.get().pop();
    }

    public static void renderToughnessBar(GuiGraphicsExtractor guiGraphics, int posX, int posY, Player player, boolean leftSide, boolean matchVanilla) {
        Profiler.get().push(OverflowingBars.id("toughness").toString());
        ClientConfig.ToughnessRowConfig config = OverflowingBars.CONFIG.get(ClientConfig.class).toughness;
        int armorPoints = Mth.floor(player.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        ArmorType armorType;
        if (!matchVanilla && config.inverseColoring) {
            armorType = leftSide ? TOUGHNESS_INVERSE : TOUGHNESS_MIRRORED_INVERSE;
        } else {
            armorType = leftSide ? TOUGHNESS : TOUGHNESS_MIRRORED;
        }

        renderArmorBar(guiGraphics,
                posX,
                posY,
                armorType, armorPoints,
                leftSide,
                matchVanilla,
                config);
        Profiler.get().pop();
    }

    private static void renderArmorBar(GuiGraphicsExtractor guiGraphics, int posX, int posY, ArmorType armorType, int value, boolean leftSide, boolean matchVanilla, ClientConfig.AbstractArmorRowConfig config) {
        if (value <= 0) {
            return;
        }

        boolean skipEmpty = !matchVanilla && config.skipEmptyArmorPoints;
        int lastRowArmorPoints = 0;
        if (!matchVanilla) {
            if (config.colorizeFirstRow || value > 20) {
                lastRowArmorPoints = (value - 1) % 20 + 1;
            }
        }

        for (int currentArmorPoint = 0; currentArmorPoint < 10; ++currentArmorPoint) {
            int startX = posX + (leftSide ? currentArmorPoint * 8 : -currentArmorPoint * 8 - 9);
            if (currentArmorPoint * 2 + 1 < lastRowArmorPoints) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, armorType.layerFull(), startX, posY, 9, 9);
            } else if (currentArmorPoint * 2 + 1 == lastRowArmorPoints) {
                if (value > 20) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, armorType.mixed(), startX, posY, 9, 9);
                } else {
                    blitHalfSprite(guiGraphics, armorType.layerHalf(), startX, posY, leftSide);
                }
            } else if (currentArmorPoint * 2 + 1 < value) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, armorType.full(), startX, posY, 9, 9);
            } else if (currentArmorPoint * 2 + 1 == value) {
                blitHalfSprite(guiGraphics, armorType.half(), startX, posY, leftSide);
            } else if (!skipEmpty && currentArmorPoint * 2 + 1 > value) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, armorType.empty(), startX, posY, 9, 9);
            }
        }
    }

    private static void blitHalfSprite(GuiGraphicsExtractor guiGraphics, Identifier sprite, int posX, int posY, boolean leftSide) {
        // vanilla has no mirrored half armor sprite, so flip it when rendering from right to left
        // swap the u coordinates instead of scaling the pose, as a negative scale gets the quad culled
        if (!leftSide && sprite.equals(VANILLA_ARMOR_HALF)) {
            TextureAtlasSprite atlasSprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(sprite);
            guiGraphics.blit(atlasSprite.atlasLocation(),
                    posX,
                    posY,
                    posX + 9,
                    posY + 9,
                    atlasSprite.getU1(),
                    atlasSprite.getU0(),
                    atlasSprite.getV0(),
                    atlasSprite.getV1());
        } else {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, posX, posY, 9, 9);
        }
    }

    private record ArmorType(Identifier empty,
                             Identifier half,
                             Identifier full,
                             Identifier layerHalf,
                             Identifier layerFull,
                             Identifier mixed) {

    }
}
