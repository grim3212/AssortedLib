package com.grim3212.assorted.lib.client.model.loaders;

import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;

/**
 * General interface for any model that can be baked, superset of vanilla {@link UnbakedModel}.
 */
public interface IModelSpecification<T extends IModelSpecification<T>> {
    /**
     * Bakes this specification into a model. Resolve sprites through the baker:
     * {@code baker.materials().resolveSlot(slots, name, debugName)} for a texture slot, or
     * {@code baker.materials().get(material, debugName)} for a material.
     */
    BlockStateModel bake(IModelBakingContext context, ModelBaker baker, ModelState modelState, Identifier modelLocation);

    /**
     * Marks every model this specification looks up through {@link ModelBaker#getModel(Identifier)}
     * while baking. Discovery does not scan the resource pack, so an unmarked model bakes to the
     * missing model and logs {@code Requested a model that was not discovered previously}.
     */
    default void resolveDependencies(ResolvableModel.Resolver resolver) {
    }
}
