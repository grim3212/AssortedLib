package com.grim3212.assorted.lib.client.shaders;

import com.grim3212.assorted.lib.LibConstants;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class LibShaders {

    @Nullable
    private static ShaderInstance rendertypeEntityTranslucentUnlitShader;

    public static ShaderInstance getEntityTranslucentUnlitShader() {
        return Objects.requireNonNull(rendertypeEntityTranslucentUnlitShader, "Attempted to call getEntityTranslucentUnlitShader before shaders have finished loading.");
    }

    public static List<Pair<ShaderInstance, Consumer<ShaderInstance>>> registerShaders(ResourceProvider resourceProvider) {
        try {
            return List.of(
                    Pair.of(new ShaderInstance(resourceProvider, LibConstants.MOD_ID + "_rendertype_entity_unlit_translucent", DefaultVertexFormat.NEW_ENTITY), (shaderInstance) -> {
                        rendertypeEntityTranslucentUnlitShader = shaderInstance;
                    })
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
