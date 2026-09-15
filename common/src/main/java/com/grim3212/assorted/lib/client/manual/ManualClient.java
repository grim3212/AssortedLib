package com.grim3212.assorted.lib.client.manual;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.client.manual.page.ImagePage;
import com.grim3212.assorted.lib.client.manual.page.ItemPage;
import com.grim3212.assorted.lib.client.manual.page.RecipePage;
import com.grim3212.assorted.lib.client.manual.page.TextPage;
import com.grim3212.assorted.lib.client.manual.screen.ManualScreen;
import com.grim3212.assorted.lib.manual.ManualLinks;
import com.grim3212.assorted.lib.manual.ManualPageRef;
import com.grim3212.assorted.lib.platform.ClientServices;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

/** Client side setup for the manual, and the way in from the item. */
public final class ManualClient {

    private ManualClient() {
    }

    public static void init() {
        ManualPageTypes.register(pageType("text"), TextPage.CODEC);
        ManualPageTypes.register(pageType("image"), ImagePage.CODEC);
        ManualPageTypes.register(pageType("item"), ItemPage.CODEC);
        ManualPageTypes.register(pageType("recipe"), RecipePage.CODEC);

        ClientServices.CLIENT.addReloadListener(ManualLoader.ID, new ManualLoader());
    }

    private static Identifier pageType(String name) {
        return Identifier.fromNamespaceAndPath(LibConstants.MOD_ID, name);
    }

    /** For the item's tooltip. */
    public static int sectionCount() {
        return ManualContent.get().sections().size();
    }

    /** Opens at {@code ref} when it resolves, otherwise where the book was last left. */
    public static void open(@Nullable ManualPageRef ref) {
        Minecraft.getInstance().gui.setScreen(ManualScreen.openedAt(ref));
    }

    /** Raytraces here because a non living entity is only known to the client. */
    public static void openAtLookedAt() {
        HitResult hit = Minecraft.getInstance().hitResult;
        open(hit instanceof EntityHitResult entityHit ? ManualLinks.pageFor(entityHit.getEntity()) : null);
    }
}
