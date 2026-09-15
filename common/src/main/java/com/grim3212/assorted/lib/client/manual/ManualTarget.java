package com.grim3212.assorted.lib.client.manual;

import com.grim3212.assorted.lib.manual.ManualLinks;
import com.grim3212.assorted.lib.manual.ManualPageRef;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

/**
 * The page the crosshair is on, for the manual's purposes. Everything the manual points at is
 * pickable, an item frame included, so the crosshair's own hit is the whole answer.
 */
public final class ManualTarget {

    private ManualTarget() {
    }

    @Nullable
    public static ManualPageRef lookedAtPage() {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        HitResult hit = minecraft.hitResult;
        if (level == null || hit == null) {
            return null;
        }

        return switch (hit) {
            case EntityHitResult entityHit -> ManualLinks.pageFor(entityHit.getEntity());
            case BlockHitResult blockHit when hit.getType() == HitResult.Type.BLOCK ->
                    ManualLinks.pageFor(level, blockHit.getBlockPos(), level.getBlockState(blockHit.getBlockPos()));
            default -> null;
        };
    }

    /** Whether that page is one the loaded packs actually have, which is what the HUD marks. */
    public static boolean hasLoadedPage(@Nullable ManualPageRef ref) {
        return ref != null && ManualContent.get().locate(ref).isPresent();
    }
}
