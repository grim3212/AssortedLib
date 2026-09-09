package com.grim3212.assorted.lib.client.model.loader;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;

/**
 * The blockstate side entry point for an
 * {@link com.grim3212.assorted.lib.client.model.loaders.IModelSpecification}, Fabric half.
 * <p>
 * Same job and same json shape as {@code ForgeSpecificationBlockStateModel} - the blockstate jsons
 * are generated once and read by both loaders, so both have to answer to
 * {@link com.grim3212.assorted.lib.client.model.block.LibBlockStateModels#SPECIFICATION} with the
 * same fields. Only the interface differs: {@code CustomUnbakedBlockStateModel} is Fabric's own here
 * and NeoForge's there, which is why this cannot be one shared class.
 */
public record FabricSpecificationBlockStateModel(Variant variant) implements CustomUnbakedBlockStateModel {

    public static final MapCodec<FabricSpecificationBlockStateModel> MAP_CODEC =
            Variant.MAP_CODEC.xmap(FabricSpecificationBlockStateModel::new, FabricSpecificationBlockStateModel::variant);

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
