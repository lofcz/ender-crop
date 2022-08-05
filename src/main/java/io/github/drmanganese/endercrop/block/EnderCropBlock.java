package io.github.drmanganese.endercrop.block;

import io.github.drmanganese.endercrop.MathExt;
import io.github.drmanganese.endercrop.NetworkHelper;
import io.github.drmanganese.endercrop.NetworkMsg;
import io.github.drmanganese.endercrop.Particle;
import io.github.drmanganese.endercrop.configuration.EnderCropConfiguration;
import io.github.drmanganese.endercrop.init.ModBlocks;
import io.github.drmanganese.endercrop.init.ModItems;
import io.github.drmanganese.endercrop.init.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import java.util.Random;

public class EnderCropBlock extends CropBlock {

    public static final int FLAG_UPDATE_BLOCK = 1;
    public static final int FLAG_SEND_CLIENT_CHANGES = 2;
    public static final int FLAG_NO_RERENDER = 4;
    public static final int FORSE_RERENDER = 8;
    public static final int FLAG_IGNORE_OBSERVERS = 16;

    private void SendMsg(String text) {
        LocalPlayer player = Minecraft.getInstance().player;
        player.sendMessage(new TextComponent(text), player.getUUID());
    }

    private static final Properties PROPERTIES = BlockBehaviour.Properties
        .of(Material.PLANT)
        .noCollission()
        .randomTicks()
        .instabreak()
        .sound(SoundType.CROP);

    public EnderCropBlock() {
        super(PROPERTIES);
    }

    private static boolean isOnEndstone(LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.below()).is(ModBlocks.TILLED_END_STONE.get());
    }

    // Vanilla growth chance is calculated as 1 in floor(25/points) + 1.
    // For the ender crop we multiply 'points' by the appropriate multiplier.
    protected static float getGrowthSpeed(Block blockIn, Level worldIn, BlockPos pos) {

        float baseChance = CropBlock.getGrowthSpeed(blockIn, worldIn, pos);
        if (isOnEndstone(worldIn, pos)) {
            baseChance *= EnderCropConfiguration.tilledEndMultiplier.get();
        } else {
            baseChance *= EnderCropConfiguration.tilledSoilMultiplier.get();
        }

        return baseChance;
    }

    @Override
    @Nonnull
    protected ItemLike getBaseSeedId() {
        return ModItems.ENDER_SEEDS.get();
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.FARMLAND) || state.is(ModBlocks.TILLED_END_STONE.get());
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        boolean lightCheck;
        if (isOnEndstone(pLevel, pPos))
            lightCheck = pLevel.getRawBrightness(pPos, 0) >= 8 || pLevel.canSeeSky(pPos);
        else
            lightCheck = pLevel.getRawBrightness(pPos, 0) <= 15;

        final BlockPos below = pPos.below();
        if (pState.getBlock() == this) //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            return lightCheck && pLevel.getBlockState(below).canSustainPlant(pLevel, below, Direction.UP, this);
        return lightCheck && this.mayPlaceOn(pLevel.getBlockState(below), pLevel, below);
    }


    public static void addGrowthParticles(LevelAccessor pLevel, BlockPos pPos, int pData) {

        for (int i = 0; i < 30; i++) {
            pLevel.addParticle(ModParticles.PARTICLE.get(), pPos.getX(), pPos.getY(), pPos.getZ(), 0, 0.1f, 0);
        }

        /*if (pData == 0) {
            pData = 15;
        }

        BlockState blockstate = pLevel.getBlockState(pPos);
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

            pLevel.addParticle(ParticleTypes.EXPLOSION, (double)pPos.getX() + 0.5D, (double)pPos.getY() + 0.5D, (double)pPos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
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
                    pLevel.addParticle(ParticleTypes.EXPLOSION, d6, d7, d8, d2, d3, d4);
                }
            }
        }*/
    }

    @Override
    public void performBonemeal(ServerLevel pLevel, Random pRandom, BlockPos pPos, BlockState pState) {
        SendMsg("performBonemeal");

        //Pig p = new Pig(EntityType.PIG, pLevel);
        //p.setPos(pPos.getX(), pPos.getY(), pPos.getZ());
        //pLevel.addFreshEntity(p);

        //Minecraft.getInstance().level.addParticle(ParticleTypes.ELECTRIC_SPARK, pPos.getX(), pPos.getY() - 2, pPos.getZ(), 1, -1, 1);

       // addGrowthParticles(pLevel, pPos, 0);

        //addParticle(ParticleTypes.EXPLOSION, pPos.getX() + 0.5D, pPos.getY() + 0.5D, pPos.getZ() + 0.5D, 0, 0, 0);
        //addGrowthParticles(pLevel, pPos, 0);

        int baseAgeP = 1;

        if (MathExt.RandomBetween(0, 1f) > 0.75f) {
            baseAgeP = 2;
            NetworkHelper.INSTANCE.send(PacketDistributor.ALL.with(null), new NetworkMsg(pPos));
        }

        int i = this.getAge(pState) + baseAgeP;
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }

        pLevel.setBlock(pPos, this.getStateForAge(i), 2);
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter world, BlockPos pos, BlockState state, boolean pIsClient) {
        SendMsg("isValidBonemealTarget");
        return !this.isMaxAge(state);
    }

    @Override
    public boolean isBonemealSuccess(Level world, Random random, BlockPos pos, BlockState state) {

        SendMsg("isBonemealSuccess");
        return true;
    }

    @Override
    protected int getBonemealAgeIncrease(Level pLevel) {
        SendMsg("getBonemealAgeIncrease");
        return 0;
    }


    public boolean isDarkEnough(Level worldIn, BlockPos pos) {
        return isOnEndstone(worldIn, pos) || worldIn.getRawBrightness(pos, 0) <= 15;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {


        if (isDarkEnough(level, pos)) {
            int age = this.getAge(state);
            if (!this.isMaxAge(state)) {
                final float growthChance = getGrowthSpeed(this, level, pos);
                if (ForgeHooks.onCropsGrowPre(level, pos, state, random.nextInt((int) (25.0F / growthChance) + 1) == 0)) {
                    level.setBlock(pos, this.getStateForAge(age + 1), 2);
                    ForgeHooks.onCropsGrowPost(level, pos, state);
                }
            }
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        //Pig p = new Pig(EntityType.PIG, level);
        //p.setPos(pos.getX(), pos.getY(), pos.getZ());
        //level.addFreshEntity(p);

        SendMsg("onDestroyedByPlayer");

        final boolean destroyed = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);

        if (destroyed
            && EnderCropConfiguration.miteChance.get() > 0
            && isOnEndstone(level, pos)
            && this.isMaxAge(state)) {
            final int roll = level.random.nextInt(EnderCropConfiguration.miteChance.get());
            if (roll == 0) {
                final Endermite mite = EntityType.ENDERMITE.create(level);
                if (mite != null) {
                    mite.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                    mite.setYBodyRot(Mth.wrapDegrees(level.random.nextFloat() * 360.0F));
                    mite.setTarget(player);
                    level.addFreshEntity(mite);
                }
            }
        }

        return destroyed;
    }
}
