package io.github.drmanganese.endercrop.init;

import io.github.drmanganese.endercrop.EnderCrop;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, EnderCrop.MOD_ID);

    public static void Register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

    private static RegistryObject<SoundEvent> RegisterSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(EnderCrop.MOD_ID, name)));
    }

    public static final RegistryObject<SoundEvent> BONEMEAL_CRIT = RegisterSoundEvent("bonemeal_crit");
}
