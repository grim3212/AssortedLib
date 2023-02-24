package com.grim3212.assorted.lib.mixin.crafting;

import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.platform.ForgeConditionHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingHelper.class)
public abstract class CraftingHelperMixin {

    @Inject(
            method = "serialize(Lnet/minecraftforge/common/crafting/conditions/ICondition;)Lcom/google/gson/JsonObject;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void serialize(ICondition input, CallbackInfoReturnable<JsonObject> ci) {
        if (input instanceof ForgeConditionHelper.ConditionWrapper wrapper) {
            JsonObject conditionJson = new JsonObject();
            wrapper.provider.write(conditionJson);
            conditionJson.addProperty("type", wrapper.getID().toString());
            ci.setReturnValue(conditionJson);
        }
    }
}
