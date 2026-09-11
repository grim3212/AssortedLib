package com.grim3212.assorted.lib.client.data;

import com.grim3212.assorted.lib.client.model.loader.ForgeSpecificationBlockStateModel;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import net.neoforged.neoforge.client.model.generators.blockstate.UnbakedMutator;

/**
 * Datagen side of {@link ForgeSpecificationBlockStateModel}: a blockstate variant naming the
 * specification model type. NeoForge's {@link CustomBlockStateModelBuilder.Simple} ignores every
 * {@link VariantMutator}, dropping {@code x} / {@code y} / {@code uvlock}; this one applies them to
 * a real {@link Variant}.
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
