/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.grim3212.assorted.lib.client.model.state;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.core.BlockMath;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.EnumMap;
import java.util.Map;

/**
 * Simple implementation of {@link ModelState}.
 * <p>
 * In 26.2 uv locking is no longer a boolean the baker reads back off the state; instead a uv locked
 * state reports a per face transformation which {@link net.minecraft.client.resources.model.cuboid.FaceBakery}
 * applies to the uvs. This class keeps the old {@code uvLocked} constructor flag and derives those
 * face transformations the same way {@link net.minecraft.client.renderer.block.dispatch.BlockModelRotation}
 * does.
 */
public final class SimpleModelState implements ModelState {
    private final Transformation transformation;
    private final Map<Direction, Matrix4fc> faceMapping;
    private final Map<Direction, Matrix4fc> inverseFaceMapping;

    public SimpleModelState(Transformation transformation, boolean uvLocked) {
        this.transformation = transformation;

        if (uvLocked) {
            this.faceMapping = new EnumMap<>(Direction.class);
            this.inverseFaceMapping = new EnumMap<>(Direction.class);

            for (Direction face : Direction.values()) {
                Matrix4fc faceTransform = BlockMath.getFaceTransformation(transformation, face).getMatrix();
                this.faceMapping.put(face, faceTransform);
                this.inverseFaceMapping.put(face, faceTransform.invertAffine(new Matrix4f()));
            }
        } else {
            this.faceMapping = Map.of();
            this.inverseFaceMapping = Map.of();
        }
    }

    public SimpleModelState(Transformation transformation) {
        this(transformation, false);
    }

    @Override
    public Transformation transformation() {
        return transformation;
    }

    @Override
    public Matrix4fc faceTransformation(Direction face) {
        return faceMapping.getOrDefault(face, NO_TRANSFORM);
    }

    @Override
    public Matrix4fc inverseFaceTransformation(Direction face) {
        return inverseFaceMapping.getOrDefault(face, NO_TRANSFORM);
    }
}
