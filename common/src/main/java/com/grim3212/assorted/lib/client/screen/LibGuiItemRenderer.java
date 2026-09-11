package com.grim3212.assorted.lib.client.screen;

import com.grim3212.assorted.lib.mixin.client.gui.GuiGraphicsExtractorAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws an item into a screen the way {@link GuiGraphicsExtractor#item} does, but with a view of
 * your own. Vanilla's call always resolves the model for {@link ItemDisplayContext#GUI} and applies
 * the transform the model carries for it, so a screen that wants a block seen head on - a storage
 * crate's face, a machine's front - cannot use it.
 */
public final class LibGuiItemRenderer {

    private LibGuiItemRenderer() {
    }

    /** Head on, filling the 16x16 the GUI gives an item, with the model's given face toward the viewer. */
    public static ItemTransform facingViewer(float yRotation) {
        return new ItemTransform(new Vector3f(0.0F, yRotation, 0.0F), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
    }

    /**
     * @param displayContext the context the model resolves its layers for
     * @param transform      replaces the transform each resolved layer took from the model, or null to keep it
     */
    public static void item(GuiGraphicsExtractor graphics, ItemStack stack, ItemDisplayContext displayContext, @Nullable ItemTransform transform, int x, int y) {
        if (stack.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LayerTrackingRenderState state = new LayerTrackingRenderState();
        minecraft.getItemModelResolver().updateForTopItem(state, stack, displayContext, minecraft.level, null, 0);

        if (transform != null) {
            state.applyTransform(transform);
        }

        // Drawn items are cached in an atlas keyed on this identity. The same stack drawn two ways is
        // two pictures, so the view has to be part of the key or one drawing is served the other's.
        state.appendModelIdentityElement(displayContext);
        state.appendModelIdentityElement(transform == null ? ItemTransform.NO_TRANSFORM : transform);

        ((GuiGraphicsExtractorAccessor) graphics).getGuiRenderState()
                .addItem(new GuiItemRenderState(new Matrix3x2f(graphics.pose()), state, x, y, null));
    }

    /**
     * The layers a model resolved into. {@code newLayer} is the only way to see them - the array
     * behind it is private - and a layer's transform can only be set, never read back.
     */
    private static final class LayerTrackingRenderState extends TrackingItemStackRenderState {

        private final List<ItemStackRenderState.LayerRenderState> layers = new ArrayList<>();

        @Override
        public ItemStackRenderState.LayerRenderState newLayer() {
            ItemStackRenderState.LayerRenderState layer = super.newLayer();
            this.layers.add(layer);
            return layer;
        }

        private void applyTransform(ItemTransform transform) {
            this.layers.forEach(layer -> layer.setItemTransform(transform));
        }
    }
}
