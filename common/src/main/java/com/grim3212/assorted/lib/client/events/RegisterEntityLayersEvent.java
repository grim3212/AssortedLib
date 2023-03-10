package com.grim3212.assorted.lib.client.events;

import com.grim3212.assorted.lib.events.GenericEvent;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class RegisterEntityLayersEvent extends GenericEvent {

    private final BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer;

    public RegisterEntityLayersEvent(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {
        this.consumer = consumer;
    }

    public void register(ModelLayerLocation modelLayerLocation, Supplier<LayerDefinition> layerDefinition) {
        this.consumer.accept(modelLayerLocation, layerDefinition);
    }
}
