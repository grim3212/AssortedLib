package com.grim3212.assorted.lib.client.model;

import net.minecraft.client.resources.model.geometry.BakedQuad;

import java.util.List;

/**
 * A transformation applied to {@linkplain BakedQuad baked quads}. A quad is an immutable record, so
 * a transformer returns a new quad rather than editing one in place.
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

    // TODO(26.2): processInPlace and the vertex format offsets (STRIDE, POSITION, COLOR, ...) are
    //  gone: a BakedQuad has no mutable vertex array, and colour, light and normals are supplied at
    //  submit time, so there is nothing to mutate.
}
