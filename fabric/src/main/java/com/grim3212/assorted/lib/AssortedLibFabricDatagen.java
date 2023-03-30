package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.data.FabricLibBiomeTagProvider;
import com.grim3212.assorted.lib.data.FabricLibBlockTagProvider;
import com.grim3212.assorted.lib.data.FabricLibItemTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class AssortedLibFabricDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        // Fabric doesn't include some basic tags that we use, we need to add them
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        FabricLibBlockTagProvider provider = pack.addProvider(FabricLibBlockTagProvider::new);
        pack.addProvider((output, registriesFuture) -> new FabricLibItemTagProvider(output, registriesFuture, provider));
        pack.addProvider((output, registriesFuture) -> new FabricLibBiomeTagProvider(output, registriesFuture));
    }
}
