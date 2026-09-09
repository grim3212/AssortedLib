package com.grim3212.assorted.lib.client.model.loaders;

import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.UnbakedModel;

/**
 * Implemented by the loader specific {@link UnbakedModel} that a model json's {@code loader} key
 * produces, so the specification inside it can be reached again from a {@link ModelBaker}.
 * <p>
 * Needed because a specification is only half useful through the model json pipeline. That path can
 * hand back geometry and nothing else, so a specification whose output varies with block entity data
 * collapses to its empty-data output there (see
 * {@code ForgeModelGeometryToSpecificationPlatformDelegator}). Reaching the specification through
 * {@link ResolvedModel#wrapped()} instead lets the <em>blockstate</em> side bake it into a whole
 * {@code BlockStateModel}, which is the only layer in 26.2 that still sees the level and the
 * position.
 */
public interface IModelSpecificationHolder {

    /**
     * The specification this unbaked model was built from.
     */
    IModelSpecification<?> getModelSpecification();
}
