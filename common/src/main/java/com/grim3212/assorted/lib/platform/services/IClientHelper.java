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
import net.minecraft.client.renderer.entity.EntityRendererProvider;
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

    void registerAdditionalModel(List<Identifier> modelLocations);

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

    // TODO(26.2): registerItemProperty has no replacement. ClampedItemPropertyFunction and the
    //  ItemProperties registry are gone; model selection by a numeric property is data-driven through
    //  client.renderer.item.properties.numeric.* referenced from the item model JSON, so there is
    //  nothing left to register from code. Removed rather than stubbed so callers fail loudly.

    void registerRenderType(Supplier<Block> block, RenderType renderType);

    void registerKeyMapping(KeyMapping keyMapping);

    void registerClientTickStart(ClientTickHandler handler);

    void registerClientTickEnd(ClientTickHandler handler);

    <T extends ParticleOptions> void registerParticle(Supplier<ParticleType<T>> type, Function<SpriteSet, ParticleProvider<T>> particleFactory);

    Player getClientPlayer();
}
