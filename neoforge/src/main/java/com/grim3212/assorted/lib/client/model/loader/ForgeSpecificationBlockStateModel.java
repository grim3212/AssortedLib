package com.grim3212.assorted.lib.client.model.loader;

import com.grim3212.assorted.lib.client.model.block.LibBlockStateModels;
import com.grim3212.assorted.lib.platform.ClientServices;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;

import java.util.Optional;

/**
 * The blockstate side entry point for an
 * {@link com.grim3212.assorted.lib.client.model.loaders.IModelSpecification}: through a model json
 * loader it is baked once with empty data, while the blockstate's model still sees level and
 * position. So this bakes the specification itself and returns a {@link ForgeBakedModelDelegate}.
 * Its json is a vanilla {@link Variant} plus the {@code type}.
 */
public record ForgeSpecificationBlockStateModel(Variant variant) implements CustomUnbakedBlockStateModel {

    /**
     * A vanilla variant's fields plus {@code "fabric:type"} naming this same type: NeoForge
     * dispatches on {@code "type"}, Fabric only on {@code "fabric:type"}, and both read these
     * blockstates.
     */
    public static final MapCodec<ForgeSpecificationBlockStateModel> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Variant.MAP_CODEC.forGetter(ForgeSpecificationBlockStateModel::variant),
            Identifier.CODEC.optionalFieldOf(LibBlockStateModels.FABRIC_TYPE_KEY).forGetter(model -> Optional.of(LibBlockStateModels.SPECIFICATION))
    ).apply(instance, (variant, fabricType) -> new ForgeSpecificationBlockStateModel(variant)));

    @Override
    public BlockStateModel bake(ModelBaker baker) {
        return ClientServices.CLIENT.bakeSpecificationModel(baker, this.variant.modelLocation(), this.variant.modelState().asModelState());
    }

    @Override
    public void resolveDependencies(ResolvableModel.Resolver resolver) {
        this.variant.resolveDependencies(resolver);
    }

    @Override
    public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
        return MAP_CODEC;
    }
}
