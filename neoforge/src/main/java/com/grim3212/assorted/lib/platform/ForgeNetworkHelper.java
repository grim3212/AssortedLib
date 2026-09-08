package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.LibConstants;
import com.grim3212.assorted.lib.platform.services.INetworkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ForgeNetworkHelper implements INetworkHelper {

    private static final Map<Class<?>, MessageHandler<?>> messageHandlers = new ConcurrentHashMap<>();
    private static final Map<String, Integer> idCounter = new ConcurrentHashMap<>();
    private static NetworkEvent.Context replyContext;

    @Override
    public <MSG> void register(MessageHandler<MSG> handler) {
        messageHandlers.put(handler.messageType(), handler);

        String modId = handler.id().getNamespace();
        SimpleChannel channel = Channels.get(modId);

        channel.registerMessage(nextId(modId), handler.messageType(), handler.encoder(), handler.decoder(), (message, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            NetworkDirection expectedDirection = handler.side() == MessageBoundSide.CLIENT ? NetworkDirection.PLAY_TO_CLIENT : NetworkDirection.PLAY_TO_SERVER;

            if (context.getDirection() != expectedDirection) {
                LibConstants.LOG.warn("Received {} on incorrect side. Expected on {}", handler.id(), context.getDirection());
                return;
            }

            context.enqueueWork(() -> {
                replyContext = context;
                ServerPlayer player = context.getSender();
                handler.messageConsumer().accept(message, player);
                replyContext = null;
            });
            context.setPacketHandled(true);
        });
    }

    @Override
    public <MSG> void sendToNearby(Level world, BlockPos pos, MSG toSend) {
        if (world instanceof ServerLevel) {
            MessageHandler<MSG> handler = (MessageHandler<MSG>) messageHandlers.get(toSend.getClass());

            ServerLevel ws = (ServerLevel) world;
            SimpleChannel channel = Channels.get(handler.id().getNamespace());
            ws.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).stream().filter(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64).forEach(p -> channel.send(PacketDistributor.PLAYER.with(() -> p), toSend));
        }
    }

    @Override
    public <MSG> void sendToNearby(Level world, Entity entity, MSG toSend) {
        sendToNearby(world, entity.blockPosition(), toSend);
    }

    @Override
    public <MSG> void sendTo(Player player, MSG toSend) {
        MessageHandler<MSG> handler = (MessageHandler<MSG>) messageHandlers.get(toSend.getClass());
        SimpleChannel channel = Channels.get(handler.id().getNamespace());
        channel.send(PacketDistributor.PLAYER.with(() -> ((ServerPlayer) player)), toSend);
    }

    @Override
    public <MSG> void sendToServer(MSG toSend) {
        MessageHandler<MSG> handler = (MessageHandler<MSG>) messageHandlers.get(toSend.getClass());
        SimpleChannel channel = Channels.get(handler.id().getNamespace());
        channel.sendToServer(toSend);
    }

    private static int nextId(String modId) {
        return idCounter.compute(modId, (key, prev) -> prev != null ? prev + 1 : 0);
    }


    class Channels {
        private static final String PROTOCOL = "7";
        private static final Map<String, SimpleChannel> channels = new ConcurrentHashMap<>();

        public static SimpleChannel get(String modId) {
            return channels.computeIfAbsent(modId, key -> {
                ResourceLocation channelName = new ResourceLocation(key, "channel");
                return NetworkRegistry.newSimpleChannel(channelName, () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
            });
        }
    }
}
