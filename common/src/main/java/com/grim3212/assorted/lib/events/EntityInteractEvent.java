package com.grim3212.assorted.lib.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class EntityInteractEvent extends GenericEvent {

    private final Entity target;
    private final InteractionHand hand;
    private final BlockPos pos;
    private final Player player;
    private InteractionResult result = InteractionResult.PASS;

    public EntityInteractEvent(Player player, InteractionHand hand, Entity target) {
        this.player = player;
        this.target = target;
        this.hand = hand;
        this.pos = target.blockPosition();
    }

    public Player getPlayer() {
        return player;
    }

    public Entity getTarget() {
        return target;
    }

    public BlockPos getPos() {
        return pos;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public InteractionResult getInteractionResult() {
        return result;
    }

    public void setResult(InteractionResult result) {
        this.result = result;
    }
}
