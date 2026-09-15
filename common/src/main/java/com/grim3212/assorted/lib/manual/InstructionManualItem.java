package com.grim3212.assorted.lib.manual;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.client.manual.ManualClient;
import com.grim3212.assorted.lib.dist.Dist;
import com.grim3212.assorted.lib.dist.DistExecutor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/**
 * The book. Right clicking opens it, at the page of whatever was clicked when that has one.
 */
public class InstructionManualItem extends Item implements IManualEntry.IManualItem {

    /** Its own page, for when one is drawn on a page and clicked. */
    private static final ManualPageRef OWN_PAGE = ManualPageRef.of(LibConstants.MOD_ID, "reading", "crafting");

    public InstructionManualItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return openLookedAt(context.getLevel());
    }

    /** Whatever the crosshair is on that did not already open the book above. */
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return openLookedAt(level);
    }

    @Override
    public ManualPageRef getManualPage(ItemStack stack) {
        return OWN_PAGE;
    }

    private static InteractionResult openLookedAt(Level level) {
        if (level.isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ManualClient::openAtLookedAt);
        }

        return InteractionResult.SUCCESS;
    }

    /** Deprecated for data component tooltips, which cannot count something at runtime. */
    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        Integer sections = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> ManualClient::sectionCount);
        lines.accept(Component.translatable("gui.assortedlib.manual.sections", sections == null ? 0 : sections)
                .withStyle(ChatFormatting.GRAY));
    }
}
