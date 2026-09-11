package com.grim3212.assorted.lib.platform.services;

import com.grim3212.assorted.lib.client.events.ClientTickHandler;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import com.grim3212.assorted.lib.client.render.IBEWLR;
import com.grim3212.assorted.lib.client.screen.LibScreenFactory;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IClientHelper {

    <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void registerScreen(Supplier<MenuType<? extends T>> menuType, LibScreenFactory<T, S> factory);

    void addReloadListener(Identifier identifier, PreparableReloadListener reloadListener);

    void registerBEWLR(final Consumer<IBEWLR> register);

    // BlockEntityRendererProvider gained a render-state type parameter in 26.x; renderers now
    // extract a state object and submit from it rather than rendering inline.
    <E extends BlockEntity, S extends BlockEntityRenderState> void registerBlockEntityRenderer(Supplier<? extends BlockEntityType<? extends E>> entityType, BlockEntityRendererProvider<E, S> entityRendererFactory);

    <E extends Entity> void registerEntityRenderer(Supplier<? extends EntityType<? extends E>> entityType, EntityRendererProvider<E> entityRendererFactory);

    void registerEntityLayer(ModelLayerLocation modelLayerLocation, Supplier<LayerDefinition> layerDefinition);

    void registerBlockColor(BlockTintSource color, Supplier<List<Block>> blocks);

    // TODO(26.2): item tinting is no longer a runtime, per-item registration. ItemColor and
    //  ItemColors are gone; an item's tints live in its item model JSON as ItemTintSource entries and
    //  code only registers the MapCodec that deserialises a custom source type, keyed by id. Callers
    //  that attached an ItemColor to a set of items must emit a "tints" entry referencing this id from
    //  those items' model JSON instead.
    void registerItemTintSource(Identifier id, MapCodec<? extends ItemTintSource> source);

    BlockColors getBlockColors();

    void registerModelLoader(Identifier name, IModelSpecificationLoader<?> modelLoader);

    /**
     * Registers the {@link MapCodec} that reads a custom {@code ItemModel.Unbaked} type, so an item
     * model json can name it under {@code "model": {"type": "<id>"}}.
     * <p>
     * The 1.20.1 way of varying an item's model per stack - an {@code ItemOverrides} on the baked
     * model - is gone; an item model is a codec-registered {@link ItemModel} whose {@code update} is
     * handed the stack, and this is the only registration point for one.
     */
    void registerItemModelType(Identifier id, MapCodec<? extends ItemModel.Unbaked> codec);

    /**
     * Registers the {@link MapCodec} that reads a custom {@link ConditionalItemModelProperty}, so a
     * {@code minecraft:condition} item model can branch on it.
     * <p>
     * This is the replacement for {@code ItemProperties.register} plus a model {@code overrides} list:
     * the branch is chosen before baking, from the item json, and only the predicate is code. Vanilla's
     * own conditionals cannot read an arbitrary value out of {@code CUSTOM_DATA} - the nearest,
     * {@code minecraft:component_matches}, needs an exact {@code NbtPredicate} - so a mod-owned tag
     * needs its own property here.
     */
    void registerConditionalItemModelProperty(Identifier id, MapCodec<? extends ConditionalItemModelProperty> codec);

    /**
     * Bakes the model at {@code modelLocation} into a whole {@link BlockStateModel}, keeping it
     * dynamic when the model json behind it was produced by a
     * {@link com.grim3212.assorted.lib.client.model.loaders.IModelSpecification}.
     * <p>
     * The model json pipeline can only carry geometry, so a specification reached that way is baked
     * once with empty model data and flattened. Reached through here the specification's own
     * {@code BlockStateModel} survives, wrapped in the loader's level-aware bridge, so a model whose
     * geometry depends on a block entity - a colorizer, say - draws what the block entity actually
     * holds. Models that are not specification backed fall through to an ordinary baked variant, so
     * this is safe to point at any model.
     *
     * @param baker         The bakery to bake with. Note that a data aware model keeps this past the
     *                      bake, because the states it has to bake for are only known while rendering.
     * @param modelLocation The model to bake. It must have been marked as a dependency during
     *                      discovery, or the bakery will not have it.
     * @param modelState    The rotation and uv lock to bake with.
     */
    BlockStateModel bakeSpecificationModel(ModelBaker baker, Identifier modelLocation, ModelState modelState);

    void registerRenderType(Supplier<Block> block, RenderType renderType);

    void registerKeyMapping(KeyMapping keyMapping);

    void registerClientTickStart(ClientTickHandler handler);

    void registerClientTickEnd(ClientTickHandler handler);

    <T extends ParticleOptions> void registerParticle(Supplier<ParticleType<T>> type, Function<SpriteSet, ParticleProvider<T>> particleFactory);

    Player getClientPlayer();
}
