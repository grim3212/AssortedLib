package com.grim3212.assorted.lib.gametest;

import com.grim3212.assorted.lib.events.AnvilUpdatedEvent;
import com.grim3212.assorted.lib.events.CorrectToolForDropEvent;
import com.grim3212.assorted.lib.events.EntityInteractEvent;
import com.grim3212.assorted.lib.events.OnDropStacksEvent;
import com.grim3212.assorted.lib.events.UseBlockEvent;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.platform.services.IPlatformHelper;
import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Helpers, constants and the probe event handlers shared by AssortedLib's gametest classes, which import them statically.
 */
final class LibTestSupport {

    private LibTestSupport() {
    }

    /** Somewhere central in the 9x9x9 test box, one block above the floor. */
    static final BlockPos WORK = new BlockPos(4, 1, 4);

    static void assertLoaded(GameTestHelper helper, String platform, String service, Object impl) {
        helper.assertTrue(impl != null, service + " did not resolve");
        helper.assertTrue(impl.getClass().getSimpleName().startsWith(platform),
                service + " resolved to " + impl.getClass().getName() + ", which is not a " + platform + " implementation");
    }

    /** Each tier gate on a pickaxe must match what vanilla lets that pickaxe harvest. */
    static void assertPickaxeTier(GameTestHelper helper, Item pickaxe) {
        ItemStack stack = new ItemStack(pickaxe);
        assertTierMatches(helper, stack, IPlatformHelper.ToolTier.STONE, Blocks.IRON_ORE);
        assertTierMatches(helper, stack, IPlatformHelper.ToolTier.IRON, Blocks.DIAMOND_ORE);
        assertTierMatches(helper, stack, IPlatformHelper.ToolTier.DIAMOND, Blocks.OBSIDIAN);
    }

    static void assertTierMatches(GameTestHelper helper, ItemStack stack, IPlatformHelper.ToolTier tier, Block gatedBlock) {
        boolean throughLib = Services.PLATFORM.isTieredTool(stack, tier, IPlatformHelper.ToolType.PICKAXE);
        boolean vanilla = stack.isCorrectToolForDrops(gatedBlock.defaultBlockState());
        helper.assertTrue(throughLib == vanilla, stack.getItem() + " at tier " + tier + ": isTieredTool said "
                + throughLib + " but vanilla harvests " + gatedBlock.getName().getString() + " = " + vanilla);
    }

    static void assertItemTagHolds(GameTestHelper helper, TagKey<Item> tag, Item item) {
        helper.assertTrue(new ItemStack(item).is(tag), "item tag " + tag.location() + " does not contain " + BuiltInRegistries.ITEM.getKey(item));
    }

    static void assertBlockTagHolds(GameTestHelper helper, TagKey<Block> tag, Block block) {
        helper.assertTrue(block.defaultBlockState().is(tag), "block tag " + tag.location() + " does not contain " + BuiltInRegistries.BLOCK.getKey(block));
    }

    static ItemStack combine(ServerPlayer player, ItemStack left) {
        AnvilMenu menu = new AnvilMenu(0, player.getInventory());
        menu.getSlot(AnvilMenu.INPUT_SLOT).set(left);
        menu.getSlot(AnvilMenu.ADDITIONAL_SLOT).set(new ItemStack(Items.STICK));
        return menu.getSlot(AnvilMenu.RESULT_SLOT).getItem();
    }

    /**
     * The name the probe handlers answer to. They are registered for the life of the server, so
     * everything they do is gated on a stack carrying it, which nothing else in a test run does.
     */
    static final String PROBE = "assortedlib gametest probe";

    static boolean probesRegistered;

    static ItemStack probe(Item item) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(PROBE));
        return stack;
    }

    static boolean isProbe(ItemStack stack) {
        Component name = stack.get(DataComponents.CUSTOM_NAME);
        return name != null && PROBE.equals(name.getString());
    }

    /**
     * A handler for each library event that answers only for a {@link #probe} stack, with an
     * outcome vanilla never produces, so a test can see the event reached it. Registered on first
     * use because nothing in {@code main} may reference the gametest source set.
     */
    static synchronized void registerProbes() {
        if (probesRegistered) {
            return;
        }
        probesRegistered = true;

        Services.EVENTS.registerEvent(UseBlockEvent.class, (final UseBlockEvent event) -> {
            if (isProbe(event.getPlayer().getItemInHand(event.getHand()))) {
                event.setResult(InteractionResult.SUCCESS);
            }
        });
        Services.EVENTS.registerEvent(EntityInteractEvent.class, (final EntityInteractEvent event) -> {
            if (isProbe(event.getPlayer().getItemInHand(event.getHand()))) {
                event.setCanceled(true);
                event.setResult(InteractionResult.SUCCESS);
            }
        });
        Services.EVENTS.registerEvent(AnvilUpdatedEvent.class, (final AnvilUpdatedEvent event) -> {
            if (isProbe(event.getLeft())) {
                event.setOutput(new ItemStack(Items.DIAMOND));
                event.setCost(1);
                event.setMaterialCost(1);
            }
        });
        Services.EVENTS.registerEvent(CorrectToolForDropEvent.class, (final CorrectToolForDropEvent event) -> {
            if (isProbe(event.getStack()) && event.getState().is(Blocks.OBSIDIAN)) {
                event.setResponse(Optional.of(true));
            }
        });
        Services.EVENTS.registerEvent(OnDropStacksEvent.class, (final OnDropStacksEvent event) -> {
            if (isProbe(event.getStack())) {
                event.setDrops(List.of(new ItemStack(Items.DIAMOND)));
            }
        });
    }

    /**
     * A real, fully joined survival player, the same way AssortedTools' tests build one:
     * {@code makeMockServerPlayerInLevel} is deprecated for removal and forced to creative, and the
     * other two mock factories hand back a player with no connection.
     */
    static ServerPlayer survivalPlayer(GameTestHelper helper, ItemStack held) {
        ServerLevel level = helper.getLevel();
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "assortedlib-test"), false);
        ServerPlayer player = new ServerPlayer(level.getServer(), level, cookie.gameProfile(), cookie.clientInformation());

        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        level.getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
        helper.runBeforeTestEnd(() -> level.getServer().getPlayerList().remove(player));

        player.setGameMode(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, held);
        return player;
    }
}
