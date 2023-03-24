package com.grim3212.assorted.lib.mixin.client.model;

import com.grim3212.assorted.lib.client.model.IModelBakeryAccessor;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin implements IModelBakeryAccessor {

    @Unique
    private ModelBakery currentBakery = null;

    @Inject(
            method = "loadModels(Lnet/minecraft/util/profiling/ProfilerFiller;Ljava/util/Map;Lnet/minecraft/client/resources/model/ModelBakery;)Lnet/minecraft/client/resources/model/ModelManager$ReloadState;",
            at = @At(
                    value = "HEAD"
            )
    )
    public void onApply(ProfilerFiller profilerFiller, Map<ResourceLocation, AtlasSet.StitchResult> map, ModelBakery modelBakery, CallbackInfoReturnable<ModelManager.ReloadState> cir) {
        this.currentBakery = modelBakery;
    }

    @Override
    public ModelBakery getModelBakery() {
        if (currentBakery == null) {
            throw new IllegalStateException("ModelBakery is not available yet");
        }

        return currentBakery;
    }
}
