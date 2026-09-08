package com.grim3212.assorted.lib.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Carries an arbitrary message object as a vanilla {@link CustomPacketPayload}.
 * <p>
 * 26.x removed the ability to register a raw encoder/decoder pair against a channel: every packet
 * is now a {@code CustomPacketPayload} with a {@link StreamCodec} and a typed id. Rather than
 * force every downstream mod to make its messages implement {@code CustomPacketPayload}, this
 * wraps the message and builds the codec from the {@code BiConsumer}/{@code Function} pair that
 * {@code INetworkHelper.MessageHandler} already carries, so that interface keeps its shape.
 * <p>
 * Both loaders use this same wrapper, so the wire format stays identical across them.
 *
 * @param type    The payload type this message was registered under.
 * @param message The wrapped message.
 */
public record LibPayload<MSG>(CustomPacketPayload.Type<LibPayload<MSG>> type, MSG message) implements CustomPacketPayload {

    public static <MSG> CustomPacketPayload.Type<LibPayload<MSG>> type(Identifier id) {
        return new CustomPacketPayload.Type<>(id);
    }

    /**
     * Builds the stream codec for a message type from its existing encoder and decoder.
     * <p>
     * {@link RegistryFriendlyByteBuf} extends {@link FriendlyByteBuf}, so encoders and decoders
     * written against the plain buffer keep working while gaining registry access.
     */
    public static <MSG> StreamCodec<RegistryFriendlyByteBuf, LibPayload<MSG>> codec(
            CustomPacketPayload.Type<LibPayload<MSG>> type,
            BiConsumer<MSG, FriendlyByteBuf> encoder,
            Function<FriendlyByteBuf, MSG> decoder) {
        return StreamCodec.of(
                (buf, payload) -> encoder.accept(payload.message(), buf),
                buf -> new LibPayload<>(type, decoder.apply(buf))
        );
    }
}
