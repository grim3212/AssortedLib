package com.grim3212.assorted.lib.client.model.vertices;

import com.google.common.base.Preconditions;
import com.grim3212.assorted.lib.client.texture.UnitTextureAtlasSprite;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.client.model.IQuadTransformer.*;

public class QuadBakingVertexConsumer implements VertexConsumer {

    private final Map<VertexFormatElement, Integer> ELEMENT_OFFSETS = Util.make(new IdentityHashMap<>(), map -> {
        int i = 0;
        for (var element : DefaultVertexFormat.BLOCK.getElements())
            map.put(element, DefaultVertexFormat.BLOCK.offsets.getInt(i++) / 4); // Int offset
    });
    private static final int QUAD_DATA_SIZE = STRIDE * 4;

    private final Consumer<BakedQuad> quadConsumer;

    int vertexIndex = 0;
    private int[] quadData = new int[QUAD_DATA_SIZE];

    private int tintIndex;
    private Direction direction = Direction.DOWN;
    private TextureAtlasSprite sprite = UnitTextureAtlasSprite.INSTANCE;
    private boolean hasAmbientOcclusion;

    public QuadBakingVertexConsumer(Consumer<BakedQuad> quadConsumer) {
        this.quadConsumer = quadConsumer;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        int offset = vertexIndex * STRIDE + POSITION;
        quadData[offset] = Float.floatToRawIntBits((float) x);
        quadData[offset + 1] = Float.floatToRawIntBits((float) y);
        quadData[offset + 2] = Float.floatToRawIntBits((float) z);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        int offset = vertexIndex * STRIDE + NORMAL;
        quadData[offset] = ((int) (x * 127.0f) & 0xFF) |
                (((int) (y * 127.0f) & 0xFF) << 8) |
                (((int) (z * 127.0f) & 0xFF) << 16);
        return this;
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a) {
        int offset = vertexIndex * STRIDE + COLOR;
        quadData[offset] = ((a & 0xFF) << 24) |
                ((b & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                (r & 0xFF);
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        int offset = vertexIndex * STRIDE + UV0;
        quadData[offset] = Float.floatToRawIntBits(u);
        quadData[offset + 1] = Float.floatToRawIntBits(v);
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        if (UV1 >= 0) // Vanilla doesn't support this, but it may be added by a 3rd party
        {
            int offset = vertexIndex * STRIDE + UV1;
            quadData[offset] = (u & 0xFFFF) | ((v & 0xFFFF) << 16);
        }
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        int offset = vertexIndex * STRIDE + UV2;
        quadData[offset] = (u & 0xFFFF) | ((v & 0xFFFF) << 16);
        return this;
    }

    public VertexConsumer misc(VertexFormatElement element, int... rawData) {
        Integer baseOffset = ELEMENT_OFFSETS.get(element);
        if (baseOffset != null) {
            int offset = vertexIndex * STRIDE + baseOffset;
            System.arraycopy(rawData, 0, quadData, offset, rawData.length);
        }
        return this;
    }

    @Override
    public void endVertex() {
        if (++vertexIndex != 4)
            return;
        // We have a full quad, pass it to the consumer and reset
        quadConsumer.accept(new BakedQuad(quadData, tintIndex, direction, sprite, hasAmbientOcclusion));
        vertexIndex = 0;
        quadData = new int[QUAD_DATA_SIZE];
    }

    @Override
    public void defaultColor(int var1, int var2, int var3, int var4) {

    }

    @Override
    public void unsetDefaultColor() {

    }


    public void setTintIndex(int tintIndex) {
        this.tintIndex = tintIndex;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setTexture(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    public void setHasAmbientOcclusion(boolean hasAmbientOcclusion) {
        this.hasAmbientOcclusion = hasAmbientOcclusion;
    }

    public static class Buffered extends QuadBakingVertexConsumer {
        private final BakedQuad[] output;

        public Buffered() {
            this(new BakedQuad[1]);
        }

        private Buffered(BakedQuad[] output) {
            super(q -> output[0] = q);
            this.output = output;
        }

        public BakedQuad getQuad() {
            var quad = Preconditions.checkNotNull(output[0], "No quad has been emitted. Vertices in buffer: " + vertexIndex);
            output[0] = null;
            return quad;
        }
    }
}
