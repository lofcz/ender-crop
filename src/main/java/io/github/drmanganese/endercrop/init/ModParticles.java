package io.github.drmanganese.endercrop.init;

import io.github.drmanganese.endercrop.EnderCrop;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, EnderCrop.MOD_ID);
    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }

    public static final RegistryObject<SimpleParticleType> PARTICLE = PARTICLE_TYPES.register("ender_particles", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> PARTICLE_DEFAULT = PARTICLE_TYPES.register("ender_particles_default", () -> new SimpleParticleType(true));

}
