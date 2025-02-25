package io.github.drmanganese.endercrop;


import io.github.drmanganese.endercrop.configuration.EnderCropConfiguration;
import io.github.drmanganese.endercrop.init.ModBlocks;
import io.github.drmanganese.endercrop.init.ModItems;
import io.github.drmanganese.endercrop.init.ModParticles;
import io.github.drmanganese.endercrop.init.ModSounds;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Random;

@Mod(EnderCrop.MOD_ID)
public final class EnderCrop {

    public static final String MOD_ID = "endercrop";

    public static boolean theOneProbeLoaded;

    public EnderCrop() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EnderCropConfiguration.COMMON_CONFIG);
        theOneProbeLoaded = ModList.get().isLoaded("theoneprobe");
        NetworkHelper.init();
    }
}

