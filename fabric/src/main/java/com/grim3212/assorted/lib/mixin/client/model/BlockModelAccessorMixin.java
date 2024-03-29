package com.grim3212.assorted.lib.mixin.client.model;

import com.grim3212.assorted.lib.client.model.IBlockModelAccessor;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@Mixin(BlockModel.class)
public abstract class BlockModelAccessorMixin implements IBlockModelAccessor {
    @Shadow
    public String name;
    @Shadow
    @Final
    protected Map<String, Either<Material, String>> textureMap;
    @Shadow
    @Nullable
    protected BlockModel parent;
    @Shadow
    @Nullable
    protected ResourceLocation parentLocation;

    @Shadow
    protected abstract ItemOverrides getItemOverrides(ModelBaker baker, BlockModel model);

    @Shadow
    public abstract List<BlockElement> getElements();

    @Shadow
    public abstract BlockModel.GuiLight getGuiLight();

    @Shadow
    public abstract boolean hasAmbientOcclusion();

    @Shadow
    public abstract ItemTransforms getTransforms();

    @Shadow
    public abstract List<ItemOverride> getOverrides();

    @Override
    public List<BlockElement> elements() {
        return getElements();
    }

    @Override
    public BlockModel.GuiLight guiLight() {
        return getGuiLight();
    }

    @Override
    public boolean usesAmbientOcclusion() {
        return hasAmbientOcclusion();
    }

    @Override
    public ItemTransforms transforms() {
        return getTransforms();
    }

    @Override
    public List<ItemOverride> overrides() {
        return getOverrides();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Map<String, Either<Material, String>> textureMap() {
        return textureMap;
    }

    @Override
    public BlockModel parent() {
        return parent;
    }

    @Override
    public ResourceLocation parentLocation() {
        return parentLocation;
    }

    @Override
    public ItemOverrides overrides(final ModelBaker modelBaker, final BlockModel model) {
        return getItemOverrides(modelBaker, model);
    }
}
