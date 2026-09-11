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
 * Simple {@link ModelState}. A uv locked state reports per face uv transformations for
 * {@link net.minecraft.client.resources.model.cuboid.FaceBakery} rather than a flag; they are
 * derived the way {@link net.minecraft.client.renderer.block.dispatch.BlockModelRotation} does.
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
