package com.grim3212.assorted.lib.util;

import com.google.common.collect.Maps;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DyeHelper {
    public static TagKey<Item> getDyeTag(DyeColor color) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(Services.PLATFORM.getCommonTagPrefix(), "dyes/" + color.getName()));
    }

    public static DyeColor getColor(ItemStack stack) {
        if (stack.getItem() instanceof DyeItem)
            return ((DyeItem) stack.getItem()).getDyeColor();

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

    public static final Map<DyeColor, Block> WOOL_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
        map.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
        map.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
        map.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
        map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
        map.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
        map.put(DyeColor.LIME, Blocks.LIME_WOOL);
        map.put(DyeColor.PINK, Blocks.PINK_WOOL);
        map.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
        map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
        map.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
        map.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
        map.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
        map.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
        map.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
        map.put(DyeColor.RED, Blocks.RED_WOOL);
        map.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
    });

    public static final Map<DyeColor, Block> CONCRETE_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
        map.put(DyeColor.WHITE, Blocks.WHITE_CONCRETE);
        map.put(DyeColor.ORANGE, Blocks.ORANGE_CONCRETE);
        map.put(DyeColor.MAGENTA, Blocks.MAGENTA_CONCRETE);
        map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CONCRETE);
        map.put(DyeColor.YELLOW, Blocks.YELLOW_CONCRETE);
        map.put(DyeColor.LIME, Blocks.LIME_CONCRETE);
        map.put(DyeColor.PINK, Blocks.PINK_CONCRETE);
        map.put(DyeColor.GRAY, Blocks.GRAY_CONCRETE);
        map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CONCRETE);
        map.put(DyeColor.CYAN, Blocks.CYAN_CONCRETE);
        map.put(DyeColor.PURPLE, Blocks.PURPLE_CONCRETE);
        map.put(DyeColor.BLUE, Blocks.BLUE_CONCRETE);
        map.put(DyeColor.BROWN, Blocks.BROWN_CONCRETE);
        map.put(DyeColor.GREEN, Blocks.GREEN_CONCRETE);
        map.put(DyeColor.RED, Blocks.RED_CONCRETE);
        map.put(DyeColor.BLACK, Blocks.BLACK_CONCRETE);
    });

    public static final Map<DyeColor, Block> CONCRETE_POWDER_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
        map.put(DyeColor.WHITE, Blocks.WHITE_CONCRETE_POWDER);
        map.put(DyeColor.ORANGE, Blocks.ORANGE_CONCRETE_POWDER);
        map.put(DyeColor.MAGENTA, Blocks.MAGENTA_CONCRETE_POWDER);
        map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CONCRETE_POWDER);
        map.put(DyeColor.YELLOW, Blocks.YELLOW_CONCRETE_POWDER);
        map.put(DyeColor.LIME, Blocks.LIME_CONCRETE_POWDER);
        map.put(DyeColor.PINK, Blocks.PINK_CONCRETE_POWDER);
        map.put(DyeColor.GRAY, Blocks.GRAY_CONCRETE_POWDER);
        map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CONCRETE_POWDER);
        map.put(DyeColor.CYAN, Blocks.CYAN_CONCRETE_POWDER);
        map.put(DyeColor.PURPLE, Blocks.PURPLE_CONCRETE_POWDER);
        map.put(DyeColor.BLUE, Blocks.BLUE_CONCRETE_POWDER);
        map.put(DyeColor.BROWN, Blocks.BROWN_CONCRETE_POWDER);
        map.put(DyeColor.GREEN, Blocks.GREEN_CONCRETE_POWDER);
        map.put(DyeColor.RED, Blocks.RED_CONCRETE_POWDER);
        map.put(DyeColor.BLACK, Blocks.BLACK_CONCRETE_POWDER);
    });

    public static final Map<DyeColor, Block> CARPET_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
        map.put(DyeColor.WHITE, Blocks.WHITE_CARPET);
        map.put(DyeColor.ORANGE, Blocks.ORANGE_CARPET);
        map.put(DyeColor.MAGENTA, Blocks.MAGENTA_CARPET);
        map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CARPET);
        map.put(DyeColor.YELLOW, Blocks.YELLOW_CARPET);
        map.put(DyeColor.LIME, Blocks.LIME_CARPET);
        map.put(DyeColor.PINK, Blocks.PINK_CARPET);
        map.put(DyeColor.GRAY, Blocks.GRAY_CARPET);
        map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CARPET);
        map.put(DyeColor.CYAN, Blocks.CYAN_CARPET);
        map.put(DyeColor.PURPLE, Blocks.PURPLE_CARPET);
        map.put(DyeColor.BLUE, Blocks.BLUE_CARPET);
        map.put(DyeColor.BROWN, Blocks.BROWN_CARPET);
        map.put(DyeColor.GREEN, Blocks.GREEN_CARPET);
        map.put(DyeColor.RED, Blocks.RED_CARPET);
        map.put(DyeColor.BLACK, Blocks.BLACK_CARPET);
    });

    public static final List<Map<DyeColor, Block>> BLOCKS_BY_DYE = Arrays.asList(WOOL_BY_DYE, CONCRETE_BY_DYE, CONCRETE_POWDER_BY_DYE, CARPET_BY_DYE);
}
