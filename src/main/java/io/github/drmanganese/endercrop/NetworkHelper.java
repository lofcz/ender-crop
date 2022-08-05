package io.github.drmanganese.endercrop;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class NetworkHelper {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(EnderCrop.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        int index = 0;
        INSTANCE.messageBuilder(NetworkMsg.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(NetworkMsg::Encode)
                .decoder(NetworkMsg::new)
                .consumer(NetworkMsg::Handle)
                .add();

    }
}
