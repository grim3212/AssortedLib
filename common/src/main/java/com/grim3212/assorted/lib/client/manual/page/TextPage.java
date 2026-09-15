package com.grim3212.assorted.lib.client.manual.page;

import com.grim3212.assorted.lib.client.manual.ManualPage;
import com.grim3212.assorted.lib.client.manual.ManualPageView;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;

import java.util.Optional;

/** Text and nothing else. */
public record TextPage(Optional<Component> title, Component text) implements ManualPage {

    public static final MapCodec<TextPage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ManualTextCodecs.TRANSLATABLE.optionalFieldOf("title").forGetter(TextPage::title),
            ManualTextCodecs.TRANSLATABLE.fieldOf("text").forGetter(TextPage::text)
    ).apply(instance, TextPage::new));

    @Override
    public MapCodec<? extends ManualPage> codec() {
        return CODEC;
    }

    @Override
    public void render(ManualPageView view, int animationTick) {
        view.text(this.text, 0);
    }
}
