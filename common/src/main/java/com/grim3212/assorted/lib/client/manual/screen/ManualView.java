package com.grim3212.assorted.lib.client.manual.screen;

/** Where the reader is. Values rather than a screen each, so the place survives closing the book. */
public sealed interface ManualView {

    /** The index. */
    record Index(int page) implements ManualView {

        public Index() {
            this(0);
        }
    }

    /** One mod's chapter list. */
    record Chapters(String section, int page) implements ManualView {

        public Chapters(String section) {
            this(section, 0);
        }
    }

    /** @param leftPage index of the left page; the right is the one after it */
    record Pages(String section, String chapter, int leftPage) implements ManualView {
    }
}
