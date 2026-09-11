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
 * {@link com.grim3212.assorted.lib.client.model.loaders.IModelSpecification}.
 * <p>
 * A model json loader can only contribute geometry in 26.2, so a specification reached that way is
 * baked once, with empty model data, and flattened into a {@code QuadCollection} - which for a model
 * that varies with a block entity means it draws its "nothing stored" state everywhere. The object
 * that still sees the level and the position is the {@code BlockStateModel} the <em>blockstate</em>
 * json picks, and this is that object: it bakes the specification itself and hands back
 * {@link ForgeBakedModelDelegate} so {@code DynamicBlockStateModel} can route the model data in.
 * <p>
 * It carries a plain vanilla {@link Variant}, so the json is the same {@code model} / {@code x} /
 * {@code y} / {@code uvlock} shape a normal variant has and only the {@code type} distinguishes it.
 */
public record ForgeSpecificationBlockStateModel(Variant variant) implements CustomUnbakedBlockStateModel {

    /**
     * A vanilla variant's fields, plus Fabric's {@code "fabric:type"} naming this same type. NeoForge
     * dispatches on {@code "type"} and ignores the extra key; Fabric dispatches on {@code "fabric:type"}
     * and ignores {@code "type"}. The blockstates are generated here and read by both, so both keys are
     * written - without the second, Fabric read every specification variant as a plain one.
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
