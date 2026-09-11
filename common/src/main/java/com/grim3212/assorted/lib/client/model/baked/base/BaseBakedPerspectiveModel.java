package com.grim3212.assorted.lib.client.model.baked.base;

import com.grim3212.assorted.lib.client.model.baked.ITransformAwareBakedModel;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/**
 * Supplies the classic hard coded "held item" transforms, as plain {@link ItemTransform} records,
 * for models that ship no {@code display} block. Translations are in block units (vanilla scales
 * the json values by 1/16); rotations are euler angles in degrees.
 */
public abstract class BaseBakedPerspectiveModel implements ITransformAwareBakedModel {
    private static final ItemTransform GROUND = transform(0.0F, 3.0F / 16.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.25F);
    private static final ItemTransform GUI = transform(0.0F, 0.0F, 0.0F, 30.0F, 225.0F, 0.0F, 0.625F);
    private static final ItemTransform FIXED = transform(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F);
    private static final ItemTransform THIRD_PERSON = transform(0.0F, 2.5F / 16.0F, 0.0F, 75.0F, 45.0F, 0.0F, 0.375F);
    private static final ItemTransform FIRST_PERSON = transform(0.0F, 0.0F, 0.0F, 0.0F, 45.0F, 0.0F, 0.40F);

    // NeoForge deprecates the canonical nine argument constructor in favour of one that also takes its
    // own modded transform map; that overload does not exist in vanilla, which this module builds against.
    @SuppressWarnings("deprecation")
    private static final ItemTransforms TRANSFORMS = new ItemTransforms(THIRD_PERSON, THIRD_PERSON, FIRST_PERSON, FIRST_PERSON, FIXED, GUI, GROUND, FIXED, FIXED);

    private static ItemTransform transform(
            final float transX,
            final float transY,
            final float transZ,
            final float rotX,
            final float rotY,
            final float rotZ,
            final float scaleXYZ) {
        return new ItemTransform(new Vector3f(rotX, rotY, rotZ), new Vector3f(transX, transY, transZ), new Vector3f(scaleXYZ, scaleXYZ, scaleXYZ));
    }

    public @NotNull ItemTransforms getTransforms() {
        return TRANSFORMS;
    }

    @Override
    public ItemTransform getTransform(final ItemDisplayContext cameraTransformType) {
        return getTransforms().getTransform(cameraTransformType);
    }
}
