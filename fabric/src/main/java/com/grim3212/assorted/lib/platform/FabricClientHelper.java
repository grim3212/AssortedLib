package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.client.events.ClientTickHandler;
import com.grim3212.assorted.lib.client.model.loader.FabricPlatformModelLoaderPlatformDelegate;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import com.grim3212.assorted.lib.client.render.IBEWLR;
import com.grim3212.assorted.lib.client.screen.LibScreenFactory;
import com.grim3212.assorted.lib.platform.services.IClientHelper;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricModelManager;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteSet;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FabricClientHelper implements IClientHelper {

    // TODO(26.2): extra models are no longer addressed by their Identifier. Fabric's model loading API
    //  hands out an opaque ExtraModelKey when a model is added, and the baked model is only reachable
    //  as FabricModelManager#getModel(key), so the id -> key mapping has to be kept here for callers
    //  that only know the model's location. Use #getAdditionalModel to read one back.
    private static final Map<Identifier, ExtraModelKey<BlockStateModel>> ADDITIONAL_MODELS = new ConcurrentHashMap<>();

    @Override
    public <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void registerScreen(Supplier<MenuType<? extends T>> menuType, LibScreenFactory<T, S> factory) {
        MenuScreens.register(menuType.get(), factory::create);
    }

    @Override
    public void registerAdditionalModel(List<Identifier> modelLocations) {
        final List<Identifier> models = List.copyOf(modelLocations);
        ModelLoadingPlugin.register(context -> {
            for (Identifier model : models) {
                context.addModel(additionalModelKey(model), SimpleUnbakedExtraModel.blockStateModel(model));
            }
        });
    }

    /**
     * The key an additionally loaded model is baked under.
     */
    public static ExtraModelKey<BlockStateModel> additionalModelKey(final Identifier model) {
        return ADDITIONAL_MODELS.computeIfAbsent(model, id -> ExtraModelKey.create(id::toString));
    }

    /**
     * Reads back a model registered through {@link #registerAdditionalModel(List)}.
     *
     * @param model The location the model was registered under.
     * @return The baked model.
     */
    public static BlockStateModel getAdditionalModel(final Identifier model) {
        return ((FabricModelManager) Minecraft.getInstance().getModelManager()).getModel(additionalModelKey(model));
    }

    @Override
    public void addReloadListener(Identifier identifier, PreparableReloadListener reloadListener) {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(identifier, reloadListener);
    }

    @Override
    public void registerBEWLR(final Consumer<IBEWLR> register) {
        // BlockEntityWithoutLevelRenderer is gone; a special item renderer is now an id -> unbaked
        // renderer codec that item models refer to by id. Fabric widens the id mapper for us.
        register.accept(SpecialModelRenderers.ID_MAPPER::put);
    }

    @Override
    public <E extends BlockEntity, S extends BlockEntityRenderState> void registerBlockEntityRenderer(Supplier<? extends BlockEntityType<? extends E>> entityType, BlockEntityRendererProvider<E, S> entityRendererFactory) {
        BlockEntityRenderers.register(entityType.get(), entityRendererFactory);
    }

    @Override
    public <E extends Entity> void registerEntityRenderer(Supplier<? extends EntityType<? extends E>> entityType, EntityRendererProvider<E> entityRendererFactory) {
        EntityRenderers.register(entityType.get(), entityRendererFactory);
    }

    @Override
    public void registerEntityLayer(ModelLayerLocation modelLayerLocation, Supplier<LayerDefinition> layerDefinition) {
        ModelLayerRegistry.registerModelLayer(modelLayerLocation, layerDefinition::get);
    }

    @Override
    public void registerBlockColor(BlockTintSource color, Supplier<List<Block>> blocks) {
        BlockColorRegistry.register(List.of(color), blocks.get().toArray(new Block[0]));
    }

    @Override
    public void registerItemTintSource(Identifier id, MapCodec<? extends ItemTintSource> source) {
        ItemTintSources.ID_MAPPER.put(id, source);
    }

    @Override
    public BlockColors getBlockColors() {
        return Minecraft.getInstance().getBlockColors();
    }

    @Override
    public void registerModelLoader(Identifier name, IModelSpecificationLoader<?> modelLoader) {
        UnbakedModelDeserializer.register(name, new FabricPlatformModelLoaderPlatformDelegate<>(name, modelLoader));
    }

    // TODO(26.2): registerRenderType has no runtime equivalent on Fabric any more, so this is a no-op.
    //  BlockRenderLayerMap is gone along with ItemBlockRenderTypes: a block's chunk layer is decided
    //  per quad while baking, from the sprite's Transparency (or Material#forceTranslucent), and ends
    //  up on BakedQuad.MaterialInfo#layer. A block therefore declares its render type from its model
    //  json ("render_type") or by the transparency of its texture, and there is nothing left to
    //  register from code.
    @Override
    public void registerRenderType(Supplier<Block> block, RenderType renderType) {
    }

    @Override
    public void registerKeyMapping(KeyMapping keyMapping) {
        KeyMappingHelper.registerKeyMapping(keyMapping);
    }

    @Override
    public void registerClientTickStart(ClientTickHandler handler) {
        ClientTickEvents.START_CLIENT_TICK.register(handler::handle);
    }

    @Override
    public void registerClientTickEnd(ClientTickHandler handler) {
        ClientTickEvents.END_CLIENT_TICK.register(handler::handle);
    }

    @Override
    public <T extends ParticleOptions> void registerParticle(Supplier<ParticleType<T>> type, Function<SpriteSet, ParticleProvider<T>> factory) {
        ParticleProviderRegistry.getInstance().register(type.get(), (FabricSpriteSet sprites) -> factory.apply(sprites));
    }

    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
