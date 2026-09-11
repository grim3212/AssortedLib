package com.grim3212.assorted.lib.client.model.baked;

import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A {@link BlockStateModel} whose geometry also depends on the model data a block entity exposes.
 * Vanilla's {@link BlockStateModel#collectParts(RandomSource, List)} gets no level, position or
 * data, so the loader delegates ({@code FabricBakedModelDelegate}, {@code ForgeBakedModelDelegate})
 * route the data in here; vanilla call sites get {@linkplain IBlockModelData#empty() empty} data.
 * The model is baked per {@link BlockState}, so capture the state while baking.
 */
public interface IDataAwareBakedModel extends BlockStateModel {

    /**
     * Collects the parts of this model for {@code extraData} into {@code output}. {@code random} is
     * seeded from the block position.
     */
    void collectParts(@NotNull RandomSource random, @NotNull IBlockModelData extraData, @NotNull List<BlockStateModelPart> output);

    /**
     * The particle sprite for {@code extraData}. Break and hit particles read this rather than the
     * geometry, so a model textured from a block entity must answer it from the data too.
     */
    // Deprecated by NeoForge in favour of the level/pos aware overload the delegates implement.
    @SuppressWarnings("deprecation")
    default Material.Baked particleMaterial(@NotNull IBlockModelData extraData) {
        return particleMaterial();
    }

    // Deprecated by NeoForge in favour of a level/pos aware overload that only exists in its patched
    // jar; vanilla still declares this one abstract, so it has to be implemented here.
    @SuppressWarnings("deprecation")
    @Override
    default void collectParts(@NotNull RandomSource random, @NotNull List<BlockStateModelPart> output) {
        collectParts(random, IBlockModelData.empty(), output);
    }

    // getSupportedRenderTypes is gone: a quad's layer comes from its sprite's transparency
    // (MaterialInfo#layer()), so a model has no render type to answer.
}
