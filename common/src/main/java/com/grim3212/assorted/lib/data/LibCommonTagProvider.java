package com.grim3212.assorted.lib.data;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.util.DyeHelper;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class LibCommonTagProvider {

    public static class BlockTagProvider extends VanillaBlockTagsProvider {

        public BlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
            super(packOutput, lookup);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            throw new NotImplementedException();
        }

        @Override
        protected IntrinsicTagAppender<Block> tag(TagKey<Block> tag) {
            throw new NotImplementedException();
        }

        public void addCommonTags(Function<TagKey<Block>, IntrinsicTagAppender<Block>> tagger, boolean isForge) {
            if (!isForge) {
                tagger.apply(LibCommonTags.Blocks.BARRELS).addTag(LibCommonTags.Blocks.BARRELS_WOODEN);
                tagger.apply(LibCommonTags.Blocks.BARRELS_WOODEN).add(Blocks.BARREL);
                tagger.apply(LibCommonTags.Blocks.BOOKSHELVES).add(Blocks.BOOKSHELF);
                tagger.apply(LibCommonTags.Blocks.CHESTS).addTag(LibCommonTags.Blocks.CHESTS_ENDER);
                tagger.apply(LibCommonTags.Blocks.CHESTS).addTag(LibCommonTags.Blocks.CHESTS_TRAPPED);
                tagger.apply(LibCommonTags.Blocks.CHESTS).addTag(LibCommonTags.Blocks.CHESTS_WOODEN);
                tagger.apply(LibCommonTags.Blocks.CHESTS_ENDER).add(Blocks.ENDER_CHEST);
                tagger.apply(LibCommonTags.Blocks.CHESTS_TRAPPED).add(Blocks.TRAPPED_CHEST);
                tagger.apply(LibCommonTags.Blocks.CHESTS_WOODEN).add(Blocks.CHEST, Blocks.TRAPPED_CHEST);
                tagger.apply(LibCommonTags.Blocks.COBBLESTONE).add(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE, Blocks.MOSSY_COBBLESTONE, Blocks.COBBLED_DEEPSLATE);
                tagger.apply(LibCommonTags.Blocks.END_STONES).add(Blocks.END_STONE);
                tagger.apply(LibCommonTags.Blocks.FENCE_GATES).addTag(LibCommonTags.Blocks.FENCE_GATES_WOODEN);
                tagger.apply(LibCommonTags.Blocks.FENCE_GATES_WOODEN).add(Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.CRIMSON_FENCE_GATE, Blocks.WARPED_FENCE_GATE, Blocks.MANGROVE_FENCE_GATE);
                tagger.apply(LibCommonTags.Blocks.FENCES).addTag(LibCommonTags.Blocks.FENCES_WOODEN);
                tagger.apply(LibCommonTags.Blocks.FENCES).addTag(LibCommonTags.Blocks.FENCES_NETHER_BRICK);
                tagger.apply(LibCommonTags.Blocks.FENCES_NETHER_BRICK).add(Blocks.NETHER_BRICK_FENCE);
                tagger.apply(LibCommonTags.Blocks.FENCES_WOODEN).addOptionalTag(BlockTags.WOODEN_FENCES.location());
                tagger.apply(LibCommonTags.Blocks.GRAVEL).add(Blocks.GRAVEL);
                tagger.apply(LibCommonTags.Blocks.NETHERRACK).add(Blocks.NETHERRACK);
                tagger.apply(LibCommonTags.Blocks.OBSIDIAN).add(Blocks.OBSIDIAN);

                List<TagKey<Block>> oreTags = Arrays.asList(LibCommonTags.Blocks.ORES_COAL, LibCommonTags.Blocks.ORES_COPPER, LibCommonTags.Blocks.ORES_DIAMOND, LibCommonTags.Blocks.ORES_EMERALD, LibCommonTags.Blocks.ORES_GOLD, LibCommonTags.Blocks.ORES_IRON, LibCommonTags.Blocks.ORES_LAPIS, LibCommonTags.Blocks.ORES_REDSTONE, LibCommonTags.Blocks.ORES_QUARTZ, LibCommonTags.Blocks.ORES_NETHERITE_SCRAP);
                for (TagKey<Block> oreTag : oreTags)
                    tagger.apply(LibCommonTags.Blocks.ORES).addTag(oreTag);

                tagger.apply(LibCommonTags.Blocks.ORES_COAL).addOptionalTag(BlockTags.COAL_ORES.location());
                tagger.apply(LibCommonTags.Blocks.ORES_COPPER).addOptionalTag(BlockTags.COPPER_ORES.location());
                tagger.apply(LibCommonTags.Blocks.ORES_DIAMOND).addOptionalTag(BlockTags.DIAMOND_ORES.location());
                tagger.apply(LibCommonTags.Blocks.ORES_EMERALD).addOptionalTag(BlockTags.EMERALD_ORES.location());
                tagger.apply(LibCommonTags.Blocks.ORES_GOLD).addOptionalTag(BlockTags.GOLD_ORES.location());
                tagger.apply(LibCommonTags.Blocks.ORES_IRON).addOptionalTag(BlockTags.IRON_ORES.location());
                tagger.apply(LibCommonTags.Blocks.ORES_LAPIS).addOptionalTag(BlockTags.LAPIS_ORES.location());
                tagger.apply(LibCommonTags.Blocks.ORES_QUARTZ).add(Blocks.NETHER_QUARTZ_ORE);
                tagger.apply(LibCommonTags.Blocks.ORES_REDSTONE).addOptionalTag(BlockTags.REDSTONE_ORES.location());
                tagger.apply(LibCommonTags.Blocks.ORES_NETHERITE_SCRAP).add(Blocks.ANCIENT_DEBRIS);
                tagger.apply(LibCommonTags.Blocks.STONE).add(Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.INFESTED_STONE, Blocks.STONE, Blocks.POLISHED_ANDESITE, Blocks.POLISHED_DIORITE, Blocks.POLISHED_GRANITE, Blocks.DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.INFESTED_DEEPSLATE, Blocks.TUFF);

                List<TagKey<Block>> storageTags = Arrays.asList(LibCommonTags.Blocks.STORAGE_BLOCKS_AMETHYST, LibCommonTags.Blocks.STORAGE_BLOCKS_COAL, LibCommonTags.Blocks.STORAGE_BLOCKS_COPPER, LibCommonTags.Blocks.STORAGE_BLOCKS_DIAMOND, LibCommonTags.Blocks.STORAGE_BLOCKS_EMERALD, LibCommonTags.Blocks.STORAGE_BLOCKS_GOLD, LibCommonTags.Blocks.STORAGE_BLOCKS_IRON, LibCommonTags.Blocks.STORAGE_BLOCKS_LAPIS, LibCommonTags.Blocks.STORAGE_BLOCKS_QUARTZ, LibCommonTags.Blocks.STORAGE_BLOCKS_RAW_COPPER, LibCommonTags.Blocks.STORAGE_BLOCKS_RAW_GOLD, LibCommonTags.Blocks.STORAGE_BLOCKS_RAW_IRON, LibCommonTags.Blocks.STORAGE_BLOCKS_REDSTONE, LibCommonTags.Blocks.STORAGE_BLOCKS_NETHERITE);
                for (TagKey<Block> storageTag : storageTags)
                    tagger.apply(LibCommonTags.Blocks.STORAGE_BLOCKS).addTag(storageTag);

                tagger.apply(LibCommonTags.Blocks.STORAGE_BLOCKS_AMETHYST).add(Blocks.AMETHYST_BLOCK);
                tagger.apply(LibCommonTags.Blocks.STORAGE_BLOCKS_COAL).add(Blocks.COAL_BLOCK);
                tagger.apply(LibCommonTags.Blocks.STORAGE_BLOCKS_COPPER).add(Blocks.COPPER_BLOCK);
                tagger.apply(LibCommonTags.Blocks.STORAGE_BLOCKS_DIAMOND).add(Blocks.DIAMOND_BLOCK);
                tagger.apply(LibCommonTags.Blocks.STORAGE_BLOCKS_EMERALD).add(Blocks.EMERALD_BLOCK);
                tagger.apply(LibCommonTags.Blocks.STORAGE_BLOCKS_GOLD).add(Blocks.GOLD_BLOCK);
                tagger.apply(LibCommonTags.Blocks.STORAGE_BLOCKS_IRON).add(Blocks.IRON_BLOCK);
                tagger.apply(LibCommonTags.Blocks.STORAGE_BLOCKS_LAPIS).add(Blocks.LAPIS_BLOCK);
                tagger.apply(LibCommonTags.Blocks.STORAGE_BLOCKS_QUARTZ).add(Blocks.QUARTZ_BLOCK);
                tagger.apply(LibCommonTags.Blocks.STORAGE_BLOCKS_RAW_COPPER).add(Blocks.RAW_COPPER_BLOCK);
                tagger.apply(LibCommonTags.Blocks.STORAGE_BLOCKS_RAW_GOLD).add(Blocks.RAW_GOLD_BLOCK);
                tagger.apply(LibCommonTags.Blocks.STORAGE_BLOCKS_RAW_IRON).add(Blocks.RAW_IRON_BLOCK);
                tagger.apply(LibCommonTags.Blocks.STORAGE_BLOCKS_REDSTONE).add(Blocks.REDSTONE_BLOCK);
                tagger.apply(LibCommonTags.Blocks.STORAGE_BLOCKS_NETHERITE).add(Blocks.NETHERITE_BLOCK);

                addColored(tagger.apply(LibCommonTags.Blocks.STAINED_GLASS)::add, LibCommonTags.Blocks.GLASS, "{color}_stained_glass", tagger);
                addColored(tagger.apply(LibCommonTags.Blocks.STAINED_GLASS_PANES)::add, LibCommonTags.Blocks.GLASS_PANES, "{color}_stained_glass_pane", tagger);

                tagger.apply(LibCommonTags.Blocks.GLASS).addTag(LibCommonTags.Blocks.GLASS_COLORLESS).addOptionalTag(LibCommonTags.Blocks.STAINED_GLASS.location()).addTag(LibCommonTags.Blocks.GLASS_TINTED);
                tagger.apply(LibCommonTags.Blocks.GLASS_COLORLESS).add(Blocks.GLASS);
                tagger.apply(LibCommonTags.Blocks.GLASS_TINTED).add(Blocks.TINTED_GLASS);
                tagger.apply(LibCommonTags.Blocks.GLASS_PANES).addTag(LibCommonTags.Blocks.GLASS_PANES_COLORLESS).addOptionalTag(LibCommonTags.Blocks.STAINED_GLASS_PANES.location());
                tagger.apply(LibCommonTags.Blocks.GLASS_PANES_COLORLESS).add(Blocks.GLASS_PANE);
            }

            DyeHelper.CONCRETE_BY_DYE.entrySet().stream().forEach((x) -> tagger.apply(LibCommonTags.Blocks.CONCRETE).add(x.getValue()));
            DyeHelper.CONCRETE_POWDER_BY_DYE.entrySet().stream().forEach((x) -> tagger.apply(LibCommonTags.Blocks.CONCRETE_POWDER).add(x.getValue()));
            DyeHelper.CARPET_BY_DYE.entrySet().stream().forEach((x) -> tagger.apply(LibCommonTags.Blocks.CARPET).add(x.getValue()));
        }

        private void addColored(Consumer<Block> consumer, TagKey<Block> group, String pattern, Function<TagKey<Block>, IntrinsicTagAppender<Block>> tagger) {
            String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
            for (DyeColor color : DyeColor.values()) {
                ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}", color.getName()));
                TagKey<Block> tag = getCommonTag(prefix + color.getName());
                Block block = Services.PLATFORM.getRegistry(Registries.BLOCK).getValue(key).get();
                if (block == null || block == Blocks.AIR)
                    throw new IllegalStateException("Unknown block: " + key.toString());
                tagger.apply(tag).add(block);
                consumer.accept(block);
            }
        }

        private TagKey<Block> getCommonTag(String name) {
            try {
                name = name.toUpperCase(Locale.ENGLISH);
                return (TagKey<Block>) LibCommonTags.Blocks.class.getDeclaredField(name).get(null);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                throw new IllegalStateException(LibCommonTags.Blocks.class.getName() + " is missing tag name: " + name);
            }
        }
    }

    public static class ItemTagProvider extends VanillaItemTagsProvider {

        public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, TagsProvider<Block> blockTags) {
            super(output, lookup, blockTags);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            throw new NotImplementedException();
        }

        @Override
        protected IntrinsicTagAppender<Item> tag(TagKey<Item> tag) {
            throw new NotImplementedException();
        }

        @Override
        protected void copy(TagKey<Block> blockTag, TagKey<Item> itemTag) {
            throw new NotImplementedException();
        }

        public void addCommonTags(Function<TagKey<Item>, IntrinsicTagAppender<Item>> tagger, Consumer<Tuple<TagKey<Block>, TagKey<Item>>> copier, boolean isForge) {
            if (!isForge) {
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STONE, LibCommonTags.Items.STONE));

                for (DyeColor color : DyeColor.values()) {
                    TagKey<Item> dyeTag = DyeHelper.getDyeTag(color);
                    tagger.apply(LibCommonTags.Items.DYES).addTag(dyeTag);
                    tagger.apply(dyeTag).add(DyeItem.byColor(color));
                }

                copier.accept(new Tuple<>(LibCommonTags.Blocks.BARRELS, LibCommonTags.Items.BARRELS));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.BARRELS_WOODEN, LibCommonTags.Items.BARRELS_WOODEN));
                tagger.apply(LibCommonTags.Items.BONES).add(Items.BONE);
                copier.accept(new Tuple<>(LibCommonTags.Blocks.BOOKSHELVES, LibCommonTags.Items.BOOKSHELVES));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.CHESTS, LibCommonTags.Items.CHESTS));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.CHESTS_ENDER, LibCommonTags.Items.CHESTS_ENDER));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.CHESTS_TRAPPED, LibCommonTags.Items.CHESTS_TRAPPED));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.CHESTS_WOODEN, LibCommonTags.Items.CHESTS_WOODEN));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.COBBLESTONE, LibCommonTags.Items.COBBLESTONE));
                tagger.apply(LibCommonTags.Items.DUSTS).addTag(LibCommonTags.Items.DUSTS_GLOWSTONE);
                tagger.apply(LibCommonTags.Items.DUSTS).addTag(LibCommonTags.Items.DUSTS_PRISMARINE);
                tagger.apply(LibCommonTags.Items.DUSTS).addTag(LibCommonTags.Items.DUSTS_REDSTONE);
                tagger.apply(LibCommonTags.Items.DUSTS_GLOWSTONE).add(Items.GLOWSTONE_DUST);
                tagger.apply(LibCommonTags.Items.DUSTS_PRISMARINE).add(Items.PRISMARINE_SHARD);
                tagger.apply(LibCommonTags.Items.DUSTS_REDSTONE).add(Items.REDSTONE);
                tagger.apply(LibCommonTags.Items.EGGS).add(Items.EGG);
                copier.accept(new Tuple<>(LibCommonTags.Blocks.END_STONES, LibCommonTags.Items.END_STONES));
                tagger.apply(LibCommonTags.Items.ENDER_PEARLS).add(Items.ENDER_PEARL);
                tagger.apply(LibCommonTags.Items.FEATHERS).add(Items.FEATHER);
                copier.accept(new Tuple<>(LibCommonTags.Blocks.FENCE_GATES, LibCommonTags.Items.FENCE_GATES));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.FENCE_GATES_WOODEN, LibCommonTags.Items.FENCE_GATES_WOODEN));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.FENCES, LibCommonTags.Items.FENCES));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.FENCES_NETHER_BRICK, LibCommonTags.Items.FENCES_NETHER_BRICK));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.FENCES_WOODEN, LibCommonTags.Items.FENCES_WOODEN));

                List<TagKey<Item>> gemTags = Arrays.asList(LibCommonTags.Items.GEMS_AMETHYST, LibCommonTags.Items.GEMS_DIAMOND, LibCommonTags.Items.GEMS_EMERALD, LibCommonTags.Items.GEMS_LAPIS, LibCommonTags.Items.GEMS_PRISMARINE, LibCommonTags.Items.GEMS_QUARTZ);
                for (TagKey<Item> gemTag : gemTags)
                    tagger.apply(LibCommonTags.Items.GEMS).addTag(gemTag);

                tagger.apply(LibCommonTags.Items.GEMS_AMETHYST).add(Items.AMETHYST_SHARD);
                tagger.apply(LibCommonTags.Items.GEMS_DIAMOND).add(Items.DIAMOND);
                tagger.apply(LibCommonTags.Items.GEMS_EMERALD).add(Items.EMERALD);
                tagger.apply(LibCommonTags.Items.GEMS_LAPIS).add(Items.LAPIS_LAZULI);
                tagger.apply(LibCommonTags.Items.GEMS_PRISMARINE).add(Items.PRISMARINE_CRYSTALS);
                tagger.apply(LibCommonTags.Items.GEMS_QUARTZ).add(Items.QUARTZ);
                copier.accept(new Tuple<>(LibCommonTags.Blocks.GRAVEL, LibCommonTags.Items.GRAVEL));
                tagger.apply(LibCommonTags.Items.GUNPOWDER).add(Items.GUNPOWDER);

                List<TagKey<Item>> ingotTags = Arrays.asList(LibCommonTags.Items.INGOTS_BRICK, LibCommonTags.Items.INGOTS_COPPER, LibCommonTags.Items.INGOTS_GOLD, LibCommonTags.Items.INGOTS_IRON, LibCommonTags.Items.INGOTS_NETHERITE, LibCommonTags.Items.INGOTS_NETHER_BRICK);
                for (TagKey<Item> ingotTag : ingotTags)
                    tagger.apply(LibCommonTags.Items.INGOTS).addTag(ingotTag);

                tagger.apply(LibCommonTags.Items.INGOTS_BRICK).add(Items.BRICK);
                tagger.apply(LibCommonTags.Items.INGOTS_COPPER).add(Items.COPPER_INGOT);
                tagger.apply(LibCommonTags.Items.INGOTS_GOLD).add(Items.GOLD_INGOT);
                tagger.apply(LibCommonTags.Items.INGOTS_IRON).add(Items.IRON_INGOT);
                tagger.apply(LibCommonTags.Items.INGOTS_NETHERITE).add(Items.NETHERITE_INGOT);
                tagger.apply(LibCommonTags.Items.INGOTS_NETHER_BRICK).add(Items.NETHER_BRICK);
                tagger.apply(LibCommonTags.Items.LEATHER).add(Items.LEATHER);
                tagger.apply(LibCommonTags.Items.NETHER_STARS).add(Items.NETHER_STAR);
                copier.accept(new Tuple<>(LibCommonTags.Blocks.NETHERRACK, LibCommonTags.Items.NETHERRACK));
                tagger.apply(LibCommonTags.Items.NUGGETS).addTag(LibCommonTags.Items.NUGGETS_GOLD);
                tagger.apply(LibCommonTags.Items.NUGGETS).addTag(LibCommonTags.Items.NUGGETS_IRON);
                tagger.apply(LibCommonTags.Items.NUGGETS_IRON).add(Items.IRON_NUGGET);
                tagger.apply(LibCommonTags.Items.NUGGETS_GOLD).add(Items.GOLD_NUGGET);
                copier.accept(new Tuple<>(LibCommonTags.Blocks.OBSIDIAN, LibCommonTags.Items.OBSIDIAN));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.ORES, LibCommonTags.Items.ORES));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.ORES_COAL, LibCommonTags.Items.ORES_COAL));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.ORES_COPPER, LibCommonTags.Items.ORES_COPPER));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.ORES_DIAMOND, LibCommonTags.Items.ORES_DIAMOND));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.ORES_EMERALD, LibCommonTags.Items.ORES_EMERALD));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.ORES_GOLD, LibCommonTags.Items.ORES_GOLD));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.ORES_IRON, LibCommonTags.Items.ORES_IRON));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.ORES_LAPIS, LibCommonTags.Items.ORES_LAPIS));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.ORES_QUARTZ, LibCommonTags.Items.ORES_QUARTZ));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.ORES_REDSTONE, LibCommonTags.Items.ORES_REDSTONE));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.ORES_NETHERITE_SCRAP, LibCommonTags.Items.ORES_NETHERITE_SCRAP));
                tagger.apply(LibCommonTags.Items.RAW_MATERIALS).addTag(LibCommonTags.Items.RAW_MATERIALS_COPPER);
                tagger.apply(LibCommonTags.Items.RAW_MATERIALS).addTag(LibCommonTags.Items.RAW_MATERIALS_GOLD);
                tagger.apply(LibCommonTags.Items.RAW_MATERIALS).addTag(LibCommonTags.Items.RAW_MATERIALS_IRON);
                tagger.apply(LibCommonTags.Items.RAW_MATERIALS_COPPER).add(Items.RAW_COPPER);
                tagger.apply(LibCommonTags.Items.RAW_MATERIALS_GOLD).add(Items.RAW_GOLD);
                tagger.apply(LibCommonTags.Items.RAW_MATERIALS_IRON).add(Items.RAW_IRON);
                tagger.apply(LibCommonTags.Items.RODS).addTag(LibCommonTags.Items.RODS_WOODEN);
                tagger.apply(LibCommonTags.Items.RODS).addTag(LibCommonTags.Items.RODS_BLAZE);
                tagger.apply(LibCommonTags.Items.RODS_BLAZE).add(Items.BLAZE_ROD);
                tagger.apply(LibCommonTags.Items.RODS_WOODEN).add(Items.STICK);
                tagger.apply(LibCommonTags.Items.SHEARS).add(Items.SHEARS);
                tagger.apply(LibCommonTags.Items.SLIMEBALLS).add(Items.SLIME_BALL);
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STORAGE_BLOCKS, LibCommonTags.Items.STORAGE_BLOCKS));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STORAGE_BLOCKS_AMETHYST, LibCommonTags.Items.STORAGE_BLOCKS_AMETHYST));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STORAGE_BLOCKS_COAL, LibCommonTags.Items.STORAGE_BLOCKS_COAL));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STORAGE_BLOCKS_COPPER, LibCommonTags.Items.STORAGE_BLOCKS_COPPER));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STORAGE_BLOCKS_DIAMOND, LibCommonTags.Items.STORAGE_BLOCKS_DIAMOND));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STORAGE_BLOCKS_EMERALD, LibCommonTags.Items.STORAGE_BLOCKS_EMERALD));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STORAGE_BLOCKS_GOLD, LibCommonTags.Items.STORAGE_BLOCKS_GOLD));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STORAGE_BLOCKS_IRON, LibCommonTags.Items.STORAGE_BLOCKS_IRON));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STORAGE_BLOCKS_LAPIS, LibCommonTags.Items.STORAGE_BLOCKS_LAPIS));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STORAGE_BLOCKS_QUARTZ, LibCommonTags.Items.STORAGE_BLOCKS_QUARTZ));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STORAGE_BLOCKS_REDSTONE, LibCommonTags.Items.STORAGE_BLOCKS_REDSTONE));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STORAGE_BLOCKS_RAW_COPPER, LibCommonTags.Items.STORAGE_BLOCKS_RAW_COPPER));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STORAGE_BLOCKS_RAW_GOLD, LibCommonTags.Items.STORAGE_BLOCKS_RAW_GOLD));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STORAGE_BLOCKS_RAW_IRON, LibCommonTags.Items.STORAGE_BLOCKS_RAW_IRON));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.STORAGE_BLOCKS_NETHERITE, LibCommonTags.Items.STORAGE_BLOCKS_NETHERITE));
                tagger.apply(LibCommonTags.Items.STRING).add(Items.STRING);
                tagger.apply(LibCommonTags.Items.TOOLS_SWORDS).add(Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
                tagger.apply(LibCommonTags.Items.TOOLS_AXES).add(Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.GOLDEN_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
                tagger.apply(LibCommonTags.Items.TOOLS_PICKAXES).add(Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
                tagger.apply(LibCommonTags.Items.TOOLS_SHOVELS).add(Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL, Items.GOLDEN_SHOVEL, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
                tagger.apply(LibCommonTags.Items.TOOLS_HOES).add(Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.GOLDEN_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE);
                tagger.apply(LibCommonTags.Items.TOOLS_SHIELDS).add(Items.SHIELD);
                tagger.apply(LibCommonTags.Items.TOOLS_BOWS).add(Items.BOW);
                tagger.apply(LibCommonTags.Items.TOOLS_CROSSBOWS).add(Items.CROSSBOW);
                tagger.apply(LibCommonTags.Items.TOOLS_FISHING_RODS).add(Items.FISHING_ROD);
                tagger.apply(LibCommonTags.Items.TOOLS_TRIDENTS).add(Items.TRIDENT);

                List<TagKey<Item>> toolTags = Arrays.asList(LibCommonTags.Items.TOOLS_SWORDS, LibCommonTags.Items.TOOLS_AXES, LibCommonTags.Items.TOOLS_PICKAXES, LibCommonTags.Items.TOOLS_SHOVELS, LibCommonTags.Items.TOOLS_HOES, LibCommonTags.Items.TOOLS_SHIELDS, LibCommonTags.Items.TOOLS_BOWS, LibCommonTags.Items.TOOLS_CROSSBOWS, LibCommonTags.Items.TOOLS_FISHING_RODS, LibCommonTags.Items.TOOLS_TRIDENTS);
                for (TagKey<Item> toolTag : toolTags)
                    tagger.apply(LibCommonTags.Items.TOOLS).addTag(toolTag);

                tagger.apply(LibCommonTags.Items.ARMORS_HELMETS).add(Items.LEATHER_HELMET, Items.TURTLE_HELMET, Items.CHAINMAIL_HELMET, Items.IRON_HELMET, Items.GOLDEN_HELMET, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET);
                tagger.apply(LibCommonTags.Items.ARMORS_CHESTPLATES).add(Items.LEATHER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);
                tagger.apply(LibCommonTags.Items.ARMORS_LEGGINGS).add(Items.LEATHER_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.IRON_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);
                tagger.apply(LibCommonTags.Items.ARMORS_BOOTS).add(Items.LEATHER_BOOTS, Items.CHAINMAIL_BOOTS, Items.IRON_BOOTS, Items.GOLDEN_BOOTS, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS);

                List<TagKey<Item>> armorTags = Arrays.asList(LibCommonTags.Items.ARMORS_HELMETS, LibCommonTags.Items.ARMORS_CHESTPLATES, LibCommonTags.Items.ARMORS_LEGGINGS, LibCommonTags.Items.ARMORS_BOOTS);
                for (TagKey<Item> armorTag : armorTags)
                    tagger.apply(LibCommonTags.Items.ARMORS).addTag(armorTag);

                copier.accept(new Tuple<>(LibCommonTags.Blocks.GLASS, LibCommonTags.Items.GLASS));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.GLASS_TINTED, LibCommonTags.Items.GLASS_TINTED));
                copier.accept(new Tuple<>(LibCommonTags.Blocks.GLASS_PANES, LibCommonTags.Items.GLASS_PANES));
                copyColored(LibCommonTags.Blocks.GLASS, LibCommonTags.Items.GLASS, copier);
                copyColored(LibCommonTags.Blocks.GLASS_PANES, LibCommonTags.Items.GLASS_PANES, copier);

                tagger.apply(LibCommonTags.Items.CROPS).addTag(LibCommonTags.Items.CROPS_BEETROOT).addTag(LibCommonTags.Items.CROPS_CARROT).addTag(LibCommonTags.Items.CROPS_NETHER_WART).addTag(LibCommonTags.Items.CROPS_POTATO).addTag(LibCommonTags.Items.CROPS_WHEAT);
                tagger.apply(LibCommonTags.Items.CROPS_BEETROOT).add(Items.BEETROOT);
                tagger.apply(LibCommonTags.Items.CROPS_CARROT).add(Items.CARROT);
                tagger.apply(LibCommonTags.Items.CROPS_NETHER_WART).add(Items.NETHER_WART);
                tagger.apply(LibCommonTags.Items.CROPS_POTATO).add(Items.POTATO);
                tagger.apply(LibCommonTags.Items.CROPS_WHEAT).add(Items.WHEAT);
            }

            copier.accept(new Tuple<>(LibCommonTags.Blocks.CONCRETE, LibCommonTags.Items.CONCRETE));
            copier.accept(new Tuple<>(LibCommonTags.Blocks.CONCRETE_POWDER, LibCommonTags.Items.CONCRETE_POWDER));
            copier.accept(new Tuple<>(LibCommonTags.Blocks.CARPET, LibCommonTags.Items.CARPET));
            tagger.apply(LibCommonTags.Items.FLUID_CONTAINERS).add(Items.BUCKET, Items.WATER_BUCKET, Items.LAVA_BUCKET);
            tagger.apply(LibCommonTags.Items.BUCKETS_MILK).add(Items.MILK_BUCKET);
        }

        private void copyColored(TagKey<Block> blockGroup, TagKey<Item> itemGroup, Consumer<Tuple<TagKey<Block>, TagKey<Item>>> copier) {
            String blockPre = blockGroup.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
            String itemPre = itemGroup.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
            for (DyeColor color : DyeColor.values()) {
                TagKey<Block> from = getCommonBlockTag(blockPre + color.getName());
                TagKey<Item> to = getCommonItemTag(itemPre + color.getName());
                copier.accept(new Tuple<>(from, to));
            }
            copier.accept(new Tuple<>(getCommonBlockTag(blockPre + "colorless"), getCommonItemTag(itemPre + "colorless")));
        }

        private TagKey<Block> getCommonBlockTag(String name) {
            try {
                name = name.toUpperCase(Locale.ENGLISH);
                return (TagKey<Block>) LibCommonTags.Blocks.class.getDeclaredField(name).get(null);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                throw new IllegalStateException(LibCommonTags.Blocks.class.getName() + " is missing tag name: " + name);
            }
        }

        private TagKey<Item> getCommonItemTag(String name) {
            try {
                name = name.toUpperCase(Locale.ENGLISH);
                return (TagKey<Item>) LibCommonTags.Items.class.getDeclaredField(name).get(null);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                throw new IllegalStateException(LibCommonTags.Items.class.getName() + " is missing tag name: " + name);
            }
        }
    }
}
