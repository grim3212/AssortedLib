package com.grim3212.assorted.lib.client.model.loaders;

import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.UnbakedModel;

/**
 * Implemented by the loader specific {@link UnbakedModel} a model json's {@code loader} produces,
 * so its specification can be reached from a {@link ModelBaker} via {@link
 * ResolvedModel#wrapped()}. The model json path yields only geometry baked with empty data; the
 * blockstate side uses this to bake the specification into a whole {@code BlockStateModel}, which
 * still sees level and position.
 */
public interface IModelSpecificationHolder {

    /**
     * The specification this unbaked model was built from.
     */
    IModelSpecification<?> getModelSpecification();
}
