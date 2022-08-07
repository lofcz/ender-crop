package io.github.drmanganese.endercrop.mixin;

import io.github.drmanganese.endercrop.block.EnderCropBlock;
import io.github.drmanganese.endercrop.init.ModBlocks;
import io.github.drmanganese.endercrop.init.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BoneMealItem.class)
public class BonemealMixin {

    @Inject(method = "addGrowthParticles", at = @At("HEAD"), cancellable = true)
    private static void injectToAddGrowthParticles(LevelAccessor pLevel, BlockPos pPos, int pData, CallbackInfo ci) {

       BlockState blockstate = pLevel.getBlockState(pPos);

       // let original impl proceed if not our block
       if (!blockstate.is(ModBlocks.ENDER_CROP.get())) {
           return;
       }

        if (pData == 0) {
            pData = 15;
        }

        if (!blockstate.isAir()) {
            double d0 = 0.5D;
            double d1;
            if (blockstate.is(Blocks.WATER)) {
                pData *= 3;
                d1 = 1.0D;
                d0 = 3.0D;
            } else if (blockstate.isSolidRender(pLevel, pPos)) {
                pPos = pPos.above();
                pData *= 3;
                d0 = 3.0D;
                d1 = 1.0D;
            } else {
                d1 = blockstate.getShape(pLevel, pPos).max(Direction.Axis.Y);
            }

            pLevel.addParticle(ModParticles.PARTICLE_DEFAULT.get(), (double)pPos.getX() + 0.5D, (double)pPos.getY() + 0.5D, (double)pPos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            Random random = pLevel.getRandom();

            for(int i = 0; i < pData; ++i) {
                double d2 = random.nextGaussian() * 0.02D;
                double d3 = random.nextGaussian() * 0.02D;
                double d4 = random.nextGaussian() * 0.02D;
                double d5 = 0.5D - d0;
                double d6 = (double)pPos.getX() + d5 + random.nextDouble() * d0 * 2.0D;
                double d7 = (double)pPos.getY() + random.nextDouble() * d1;
                double d8 = (double)pPos.getZ() + d5 + random.nextDouble() * d0 * 2.0D;
                if (!pLevel.getBlockState((new BlockPos(d6, d7, d8)).below()).isAir()) {
                    pLevel.addParticle(ModParticles.PARTICLE_DEFAULT.get(), d6, d7, d8, d2, d3, d4);
                }
            }

        }

        ci.cancel();
    }
}
