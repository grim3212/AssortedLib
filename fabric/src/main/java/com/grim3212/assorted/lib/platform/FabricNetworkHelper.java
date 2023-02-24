package com.grim3212.assorted.lib.platform;

import com.grim3212.assorted.lib.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FabricNetworkHelper implements INetworkHelper {

    private static final Map<Class<?>, MessageHandler<?>> messageHandlers = new ConcurrentHashMap<>();
    private static final List<MessageHandler<?>> clientMessageHandlers = new ArrayList<>();
    private static Player replyPlayer;

    @Override
    public <MSG> void register(MessageHandler<MSG> handler) {
        messageHandlers.put(handler.messageType(), handler);

        if (handler.side() == MessageBoundSide.CLIENT) {
            clientMessageHandlers.add(handler);
            return;
        }

        ServerPlayNetworking.registerGlobalReceiver(handler.id(), ((server, player, listener, buf, responseSender) -> {
            MSG message = handler.decoder().apply(buf);
            server.execute(() -> {
                replyPlayer = player;
                handler.messageConsumer().accept(message, player);
                replyPlayer = null;
            });
        }));
    }

    public static void initializeClientHandlers() {
        for (MessageHandler<?> handler : clientMessageHandlers) {
            registerClientReceiver(handler);
        }
    }

    private static <MSG> void registerClientReceiver(MessageHandler<MSG> handler) {
        ClientPlayNetworking.registerGlobalReceiver(handler.id(), ((client, listener, buf, responseSender) -> {
            MSG message = handler.decoder().apply(buf);
            client.execute(() -> handler.messageConsumer().accept(message, Minecraft.getInstance().player));
        }));
    }

    @Override
    public <MSG> void sendToNearby(Level world, BlockPos pos, MSG toSend) {
        if (world instanceof ServerLevel) {
            MessageHandler<MSG> handler = (MessageHandler<MSG>) messageHandlers.get(toSend.getClass());

            ServerLevel ws = (ServerLevel) world;
            FriendlyByteBuf buf = PacketByteBufs.create();
            handler.encoder().accept(toSend, buf);
            ws.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).stream().filter(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64).forEach(p -> ServerPlayNetworking.send(p, handler.id(), buf));
        }
    }

    @Override
    public <MSG> void sendToNearby(Level world, Entity entity, MSG toSend) {
        sendToNearby(world, entity.blockPosition(), toSend);
    }

    @Override
    public <MSG> void sendTo(Player player, MSG toSend) {
        MessageHandler<MSG> handler = (MessageHandler<MSG>) messageHandlers.get(toSend.getClass());
        FriendlyByteBuf buf = PacketByteBufs.create();
        handler.encoder().accept(toSend, buf);
        ServerPlayNetworking.send((ServerPlayer) player, handler.id(), buf);
    }

    @Override
    public <MSG> void sendToServer(MSG toSend) {
        MessageHandler<MSG> handler = (MessageHandler<MSG>) messageHandlers.get(toSend.getClass());
        FriendlyByteBuf buf = PacketByteBufs.create();
        handler.encoder().accept(toSend, buf);
        ClientPlayNetworking.send(handler.id(), buf);
    }
}
