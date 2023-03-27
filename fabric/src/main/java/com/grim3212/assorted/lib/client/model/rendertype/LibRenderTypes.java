package com.grim3212.assorted.lib.client.model.rendertype;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.client.shaders.LibShaders;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class LibRenderTypes extends RenderType {

    private static final ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_UNLIT_SHADER = new ShaderStateShard(LibShaders::getEntityTranslucentUnlitShader);

    public LibRenderTypes(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
        throw new UnsupportedOperationException("Do not initialize");
    }

    public static Function<ResourceLocation, RenderType> UNSORTED_TRANSLUCENT = Util.memoize(LibRenderTypes::unsortedTranslucent);

    private static RenderType unsortedTranslucent(ResourceLocation textureLocation) {
        final boolean sortingEnabled = false;
        CompositeState renderState = CompositeState.builder()
                .setShaderState(RenderType.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                .setTextureState(new TextureStateShard(textureLocation, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return create(LibConstants.MOD_ID + "_entity_unsorted_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, sortingEnabled, renderState);
    }

    public static Function<ResourceLocation, RenderType> UNLIT_TRANSLUCENT_SORTED = Util.memoize(tex -> LibRenderTypes.unlitTranslucent(tex, true));
    public static Function<ResourceLocation, RenderType> UNLIT_TRANSLUCENT_UNSORTED = Util.memoize(tex -> LibRenderTypes.unlitTranslucent(tex, false));

    private static RenderType unlitTranslucent(ResourceLocation textureLocation, boolean sortingEnabled) {
        CompositeState renderState = CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_UNLIT_SHADER)
                .setTextureState(new TextureStateShard(textureLocation, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return create(LibConstants.MOD_ID + "_entity_unlit_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, sortingEnabled, renderState);
    }

    public static RenderType getUnlitTranslucent(ResourceLocation textureLocation, boolean sortingEnabled) {
        return (sortingEnabled ? UNLIT_TRANSLUCENT_SORTED : UNLIT_TRANSLUCENT_UNSORTED).apply(textureLocation);
    }

    public static RenderType getUnsortedTranslucent(ResourceLocation textureLocation) {
        return UNSORTED_TRANSLUCENT.apply(textureLocation);
    }
}
