package com.grim3212.assorted.lib.client.manual.page;

import com.grim3212.assorted.lib.client.manual.ManualPage;
import com.grim3212.assorted.lib.client.manual.ManualPageView;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

/** One item shown large with its name under it; more than one cycles. */
public record ItemPage(Optional<Component> title, List<Holder<Item>> items, int interval, Optional<Component> text) implements ManualPage {

    private static final int SCALE = 3;
    private static final int SLOT_TOP = 92;

    public static final MapCodec<ItemPage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ManualTextCodecs.TRANSLATABLE.optionalFieldOf("title").forGetter(ItemPage::title),
            BuiltInRegistries.ITEM.holderByNameCodec().listOf().fieldOf("items").forGetter(ItemPage::items),
            Codec.INT.optionalFieldOf("interval", 40).forGetter(ItemPage::interval),
            ManualTextCodecs.TRANSLATABLE.optionalFieldOf("text").forGetter(ItemPage::text)
    ).apply(instance, ItemPage::new));

    @Override
    public MapCodec<? extends ManualPage> codec() {
        return CODEC;
    }

    @Override
    public void render(ManualPageView view, int animationTick) {
        this.text.ifPresent(body -> view.text(body, 0, SLOT_TOP - 4));

        if (this.items.isEmpty()) {
            return;
        }

        ItemStack stack = new ItemStack(this.items.get(this.cycle(animationTick)));
        int size = 16 * SCALE;
        int x = (view.width() - size) / 2;

        // Hold still while the cursor is on it.
        if (view.isMouseOver(x, SLOT_TOP, size, size)) {
            view.freezeAnimation();
        }

        view.largeItem(stack, x, SLOT_TOP, SCALE);
        view.centeredText(stack.getHoverName(), SLOT_TOP + size + 6, view.style().textColor());
    }

    private int cycle(int animationTick) {
        return (animationTick / Math.max(1, this.interval)) % this.items.size();
    }
}
