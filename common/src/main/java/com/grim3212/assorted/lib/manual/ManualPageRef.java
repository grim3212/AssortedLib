package com.grim3212.assorted.lib.manual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

/**
 * A page address, written {@code <section>:<chapter>/<page>} or {@code <section>:<chapter>} for the
 * chapter's first page. Resolved only when the book opens, so common code can name a page this
 * client may not have loaded; one that resolves to nothing falls back to the chapter or the index.
 */
public record ManualPageRef(String section, String chapter, String page) {

    public static final Codec<ManualPageRef> CODEC = Codec.STRING.comapFlatMap(ManualPageRef::read, ManualPageRef::toString).stable();

    /** Addresses the chapter rather than a page within it. */
    public static ManualPageRef ofChapter(String section, String chapter) {
        return new ManualPageRef(section, chapter, "");
    }

    public static ManualPageRef of(String section, String chapter, String page) {
        return new ManualPageRef(section, chapter, page);
    }

    public boolean isChapterOnly() {
        return this.page.isEmpty();
    }

    public static DataResult<ManualPageRef> read(String value) {
        int colon = value.indexOf(':');
        if (colon <= 0 || colon == value.length() - 1) {
            return DataResult.error(() -> "Not a manual page reference, expected <section>:<chapter>[/<page>]: " + value);
        }

        String section = value.substring(0, colon);
        String rest = value.substring(colon + 1);
        int slash = rest.indexOf('/');
        if (slash < 0) {
            return DataResult.success(ofChapter(section, rest));
        }
        if (slash == 0 || slash == rest.length() - 1) {
            return DataResult.error(() -> "Manual page reference has an empty chapter or page: " + value);
        }

        return DataResult.success(of(section, rest.substring(0, slash), rest.substring(slash + 1)));
    }

    public static ManualPageRef parse(String value) {
        return read(value).getOrThrow(IllegalArgumentException::new);
    }

    @Override
    public String toString() {
        return this.isChapterOnly() ? this.section + ":" + this.chapter : this.section + ":" + this.chapter + "/" + this.page;
    }
}
