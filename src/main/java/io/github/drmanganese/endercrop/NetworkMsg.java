package io.github.drmanganese.endercrop;

import io.github.drmanganese.endercrop.block.EnderCropBlock;
import io.github.drmanganese.endercrop.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class NetworkMsg {
    public BlockPos bp;

    public NetworkMsg(BlockPos bp) {
        this.bp = bp;
    }

    public NetworkMsg(FriendlyByteBuf b) {
        this.bp = b.readBlockPos();
    }

    public void Encode(FriendlyByteBuf b) {
        b.writeBlockPos(bp);
    }

    public boolean Handle(Supplier<NetworkEvent.Context> ctx) {
        AtomicBoolean ok = new AtomicBoolean(true);
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                EnderCropBlock.addGrowthParticles(Minecraft.getInstance().level , bp, 0);
                Minecraft.getInstance().level.playSound(Minecraft.getInstance().player, bp, ModSounds.BONEMEAL_CRIT.get(), SoundSource.BLOCKS, 1f, 1f);
            });
        });

        ctx.get().setPacketHandled(true);
        return ok.get();
    }
}
