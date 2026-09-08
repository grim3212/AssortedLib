package com.grim3212.assorted.lib.client.model;

import net.minecraft.client.resources.model.geometry.BakedQuad;

import java.util.List;

/**
 * A transformation that can be applied to {@linkplain BakedQuad baked quads}.
 * <p>
 * {@link BakedQuad} is an immutable record in 26.2 - four {@code Vector3fc} positions, four packed uv
 * longs, a {@code Direction} and a {@link BakedQuad.MaterialInfo} - so a transformer produces a new
 * quad instead of editing one in place.
 */
@FunctionalInterface
public interface IQuadTransformer {

    BakedQuad process(BakedQuad quad);

    default List<BakedQuad> process(List<BakedQuad> inputs) {
        return inputs.stream().map(this::process).toList();
    }

    default IQuadTransformer andThen(IQuadTransformer other) {
        return quad -> other.process(process(quad));
    }

    // TODO(26.2): the in place half of this interface is gone, along with the vertex format constants
    //  it was built on. processInPlace(BakedQuad) worked because a 1.20.1 BakedQuad owned a mutable
    //  int[] of DefaultVertexFormat.BLOCK vertex data, and STRIDE / POSITION / COLOR / UV0 / UV1 /
    //  UV2 / NORMAL were int offsets into it (DefaultVertexFormat.BLOCK#getIntegerSize and
    //  VertexFormat#offsets). 26.2 has no packed vertex array on a quad at all: positions and uvs are
    //  typed fields on a record, normals are recomputed from direction() at submit time, and colour
    //  and light are submit time arguments (tintLayers / lightCoords on
    //  SubmitNodeCollector#submitBlockModel), not geometry. There is nothing to mutate, so the
    //  offsets and processInPlace were dropped rather than faked.
}
