package com.grim3212.assorted.lib.client.model;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class DynamicBakedModel implements BakedModel {
    protected final UnbakedModel originalUnbakedModel;
    protected final BakedModel parentBakedModel;
    private final ModelBaker baker;
    private final Function<Material, TextureAtlasSprite> spriteGetter;
    private final ModelState state;
    protected final ResourceLocation location;
    protected TextureAtlasSprite currentParticle;

    public DynamicBakedModel(UnbakedModel originalModel, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, ResourceLocation location) {
        this.originalUnbakedModel = originalModel;
        this.baker = baker;
        this.spriteGetter = spriteGetter;
        this.state = state;
        this.location = location;

        this.parentBakedModel = this.originalUnbakedModel.bake(baker, spriteGetter, state, location);
        this.currentParticle = this.parentBakedModel.getParticleIcon();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        return this.parentBakedModel.getQuads(state, direction, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.parentBakedModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.parentBakedModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.parentBakedModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.parentBakedModel.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.currentParticle != null ? this.currentParticle : this.parentBakedModel.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.parentBakedModel.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.parentBakedModel.getOverrides();
    }

    public abstract ResourceLocation defaultTexture();

    protected final Map<BlockState, BakedModel> cache = new HashMap<BlockState, BakedModel>();
    protected BakedModel EMPTY;

    public BakedModel getCachedModel(BlockState blockState) {
        if (blockState == null || blockState == Blocks.AIR.defaultBlockState()) {
            if (EMPTY == null) {
                ImmutableMap.Builder<String, String> newTexture = ImmutableMap.builder();
                String texture = defaultTexture().toString();
                newTexture.put("particle", texture);
                newTexture.put("#stored", texture);
                EMPTY = generateModel(newTexture.build());
            }
            return EMPTY;
        }

        if (!this.cache.containsKey(blockState)) {
            ImmutableMap.Builder<String, String> newTexture = ImmutableMap.builder();

            String texture = "";
            if (blockState.getBlock() == Blocks.GRASS_BLOCK) {
                texture = "minecraft:block/grass_block_top";
            } else if (blockState.getBlock() == Blocks.PODZOL) {
                texture = "minecraft:block/dirt_podzol_top";
            } else if (blockState.getBlock() == Blocks.MYCELIUM) {
                texture = "minecraft:block/mycelium_top";
            } else {
                BlockModelShaper blockModel = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
                TextureAtlasSprite blockTexture = blockModel.getParticleIcon(blockState);
                texture = blockTexture.contents().name().toString();
            }

            newTexture.put("particle", texture);
            newTexture.put("#stored", texture);
            this.cache.put(blockState, generateModel(newTexture.build()));
        }

        return this.cache.get(blockState);
    }

    protected BakedModel generateModel(ImmutableMap<String, String> texture) {
        RetexturableBlockModel toBake = RetexturableBlockModel.from((BlockModel) this.originalUnbakedModel, this.location);
        return toBake.retexture(texture).bake(this.baker, this.spriteGetter, this.state, this.location);
    }
}
