package io.github.drmanganese.endercrop;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class Particle extends TextureSheetParticle {

    float dirX = 0;
    float dirZ = 0;
    float mult = MathExt.RandomBetween(0.01f, 0.03f);

    protected Particle(ClientLevel level, double x, double y, double z, SpriteSet ss, double xd, double yd, double zd) {
        super(level, x + 0.5D, y + MathExt.RandomBetween(0f, 0.8f), z + 0.5D);

        dirX = MathExt.RandomBetween(-1f, 1f);
        dirZ = MathExt.RandomBetween(-1f, 1f);

        friction = 0f;
        quadSize *= 0.85f;
        lifetime = (int)MathExt.RandomBetween(30, 50);
        rCol = 1f;
        gCol = 1f;
        bCol = 1f;
        this.xd = dirX * 1.1f;
        this.yd = MathExt.RandomBetween(-0.2f, -0.1f);
        this.zd = dirZ * 1.1f;
        gravity = MathExt.RandomBetween(-0.4f, -0.2f);
        //ss.get()
        setSprite(ss.get(MathExt.Rnd));
        hasPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        gravity -= 0.01;
        //yd -= 0.02;

        if (gravity < -0.5f) {
            gravity = -0.5f;
        }

        xd += dirX * mult;
        zd += dirZ * mult;

        alpha = -(1/(float)lifetime) * age + 1;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Nullable
        @Override
        public net.minecraft.client.particle.Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new Particle(pLevel, pX, pY, pZ, sprites, pXSpeed, pYSpeed, pZSpeed);
        }
    }
}
