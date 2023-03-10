package com.grim3212.assorted.lib.client.events;

import com.grim3212.assorted.lib.client.render.entity.EntityRenderers;
import com.grim3212.assorted.lib.events.GenericEvent;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RegisterRenderersEvent extends GenericEvent {

    private final EntityRenderers.BlockEntityRendererConsumer blockEntityConsumer;
    private final EntityRenderers.EntityRendererConsumer entityRendererConsumer;

    public RegisterRenderersEvent(EntityRenderers.BlockEntityRendererConsumer blockEntityConsumer, EntityRenderers.EntityRendererConsumer entityRendererConsumer) {
        this.blockEntityConsumer = blockEntityConsumer;
        this.entityRendererConsumer = entityRendererConsumer;
    }


    public <E extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<? extends E> entityType, BlockEntityRendererProvider<E> entityRendererFactory) {
        this.blockEntityConsumer.accept(entityType, entityRendererFactory);
    }

    public <E extends Entity> void registerEntityRenderer(EntityType<? extends E> entityType, EntityRendererProvider<E> entityRendererFactory) {
        this.entityRendererConsumer.accept(entityType, entityRendererFactory);
    }
}
