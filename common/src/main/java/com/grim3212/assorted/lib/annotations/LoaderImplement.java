package com.grim3212.assorted.lib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark methods that are loader specific and that we can't use @Override on
 * This way for updates we know areas to validate against
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface LoaderImplement {
    String value();

    Loader loader();

    enum Loader {
        FORGE, FABRIC
    }
}
