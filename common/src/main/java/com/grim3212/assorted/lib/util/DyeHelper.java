package com.grim3212.assorted.lib.util;

import com.google.common.collect.Maps;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColorCollection;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DyeHelper {
    public static TagKey<Item> getDyeTag(DyeColor color) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Services.PLATFORM.getCommonTagPrefix(), "dyes/" + color.getName()));
    }

    public static DyeColor getColor(ItemStack stack) {
        if (stack.getItem() instanceof DyeItem) {
            DyeColor dyeColor = stack.get(DataComponents.DYE);
            if (dyeColor != null)
                return dyeColor;
        }

        for (int x = 0; x < DyeColor.BLACK.getId(); x++) {
            DyeColor color = DyeColor.byId(x);
            if (stack.is(getDyeTag(color)))
                return color;
        }

        return null;
    }

    public static String[] getDyeNames() {
        DyeColor[] states = DyeColor.values();
        String[] names = new String[states.length];

        for (int i = 0; i < states.length; i++) {
            names[i] = states[i].name();
        }
        return names;
    }

    /**
     * Vanilla no longer declares one static field per dyed block, they are grouped
     * into a {@link ColorCollection}. This flattens one back out into the
     * {@link DyeColor} keyed map shape the rest of Assorted expects.
     *
     * @param blocks The vanilla collection to flatten
     */
    private static Map<DyeColor, Block> byDye(ColorCollection<Block> blocks) {
        return Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
            for (DyeColor color : DyeColor.values()) {
                map.put(color, blocks.pick(color));
            }
        });
    }

    public static final Map<DyeColor, Block> WOOL_BY_DYE = byDye(Blocks.WOOL);

    public static final Map<DyeColor, Block> CONCRETE_BY_DYE = byDye(Blocks.CONCRETE);

    public static final Map<DyeColor, Block> CONCRETE_POWDER_BY_DYE = byDye(Blocks.CONCRETE_POWDER);

    public static final Map<DyeColor, Block> CARPET_BY_DYE = byDye(Blocks.CARPET);

    public static final List<Map<DyeColor, Block>> BLOCKS_BY_DYE = Arrays.asList(WOOL_BY_DYE, CONCRETE_BY_DYE, CONCRETE_POWDER_BY_DYE, CARPET_BY_DYE);
}
