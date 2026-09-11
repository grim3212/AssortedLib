package com.grim3212.assorted.lib.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AnvilUpdatedEvent extends GenericEvent {

    private final ItemStack left;
    private final ItemStack right;
    private final String name;
    private ItemStack output;
    private int cost;
    private int materialCost;
    private final Player player;

    public AnvilUpdatedEvent(ItemStack left, ItemStack right, String name, int cost, Player player) {
        this.left = left;
        this.right = right;
        this.output = ItemStack.EMPTY;
        this.name = name;
        this.player = player;
        this.setCost(cost);
        this.setMaterialCost(0);
    }

    /**
     * @return The item in the left input (leftmost) anvil slot.
     */
    public ItemStack getLeft() {
        return left;
    }

    /**
     * @return The item in the right input (center) anvil slot.
     */
    public ItemStack getRight() {
        return right;
    }

    /**
     * The name the client sent, or null if none was sent. Empty means the player wants the custom
     * name cleared.
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * The output set by handlers, not vanilla's result for these inputs: empty unless a handler
     * changed it, and discarded if the event is cancelled.
     */
    public ItemStack getOutput() {
        return output;
    }

    /**
     * Sets the output slot to a specific itemstack.
     *
     * @param output The stack to change the output to.
     */
    public void setOutput(ItemStack output) {
        this.output = output;
    }

    /** The level cost; unless changed, the sum of both inputs' repair costs. */
    public int getCost() {
        return cost;
    }

    /** Sets the level cost. A player without enough levels cannot take the output. */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * The material cost is how many units of the right input stack are consumed.
     *
     * @return The material cost of this anvil operation.
     */
    public int getMaterialCost() {
        return materialCost;
    }

    /**
     * Sets how many of the right input are consumed. Zero, or more than the stack holds, consumes
     * the whole stack. It does not stop the output being taken.
     */
    public void setMaterialCost(int materialCost) {
        this.materialCost = materialCost;
    }

    /**
     * @return The player using this anvil container.
     */
    public Player getPlayer() {
        return this.player;
    }

}
