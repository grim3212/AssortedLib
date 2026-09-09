package com.grim3212.assorted.lib.client.data;

import com.grim3212.assorted.lib.client.model.loader.ForgeSpecificationBlockStateModel;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import net.neoforged.neoforge.client.model.generators.blockstate.UnbakedMutator;

/**
 * Datagen side of {@link ForgeSpecificationBlockStateModel}: emits a blockstate variant that names
 * the specification model type instead of the vanilla one.
 * <p>
 * NeoForge ships {@link CustomBlockStateModelBuilder.Simple}, but it drops every
 * {@link VariantMutator} on the floor - its {@code with} returns {@code this} - which would silently
 * throw away the {@code x} / {@code y} / {@code uvlock} that a rotated blockstate is built from. This
 * builder keeps a real {@link Variant} and applies mutators to it, so
 * {@code plainVariant(...).with(yRot(90))} means the same thing here as it does for a vanilla
 * variant.
 */
public final class SpecificationBlockStateModelBuilder extends CustomBlockStateModelBuilder {

    private final Variant variant;

    private SpecificationBlockStateModelBuilder(final Variant variant) {
        this.variant = variant;
    }

    /**
     * A {@link MultiVariant} drawing {@code model} through its model specification, ready to be fed to
     * {@code MultiVariantGenerator} / {@code MultiPartGenerator} and mutated like any other variant.
     */
    public static MultiVariant specificationVariant(final Identifier model) {
        return MultiVariant.of(new SpecificationBlockStateModelBuilder(new Variant(model)));
    }

    @Override
    public CustomBlockStateModelBuilder with(final VariantMutator variantMutator) {
        return new SpecificationBlockStateModelBuilder(this.variant.with(variantMutator));
    }

    @Override
    public CustomBlockStateModelBuilder with(final UnbakedMutator variantMutator) {
        // An UnbakedMutator works on a BlockStateModel.Unbaked, and the only thing it can usefully
        // change on this one is the variant it wraps, so it is applied through that.
        return new SpecificationBlockStateModelBuilder(variantMutator.apply(new net.minecraft.client.renderer.block.dispatch.SingleVariant.Unbaked(this.variant)).variant());
    }

    @Override
    public ForgeSpecificationBlockStateModel toUnbaked() {
        return new ForgeSpecificationBlockStateModel(this.variant);
    }
}
