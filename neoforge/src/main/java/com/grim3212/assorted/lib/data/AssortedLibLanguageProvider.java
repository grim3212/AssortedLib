package com.grim3212.assorted.lib.data;

import com.grim3212.assorted.lib.LibConstants;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;

/**
 * Names for the c: item tags AssortedLib declares and neither loader names. Every mod loads this
 * file, so a mod that uses one of these tags needs no name for it.
 */
public class AssortedLibLanguageProvider extends LibLanguageProvider {

    public AssortedLibLanguageProvider(PackOutput output) {
        super(output, LibConstants.MOD_ID);
    }

    @Override
    protected void addNames() {
        for (DyeColor color : DyeColor.values()) {
            String name = titleCase(color.getName());
            this.add("tag.item.c.glass_blocks." + color.getName(), name + " Glass Blocks");
            this.add("tag.item.c.glass_panes." + color.getName(), name + " Glass Panes");
        }
        this.add("tag.item.c.containers.fluid", "Fluid Containers");
        this.add("tag.item.c.dusts.prismarine", "Prismarine Dusts");
        this.add("tag.item.c.storage_blocks.amethyst", "Amethyst Storage Blocks");
        this.add("tag.item.c.storage_blocks.quartz", "Quartz Storage Blocks");
    }
}
