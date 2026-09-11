package com.grim3212.assorted.lib.client.model.item;

import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.baked.IDelegatingBakedModel;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.platform.ClientServices;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * The item form of a block whose look comes from model data - a colorizer's stored block, a bridge's
 * projected one - drawn from what the stack carries rather than from the block's empty state.
 * <p>
 * {@code ItemOverrides} is gone, and the stock {@code minecraft:model} bakes one quad collection at
 * load time, which for these blocks is their empty state. An {@link ItemModel}'s {@code update} is
 * handed the stack, so this builds the model data from it and asks the block's own
 * {@link IDataAwareBakedModel} for the parts and the particle, reaching the same lazily baked cache
 * the placed block uses. It cannot bake on demand from {@code update}, which is why it goes through
 * that cache at all.
 * <p>
 * A mod registers its own {@code ItemModel.Unbaked} codec type and bakes one of these from it,
 * supplying how to read model data off a stack.
 */
public final class DataAwareItemModel implements ItemModel {

    /** An item is drawn off no position, so its random source only has to be stable. */
    private static final long ITEM_SEED = 42L;

    private final BlockStateModel model;
    private final ModelRenderProperties properties;
    private final List<ItemTintSource> tints;
    private final Function<ItemStack, IBlockModelData> modelData;
    private final Function<ItemStack, ?> identity;

    private DataAwareItemModel(BlockStateModel model, ModelRenderProperties properties, List<ItemTintSource> tints,
                               Function<ItemStack, IBlockModelData> modelData, Function<ItemStack, ?> identity) {
        this.model = model;
        this.properties = properties;
        this.tints = tints;
        this.modelData = modelData;
        this.identity = identity;
    }

    /**
     * @param model     The <em>block</em> model to draw - the json carrying the block's model loader,
     *                  the same model its blockstate points at. The Unbaked calling this has to mark
     *                  it as a dependency, or it is not discovered and bakes to the missing model.
     * @param tints     Item tint sources, as on a vanilla {@code minecraft:model}.
     * @param modelData The model data a stack stands for, as its block entity would supply it.
     * @param identity  What distinguishes one stack's look from another's; the render state is cached
     *                  under it, so two stacks that draw differently must answer differently.
     */
    public static DataAwareItemModel bake(ItemModel.BakingContext context, Identifier model, List<ItemTintSource> tints,
                                          Function<ItemStack, IBlockModelData> modelData, Function<ItemStack, ?> identity) {
        ModelBaker baker = context.blockModelBaker();

        // The same entry point the blockstate side uses, so the item and the placed block share one
        // baked model and one cache of variants.
        BlockStateModel baked = ClientServices.CLIENT.bakeSpecificationModel(baker, model, BlockModelRotation.IDENTITY);

        // Transforms, gui light and the fallback particle come off the resolved model, which walks up
        // into the shape template the json names as its parent - where the display block that sits the
        // item correctly in an inventory actually lives.
        ResolvedModel resolved = baker.getModel(model);
        ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolved, resolved.getTopTextureSlots());

        return new DataAwareItemModel(unwrap(baked), properties, tints, modelData, identity);
    }

    /**
     * {@code bakeSpecificationModel} wraps the model in the loader's block delegate, which reads model
     * data off a level and a position. An item has neither, so it talks to the model underneath - and
     * Fabric's delegate is not itself data aware, so without this a Fabric item drew its empty state.
     */
    private static BlockStateModel unwrap(BlockStateModel model) {
        BlockStateModel inner = model;
        while (inner instanceof IDelegatingBakedModel delegating) {
            inner = delegating.getDelegate();
        }
        return inner;
    }

    @Override
    public void update(ItemStackRenderState output, ItemStack item, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        IBlockModelData data = this.modelData.apply(item);

        output.appendModelIdentityElement(this);
        output.appendModelIdentityElement(this.identity.apply(item));

        ItemStackRenderState.LayerRenderState layer = output.newLayer();

        if (item.hasFoil()) {
            layer.setFoilType(ItemStackRenderState.FoilType.STANDARD);
            output.setAnimated();
            output.appendModelIdentityElement(ItemStackRenderState.FoilType.STANDARD);
        }

        if (!this.tints.isEmpty()) {
            var tintLayers = layer.tintLayers();
            for (ItemTintSource tint : this.tints) {
                int color = tint.calculate(item, level, owner == null ? null : owner.asLivingEntity());
                tintLayers.add(color);
                output.appendModelIdentityElement(color);
            }
        }

        List<BakedQuad> quads = layer.prepareQuadList();
        int materialFlags = collectQuads(data, quads);

        // A render state is reused between frames unless it says it is animated, so an item drawing an
        // animated block would freeze on whichever frame it was first drawn with. Which data is
        // animated is only known once the quads are collected, so this is asked per stack.
        if ((materialFlags & BakedQuad.FLAG_ANIMATED) != 0) {
            output.setAnimated();
        }

        layer.setExtents(() -> CuboidItemModelWrapper.computeExtents(quads));
        this.properties.applyToLayer(layer, displayContext);

        // applyToLayer sets the particle from the json - the block's empty texture. The particles a
        // dropped or broken item throws off have to come from what it is actually drawing.
        if (this.model instanceof IDataAwareBakedModel dataAware) {
            layer.setParticleMaterial(dataAware.particleMaterial(data));
        }
    }

    /**
     * @return the union of the collected parts' {@linkplain BakedQuad.MaterialFlags material flags}.
     */
    private int collectQuads(IBlockModelData data, List<BakedQuad> output) {
        List<BlockStateModelPart> parts = new ArrayList<>();
        RandomSource random = RandomSource.create(ITEM_SEED);

        if (this.model instanceof IDataAwareBakedModel dataAware) {
            dataAware.collectParts(random, data, parts);
        } else {
            collectPlainParts(this.model, random, parts);
        }

        int materialFlags = 0;
        for (BlockStateModelPart part : parts) {
            materialFlags |= part.materialFlags();
            output.addAll(part.getQuads(null));
            for (Direction direction : Direction.values()) {
                output.addAll(part.getQuads(direction));
            }
        }

        return materialFlags;
    }

    // Deprecated by NeoForge in favour of a level/pos aware overload that only exists in its patched
    // jar. An item has neither, so this is the only overload that applies.
    @SuppressWarnings("deprecation")
    private static void collectPlainParts(BlockStateModel model, RandomSource random, List<BlockStateModelPart> parts) {
        model.collectParts(random, parts);
    }
}
