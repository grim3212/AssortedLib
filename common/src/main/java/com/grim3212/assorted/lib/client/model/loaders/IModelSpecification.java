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

    /**
     * Declares any other models this specification looks up through
     * {@link ModelBaker#getModel(Identifier)} while baking.
     * <p>
     * Only models reached during the discovery pass end up in the bakery's resolved map. A
     * specification that resolves another model at bake time without marking it here gets the
     * missing model back and the client logs {@code Requested a model that was not discovered
     * previously}, which is how a whole family of blocks ends up rendering as the black and magenta
     * cube. Discovery does <em>not</em> walk the resource pack: a model json existing on disk is not
     * enough to make it resolvable.
     *
     * @param resolver marks a model id as needed, via {@link ResolvableModel.Resolver#markDependency}.
     */
    default void resolveDependencies(ResolvableModel.Resolver resolver) {
    }
}
