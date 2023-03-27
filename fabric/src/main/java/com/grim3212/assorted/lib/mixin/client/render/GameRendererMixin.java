package com.grim3212.assorted.lib.mixin.client.render;

import com.grim3212.assorted.lib.client.shaders.LibShaders;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    @Final
    private Map<String, ShaderInstance> shaders;

    @Inject(method = "reloadShaders", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;shutdownShaders()V", shift = At.Shift.AFTER))
    private void reloadShaders(ResourceProvider pResourceProvider, CallbackInfo ci) {
        this.setupShader(LibShaders::registerShaders, pResourceProvider);
    }

    private void setupShader(Function<ResourceProvider, List<Pair<ShaderInstance, Consumer<ShaderInstance>>>> function, ResourceProvider pResourceProvider) {
        var shaders = function.apply(pResourceProvider);
        for (Pair<ShaderInstance, Consumer<ShaderInstance>> shader : shaders) {
            this.shaders.put(shader.getFirst().getName(), shader.getFirst());
            shader.getSecond().accept(shader.getFirst());
        }
    }

}
