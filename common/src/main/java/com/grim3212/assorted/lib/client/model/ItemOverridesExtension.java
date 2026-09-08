package com.grim3212.assorted.lib.client.model;

import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;

/**
 * Carries the baking context a custom model loader used to need in order to build its item overrides.
 */
// TODO(26.2): this class used to extend net.minecraft.client.renderer.block.model.ItemOverrides, which
//  was deleted along with ItemOverride. Its whole purpose was to give a model loader a hook -
//  ItemOverrides#resolve(BakedModel, ItemStack, ClientLevel, LivingEntity, int) - where it could pick a
//  different BakedModel for a stack, with the IModelBakingContext kept around so the override could
//  bake more models on demand.
//  Why it cannot be expressed: there is no code-registered override list in 26.2. Item variation runs
//  entirely through net.minecraft.client.renderer.item.ItemModel: an item's json names one
//  ItemModel.Unbaked type (registered by MapCodec in ItemModels), and the branching implementations -
//  SelectItemModel, ConditionalItemModel, RangeSelectItemModel, CompositeModel - choose between child
//  models using codec-registered properties from client.renderer.item.properties.{select,conditional,
//  numeric}. The equivalent of an override is therefore a registered ItemModel.Unbaked plus a json
//  change, and it is chosen before baking rather than resolved per draw. The class is kept only so the
//  context plumbing does not have to be deleted from the loader modules while that migration happens.
public class ItemOverridesExtension {
    protected final IModelBakingContext context;

    protected ItemOverridesExtension(IModelBakingContext context) {
        this.context = context;
    }
}
