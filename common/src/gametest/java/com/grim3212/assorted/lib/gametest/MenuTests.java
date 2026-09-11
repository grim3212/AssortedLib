package com.grim3212.assorted.lib.gametest;

import com.grim3212.assorted.lib.core.inventory.MenuData;
import com.grim3212.assorted.lib.platform.Services;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Menu data: what the server writes for a menu is what the client's menu factory is given.
 */
final class MenuTests {

    private MenuTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("menu_data_round_trips", MenuTests::menuDataRoundTrips);
    }

    /**
     * A menu type from {@code createMenuType} keeps its codec in {@link MenuData}, and writing then
     * reading through it hands the factory the same value with no bytes left over. One made without a
     * codec carries no data. The types are never registered; nothing here needs them to be.
     */
    // NeoForge deprecates RegistryFriendlyByteBuf.decorator for an overload only its patched jar has.
    @SuppressWarnings("deprecation")
    private static void menuDataRoundTrips(GameTestHelper helper) {
        MenuType<PosMenu> type = Services.PLATFORM.createMenuType(PosMenu::new, BlockPos.STREAM_CODEC);
        helper.assertTrue(MenuData.hasData(type), "a menu type made with a codec is not in MenuData");
        helper.assertFalse(MenuData.hasData(Services.PLATFORM.createMenuType(PosMenu::new)), "a menu type made without a codec claims to carry data");

        BlockPos sent = new BlockPos(12, -40, 345);
        RegistryFriendlyByteBuf buf = RegistryFriendlyByteBuf.decorator(helper.getLevel().registryAccess()).apply(Unpooled.buffer());
        MenuData.write(type, sent, buf);

        Inventory inventory = helper.makeMockPlayer(GameType.SURVIVAL).getInventory();
        PosMenu menu = MenuData.read(type, 7, inventory, buf);
        helper.assertValueEqual(menu.pos, sent, "the position the client menu was built from");
        helper.assertValueEqual(menu.containerId, 7, "the client menu's container id");
        helper.assertValueEqual(buf.readableBytes(), 0, "bytes left after the client read the menu data");

        helper.succeed();
    }

    private static final class PosMenu extends AbstractContainerMenu {
        final @Nullable BlockPos pos;

        PosMenu(int containerId, Inventory inventory) {
            this(containerId, inventory, null);
        }

        PosMenu(int containerId, Inventory inventory, @Nullable BlockPos pos) {
            super(null, containerId);
            this.pos = pos;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }
    }
}
