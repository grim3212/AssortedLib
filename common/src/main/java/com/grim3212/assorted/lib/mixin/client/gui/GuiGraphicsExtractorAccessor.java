package com.grim3212.assorted.lib.mixin.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * The GUI render state an extractor is filling. {@code GuiGraphicsExtractor#item} is hard wired to
 * {@link net.minecraft.world.item.ItemDisplayContext#GUI}, so submitting an item drawn any other way
 * means adding the render state ourselves - see
 * {@link com.grim3212.assorted.lib.client.screen.LibGuiItemRenderer}.
 */
@Mixin(GuiGraphicsExtractor.class)
public interface GuiGraphicsExtractorAccessor {

    @Accessor
    GuiRenderState getGuiRenderState();
}
