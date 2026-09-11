package com.grim3212.assorted.lib.events;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class UseBlockEvent extends GenericEvent {
    private final Player player;
    private final Level level;
    private final InteractionHand hand;
    private final BlockHitResult hitResult;
    private InteractionResult result = InteractionResult.PASS;

    public UseBlockEvent(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        this.player = player;
        this.level = level;
        this.hand = hand;
        this.hitResult = hitResult;
    }

    public Player getPlayer() {
        return player;
    }

    public Level getLevel() {
        return level;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public BlockHitResult getHitResult() {
        return hitResult;
    }

    public InteractionResult getInteractionResult() {
        return result;
    }

    /**
     * Any result other than {@link InteractionResult#PASS} ends the click with that result: the
     * block's and the item's own use do not run afterwards.
     */
    public void setResult(InteractionResult result) {
        this.result = result;
    }

    /**
     * What the loader should answer for this click, or empty to let the rest of the interaction
     * run. Both loaders' bridges go through this so they cannot disagree: a cancelled event fails
     * the click, and a non-{@code PASS} result ends it with that result.
     */
    public Optional<InteractionResult> outcome() {
        if (this.isCanceled()) {
            return Optional.of(InteractionResult.FAIL);
        }

        return this.result == InteractionResult.PASS ? Optional.empty() : Optional.of(this.result);
    }
}
