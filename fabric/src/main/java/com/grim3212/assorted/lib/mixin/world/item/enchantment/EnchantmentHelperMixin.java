package com.grim3212.assorted.lib.mixin.world.item.enchantment;

import com.grim3212.assorted.lib.core.enchantment.LibEnchantment;
import com.grim3212.assorted.lib.core.item.IItemEnchantmentCondition;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Unique
    private static ThreadLocal<Enchantment> capturedEnchantment = new ThreadLocal<>();
    @Unique
    private static ThreadLocal<ItemStack> capturedItemStack = new ThreadLocal<>();

    @Redirect(method = "getAvailableEnchantmentResults", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private static <E> E assortedlib_captureEnchantment(Iterator<Enchantment> iterator) {
        Enchantment next = iterator.next();
        capturedEnchantment.set(next);
        return (E) next;
    }

    @Inject(method = "getAvailableEnchantmentResults", at = @At("HEAD"))
    private static void assortedlib_captureItemStack(int level, ItemStack stack, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        capturedItemStack.set(stack);
    }

    @Redirect(method = "getAvailableEnchantmentResults", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentCategory;canEnchant(Lnet/minecraft/world/item/Item;)Z"))
    private static boolean assortedlib_canEnchant(EnchantmentCategory instance, Item item) {
        if (capturedEnchantment.get() instanceof LibEnchantment libEnchantment) {
            Optional<Boolean> result = libEnchantment.assortedlib_canApplyAtEnchantingTable(capturedItemStack.get());
            if (result.isPresent()) {
                return result.get();
            }
        }
        if (item instanceof IItemEnchantmentCondition extension) {
            Optional<Boolean> result = extension.assortedlib_canApplyAtEnchantingTable(capturedItemStack.get(), capturedEnchantment.get());
            if (result.isPresent()) {
                return result.get();
            }
        }

        return instance.canEnchant(item);
    }

    @Inject(method = "getAvailableEnchantmentResults", at = @At("RETURN"))
    private static void assortedlib_clearCaptured(int level, ItemStack stack, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        capturedEnchantment.set(null);
        capturedItemStack.set(null);
    }
}
