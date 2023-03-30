package com.grim3212.assorted.lib;

import com.grim3212.assorted.lib.data.FabricBiomeTagProvider;
import com.grim3212.assorted.lib.data.FabricBlockTagProvider;
import com.grim3212.assorted.lib.data.FabricItemTagProvider;
import com.grim3212.assorted.lib.data.LibCommonTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class AssortedLibFabricDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        // Fabric doesn't include some basic tags that we use, we need to add them
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        
        FabricBlockTagProvider provider = pack.addProvider((output, registriesFuture) -> new FabricBlockTagProvider(output, registriesFuture, new LibCommonTagProvider.BlockTagProvider(output, registriesFuture)));
        pack.addProvider((output, registriesFuture) -> new FabricItemTagProvider(output, registriesFuture, provider, new LibCommonTagProvider.ItemTagProvider(output, registriesFuture, provider)));
        pack.addProvider((output, registriesFuture) -> new FabricBiomeTagProvider(output, registriesFuture, new LibCommonTagProvider.BiomeTagProvider(output, registriesFuture)));
    }
}
