package com.grim3212.assorted.lib.client.model;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * An {@link UnbakedModel} that inherits everything from another model but swaps out some of its
 * texture slots.
 * <p>
 * In 1.20.1 this subclassed {@code BlockModel} and overrode {@code getMaterial}, reaching into the
 * private {@code textureMap} through a mixin accessor to copy the parent state. 26.2 makes that both
 * unnecessary and impossible - the json model class is {@code CuboidModel}, a record, and textures are
 * a {@link TextureSlots.Data} rather than a mutable map.
 * <p>
 * The replacement uses the vanilla inheritance mechanism directly: an {@code UnbakedModel} declares a
 * {@link #parent()} id and its own {@link #textureSlots()}, and
 * {@link net.minecraft.client.resources.model.ResolvedModel#findTopTextureSlots} walks that chain
 * child-first, so slots declared here win over the ones the parent declares. Everything a retexture
 * does not touch - geometry, ambient occlusion, gui light, transforms - is left null and therefore
 * inherited.
 */
public class RetexturableBlockModel implements UnbakedModel {
    private final Identifier parentLocation;
    private final Map<String, Material> replacements = new HashMap<>();

    private TextureSlots.@Nullable Data resolved;

    public static RetexturableBlockModel from(Identifier parentLocation) {
        return new RetexturableBlockModel(parentLocation);
    }

    public RetexturableBlockModel(Identifier parentLocation) {
        this.parentLocation = parentLocation;
    }

    @Override
    public @Nullable Identifier parent() {
        return this.parentLocation;
    }

    @Override
    public TextureSlots.Data textureSlots() {
        if (this.resolved == null) {
            var builder = new TextureSlots.Data.Builder();
            this.replacements.forEach(builder::addTexture);
            this.resolved = builder.build();
        }

        return this.resolved;
    }

    public void replaceTexture(String name, Identifier texture) {
        this.replacements.put(name, new Material(texture));
        this.resolved = null;
    }

    public RetexturableBlockModel retexture(ImmutableMap<String, String> textures) {
        textures.forEach((name, texture) -> replaceTexture(name, Identifier.parse(texture)));
        return this;
    }
}
