# Assorted Lib

The library mod that all of the Assorted Mods use for easier development and code reuse.

Minecraft 26.2, on both NeoForge and Fabric from a single source tree. Branches are per Minecraft
version; `26.2` is the current one.

## Issue Reporting

Please include the following

* Minecraft version
* Loader and its version — NeoForge, or Fabric Loader together with Fabric API
* Assorted Lib version
* Which Assorted mod you were using it with
* The full `latest.log`, plus the crash report if the game crashed

## Using it in a mod

Assorted Lib publishes three artifacts per version:

```
com.grim3212.assorted.lib:assortedlib-common-26.2:<version>
com.grim3212.assorted.lib:assortedlib-fabric-26.2:<version>
com.grim3212.assorted.lib:assortedlib-neoforge-26.2:<version>
```

Depend on `common` from your common module and on the matching loader artifact from each loader
module. The Minecraft version is part of the artifactId, so each version of the game gets its
own artifact tree. They are published to `https://maven.grimoid.com/mods`; to test a local build
instead, install it to your own Maven and resolve from `mavenLocal()`:

```bash
./gradlew publishToMavenLocal
```

## Building

JDK 25 and the bundled Gradle wrapper. `common/` holds the loader-agnostic code; both loader
modules compile those sources inline rather than depending on a common jar, so there is nothing to
install between them.

```bash
./gradlew build                        # every module; jars land in <module>/build/libs
./gradlew :neoforge:runClient
./gradlew :fabric:runClient
./gradlew :neoforge:runGameTestServer  # headless gametests, non-zero exit on failure
./gradlew :fabric:runGameTest
./gradlew :neoforge:runClientData      # datagen
./gradlew :fabric:runDatagenClient
```

Generated resources are committed. Datagen output is regenerated, never hand-edited.

## License

[LGPL-3.0-only](LICENSE).
