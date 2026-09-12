package com.grim3212.assorted.lib.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunk;
import org.jetbrains.annotations.Nullable;

/**
 * A block whose light emission depends on where it is - one that emits what the block in its block
 * entity would. Both loaders hand this to the light engine (NeoForge through its own
 * {@code getLightEmission(state, level, pos)}, Fabric through AssortedLib's {@code BlockLightEngineMixin}),
 * which asks it from its own thread with the level in hand, so read the block entity with
 * {@link #blockEntityAt}.
 */
public interface IBlockLightEmission {
    int getLightEmission(BlockState state, BlockGetter blockGetter, BlockPos pos);

    /**
     * The block entity at {@code pos}, from any thread. {@code Level#getBlockEntity} answers null
     * off the server thread - silently, so a block read straight through it looks empty to the
     * light engine and only the client's light ever shows its stored block glowing. A server level
     * is therefore read through its chunk, the way the light engine itself reads blocks
     * ({@code ServerChunkCache#getChunkForLighting}): a plain map read that works from any thread
     * and never loads a chunk. Everything else - a client level, a chunk, a worldgen region - answers
     * as it is.
     */
    @Nullable
    static BlockEntity blockEntityAt(BlockGetter getter, BlockPos pos) {
        if (getter instanceof ServerLevel level) {
            final LightChunk chunk = level.getChunkSource().getChunkForLighting(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
            return chunk == null ? null : chunk.getBlockEntity(pos);
        }
        return getter.getBlockEntity(pos);
    }
}
