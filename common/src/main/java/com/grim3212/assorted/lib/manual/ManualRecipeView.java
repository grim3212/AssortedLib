package com.grim3212.assorted.lib.manual;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.StonecutterRecipeDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A recipe reduced to what the manual draws: a grid of input slots and a result, left unresolved so
 * each slot keeps the tag or fuel behind it. Where they land on the page is
 * {@link com.grim3212.assorted.lib.client.manual.ManualRecipeLayout}.
 *
 * @param width     input slots per row
 * @param height    rows of input slots
 * @param inputs    {@code width * height} slots in reading order
 * @param shapeless whether the inputs may go in any arrangement, which the book marks
 */
public record ManualRecipeView(int width, int height, List<ManualSlot> inputs, ManualSlot result, boolean shapeless) {

    /** Packed into rows of three, the width a crafting grid gives them. */
    public static ManualRecipeView shapeless(List<ManualSlot> inputs, ManualSlot result) {
        int width = Math.min(3, Math.max(1, inputs.size()));
        return new ManualRecipeView(width, ceilDiv(inputs.size(), width), padded(inputs, width), result, true);
    }

    public static ManualRecipeView shaped(int width, int height, List<ManualSlot> inputs, ManualSlot result) {
        return new ManualRecipeView(width, height, inputs, result, false);
    }

    /**
     * Empty when neither {@link IManualRecipeProvider} nor a published {@link RecipeDisplay} says
     * how to draw it.
     */
    public static Optional<ManualRecipeView> of(RecipeHolder<?> holder) {
        Recipe<?> recipe = holder.value();

        if (recipe instanceof IManualRecipeProvider provider) {
            return Optional.of(provider.manualView());
        }

        return recipe.display().stream().flatMap(display -> fromDisplay(display).stream()).findFirst();
    }

    private static Optional<ManualRecipeView> fromDisplay(RecipeDisplay display) {
        return switch (display) {
            case ShapedCraftingRecipeDisplay shaped -> Optional.of(shaped(
                    shaped.width(), shaped.height(), slots(shaped.ingredients()), ManualSlot.of(shaped.result())));
            case ShapelessCraftingRecipeDisplay shapeless -> Optional.of(shapeless(
                    slots(shapeless.ingredients()), ManualSlot.of(shapeless.result())));
            case FurnaceRecipeDisplay furnace -> Optional.of(shaped(
                    1, 1, List.of(ManualSlot.of(furnace.ingredient())), ManualSlot.of(furnace.result())));
            case StonecutterRecipeDisplay stonecutter -> Optional.of(shaped(
                    1, 1, List.of(ManualSlot.of(stonecutter.input())), ManualSlot.of(stonecutter.result())));
            default -> Optional.empty();
        };
    }

    private static List<ManualSlot> slots(List<SlotDisplay> displays) {
        return displays.stream().map(ManualSlot::of).toList();
    }

    /** Pads the last row so every row is {@code width} slots. */
    private static List<ManualSlot> padded(List<ManualSlot> inputs, int width) {
        List<ManualSlot> padded = new ArrayList<>(inputs);
        while (padded.size() % width != 0) {
            padded.add(ManualSlot.EMPTY);
        }
        return List.copyOf(padded);
    }

    private static int ceilDiv(int value, int divisor) {
        return Math.max(1, (value + divisor - 1) / divisor);
    }

    public ManualSlot slot(int column, int row) {
        int index = row * this.width + column;
        return index >= 0 && index < this.inputs.size() ? this.inputs.get(index) : ManualSlot.EMPTY;
    }

    public boolean isEmpty() {
        return this.result.isEmpty() && this.inputs.stream().allMatch(ManualSlot::isEmpty);
    }
}
