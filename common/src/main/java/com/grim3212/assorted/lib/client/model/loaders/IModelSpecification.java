package com.grim3212.assorted.lib.client.model.loaders;

import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;

/**
 * General interface for any model that can be baked, superset of vanilla {@link UnbakedModel}.
 */
public interface IModelSpecification<T extends IModelSpecification<T>> {
    /**
     * Bakes this specification into a model.
     * <p>
     * The separate sprite getter argument is gone. Sprites are resolved through the baker now, either
     * from a {@link net.minecraft.client.resources.model.sprite.TextureSlots} slot with
     * {@code baker.materials().resolveSlot(slots, name, debugName)} or from a
     * {@link net.minecraft.client.resources.model.sprite.Material} with
     * {@code baker.materials().get(material, debugName)}.
     *
     * @param context       The context to bake in.
     * @param baker         The bakery to use.
     * @param modelState    The transformers to apply.
     * @param modelLocation The location of the model that is being baked.
     * @return The baked model.
     */
    BlockStateModel bake(IModelBakingContext context, ModelBaker baker, ModelState modelState, Identifier modelLocation);
}
