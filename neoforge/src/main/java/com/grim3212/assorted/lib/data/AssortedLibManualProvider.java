package com.grim3212.assorted.lib.data;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.manual.LibItems;
import com.grim3212.assorted.lib.manual.ManualSection;
import net.minecraft.data.PackOutput;

/** The library's own section, which explains the book itself and sorts above every mod's. */
public class AssortedLibManualProvider extends LibManualProvider {

    public AssortedLibManualProvider(PackOutput output) {
        super(output, LibConstants.MOD_ID);
    }

    @Override
    protected void addChapters() {
        this.section(ManualSection.LIB_SORT_ORDER, LibItems.INSTRUCTION_MANUAL.get());

        ChapterBuilder reading = this.chapter("reading");
        reading.text("pointing");
        reading.text("recipes");
        reading.text("marks");
        reading.recipes("crafting", LibItems.INSTRUCTION_MANUAL.get()).opens(LibItems.INSTRUCTION_MANUAL.get());
    }
}
