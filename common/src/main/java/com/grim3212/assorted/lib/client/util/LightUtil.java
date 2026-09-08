package com.grim3212.assorted.lib.client.util;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.core.Direction;

public final class LightUtil {

    private LightUtil() {
        throw new IllegalStateException("Tried to construct a LightUtil instance, but this is a utility class!");
    }

    // TODO(26.2): LightUtil#put(IVertexConsumer, BakedQuad) has no replacement and was dropped.
    //  In 26.2 a BakedQuad is a record of four org.joml.Vector3fc positions, four packed UV longs
    //  (unpack them with net.minecraft.client.model.geom.builders.UVPair#unpackU / #unpackV), a
    //  Direction and a BakedQuad.MaterialInfo (sprite / tintIndex / shade / lightEmission). It no
    //  longer carries an int[] of DefaultVertexFormat.BLOCK vertex data, and it never carries
    //  per-vertex colour or lightmap coordinates - those are supplied at submit time instead, through
    //  the tintLayers and lightCoords arguments of SubmitNodeCollector#submitBlockModel / #submitItem.
    //  There is therefore nothing left to pipe into an element-indexed IVertexConsumer. Anything that
    //  used to re-emit a quad should submit it through SubmitNodeCollector, or write it straight out
    //  with VertexConsumer#putBakedQuad(PoseStack.Pose, BakedQuad, QuadInstance).

    public static void pack(float[] from, int[] to, VertexFormat formatTo, int v, int e) {
        VertexFormatElement element = formatTo.getElements().get(e);
        GpuFormat format = element.format();
        GpuFormat.ComponentType type = format.componentType();
        int vertexStart = v * formatTo.getVertexSize() + element.offset();
        int count = format.componentCount();
        int size = type.byteSize();
        int mask = (256 << (8 * (size - 1))) - 1;
        for (int i = 0; i < 4; i++) {
            if (i < count) {
                int pos = vertexStart + size * i;
                int index = pos >> 2;
                int offset = pos & 3;
                float f = i < from.length ? from[i] : 0;
                int bits = packComponent(type, f, mask);
                to[index] &= ~(mask << (offset * 8));
                to[index] |= (((bits & mask) << (offset * 8)));
            }
        }
    }

    public static void unpack(int[] from, float[] to, VertexFormat formatFrom, int v, int e) {
        int length = Math.min(4, to.length);
        VertexFormatElement element = formatFrom.getElements().get(e);
        GpuFormat format = element.format();
        GpuFormat.ComponentType type = format.componentType();
        int vertexStart = v * formatFrom.getVertexSize() + element.offset();
        int count = format.componentCount();
        int size = type.byteSize();
        int mask = (256 << (8 * (size - 1))) - 1;
        for (int i = 0; i < length; i++) {
            if (i < count) {
                int pos = vertexStart + size * i;
                int index = pos >> 2;
                int offset = pos & 3;
                int bits = from[index];
                bits = bits >>> (offset * 8);
                if ((pos + size - 1) / 4 != index) {
                    bits |= from[index + 1] << ((4 - offset) * 8);
                }
                bits &= mask;
                to[i] = unpackComponent(type, bits, mask);
            } else {
                to[i] = (i == 3 && element.name().equals(DefaultVertexFormat.POSITION_SEMANTIC_NAME)) ? 1 : 0;
            }
        }
    }

    /**
     * 26.2 replaced {@code VertexFormatElement.Type} with {@link GpuFormat.ComponentType}, which folds
     * the old element count and the signed/unsigned distinction into the component type itself.
     */
    private static int packComponent(GpuFormat.ComponentType type, float value, int mask) {
        return switch (type) {
            case FLOAT_32 -> Float.floatToRawIntBits(value);
            case UNORM_8, UNORM_16, UINT_8, UINT_16, UINT_32 -> Math.round(value * mask);
            case SNORM_8, SNORM_16, SINT_8, SINT_16, SINT_32 -> Math.round(value * (mask >> 1));
            default -> throw new UnsupportedOperationException("Cannot pack a vertex component of type " + type);
        };
    }

    private static float unpackComponent(GpuFormat.ComponentType type, int bits, int mask) {
        return switch (type) {
            case FLOAT_32 -> Float.intBitsToFloat(bits);
            case UNORM_8, UNORM_16, UINT_8, UINT_16 -> (float) bits / mask;
            case UINT_32 -> (float) ((double) (bits & 0xFFFFFFFFL) / 0xFFFFFFFFL);
            case SNORM_8, SINT_8 -> ((float) (byte) bits) / (mask >> 1);
            case SNORM_16, SINT_16 -> ((float) (short) bits) / (mask >> 1);
            case SINT_32 -> (float) ((double) (bits & 0xFFFFFFFFL) / (0xFFFFFFFFL >> 1));
            default -> throw new UnsupportedOperationException("Cannot unpack a vertex component of type " + type);
        };
    }

    public static float diffuseLight(Direction side) {
        return switch (side) {
            case DOWN -> .5f;
            case UP -> 1f;
            case NORTH, SOUTH -> .8f;
            default -> .6f;
        };
    }
}
