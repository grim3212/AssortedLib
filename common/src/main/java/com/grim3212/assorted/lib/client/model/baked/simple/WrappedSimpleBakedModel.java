package com.grim3212.assorted.lib.client.model.baked.simple;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.grim3212.assorted.lib.client.model.RenderTypeGroup;
import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.platform.ClientServices;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class WrappedSimpleBakedModel extends SimpleBakedModel implements IDataAwareBakedModel {

    protected final List<RenderType> blockRenderTypes;
    protected final List<RenderType> itemRenderTypes;
    protected final List<RenderType> fabulousItemRenderTypes;

    public WrappedSimpleBakedModel(List<BakedQuad> $$0, Map<Direction, List<BakedQuad>> $$1, boolean $$2, boolean $$3, boolean $$4, TextureAtlasSprite $$5, ItemTransforms $$6, ItemOverrides $$7) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, RenderTypeGroup.EMPTY);
    }

    public WrappedSimpleBakedModel(List<BakedQuad> $$0, Map<Direction, List<BakedQuad>> $$1, boolean $$2, boolean $$3, boolean $$4, TextureAtlasSprite $$5, ItemTransforms $$6, ItemOverrides $$7, RenderTypeGroup renderTypeGroup) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        this.blockRenderTypes = !renderTypeGroup.isEmpty() ? List.of(renderTypeGroup.block()) : null;
        this.itemRenderTypes = !renderTypeGroup.isEmpty() ? List.of(renderTypeGroup.entity()) : null;
        this.fabulousItemRenderTypes = !renderTypeGroup.isEmpty() ? List.of(renderTypeGroup.entityFabulous()) : null;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull IBlockModelData extraData, @Nullable RenderType renderType) {
        return this.getQuads(state, side, rand);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(ItemStack stack, boolean fabulous, @NotNull RandomSource rand, @Nullable RenderType renderType) {
        return this.getQuads(null, null, rand);
    }

    @Override
    public @NotNull Collection<RenderType> getSupportedRenderTypes(BlockState state, RandomSource rand, IBlockModelData data) {
        if (blockRenderTypes != null)
            return blockRenderTypes;
        return ClientServices.MODELS.getRenderTypesFor(this, state, rand, data);
    }

    @Override
    public @NotNull Collection<RenderType> getSupportedRenderTypes(ItemStack stack, boolean fabulous) {
        if (!fabulous) {
            if (itemRenderTypes != null)
                return itemRenderTypes;
        } else {
            if (fabulousItemRenderTypes != null)
                return fabulousItemRenderTypes;
        }
        return ClientServices.MODELS.getRenderTypesFor(this, stack, fabulous);
    }

    public static class Builder {

        private final List<BakedQuad> unculledFaces;
        private final Map<Direction, List<BakedQuad>> culledFaces;
        private final ItemOverrides overrides;
        private final boolean hasAmbientOcclusion;
        private TextureAtlasSprite particleIcon;
        private final boolean usesBlockLight;
        private final boolean isGui3d;
        private final ItemTransforms transforms;

        public Builder(BlockModel $$0, ItemOverrides $$1, boolean $$2) {
            this($$0.hasAmbientOcclusion(), $$0.getGuiLight().lightLikeBlock(), $$2, $$0.getTransforms(), $$1);
        }

        public Builder(boolean $$0, boolean $$1, boolean $$2, ItemTransforms $$3, ItemOverrides $$4) {
            this.unculledFaces = Lists.newArrayList();
            this.culledFaces = Maps.newEnumMap(Direction.class);
            Direction[] var6 = Direction.values();
            int var7 = var6.length;

            for (int var8 = 0; var8 < var7; ++var8) {
                Direction $$5 = var6[var8];
                this.culledFaces.put($$5, Lists.newArrayList());
            }

            this.overrides = $$4;
            this.hasAmbientOcclusion = $$0;
            this.usesBlockLight = $$1;
            this.isGui3d = $$2;
            this.transforms = $$3;
        }

        public WrappedSimpleBakedModel.Builder addCulledFace(Direction $$0, BakedQuad $$1) {
            this.culledFaces.get($$0).add($$1);
            return this;
        }

        public WrappedSimpleBakedModel.Builder addUnculledFace(BakedQuad $$0) {
            this.unculledFaces.add($$0);
            return this;
        }

        public WrappedSimpleBakedModel.Builder particle(TextureAtlasSprite $$0) {
            this.particleIcon = $$0;
            return this;
        }

        public WrappedSimpleBakedModel.Builder item() {
            return this;
        }

        public BakedModel build() {
            return build(RenderTypeGroup.EMPTY);
        }

        public BakedModel build(RenderTypeGroup renderTypes) {
            if (this.particleIcon == null) {
                throw new RuntimeException("Missing particle!");
            } else {
                return new WrappedSimpleBakedModel(this.unculledFaces, this.culledFaces, this.hasAmbientOcclusion, this.usesBlockLight, this.isGui3d, this.particleIcon, this.transforms, this.overrides, renderTypes);
            }
        }
    }
}
