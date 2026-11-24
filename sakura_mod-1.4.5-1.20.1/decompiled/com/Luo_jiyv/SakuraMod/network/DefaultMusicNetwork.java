package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.client.music.ClientDefaultMusicSync;
import com.Luo_jiyv.SakuraMod.music.ServerDefaultMusic;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.network.simple.SimpleChannel;

public final class DefaultMusicNetwork {
   public static final int CHUNK_SIZE = 262144;
   public static final int MAX_TRACKS = 128;
   private static final String VERSION = "1";
   public static final SimpleChannel CHANNEL = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath("sakura_mod", "default_music_sync"))
      .networkProtocolVersion(() -> "1")
      .clientAcceptedVersions(NetworkRegistry.acceptMissingOr("1"))
      .serverAcceptedVersions(NetworkRegistry.acceptMissingOr("1"))
      .simpleChannel();

   public static void register() {
      CHANNEL.messageBuilder(DefaultMusicNetwork.Offer.class, 0, NetworkDirection.PLAY_TO_CLIENT)
         .encoder((offer, buf) -> {
            buf.m_130103_(offer.revision());
            buf.m_130130_(offer.tracks().size());

            for (DefaultMusicNetwork.Track track : offer.tracks()) {
               buf.m_130072_(track.id(), 64);
               buf.m_130072_(track.hash(), 64);
               buf.m_130130_(track.size());
            }
         })
         .decoder(DefaultMusicNetwork::decodeOffer)
         .consumerMainThread((offer, context) -> ClientDefaultMusicSync.offer(offer, ((Context)context.get()).getNetworkManager()))
         .add();
      CHANNEL.messageBuilder(DefaultMusicNetwork.Request.class, 1, NetworkDirection.PLAY_TO_SERVER)
         .encoder((request, buf) -> {
            buf.m_130103_(request.revision());
            buf.m_130072_(request.id(), 64);
            buf.m_130072_(request.hash(), 64);
            buf.m_130130_(request.offset());
         })
         .decoder(buf -> new DefaultMusicNetwork.Request(buf.m_130258_(), buf.m_130136_(64), buf.m_130136_(64), buf.m_130242_()))
         .consumerMainThread((request, context) -> ServerDefaultMusic.request(request, ((Context)context.get()).getSender()))
         .add();
      CHANNEL.messageBuilder(DefaultMusicNetwork.Chunk.class, 2, NetworkDirection.PLAY_TO_CLIENT)
         .encoder((chunk, buf) -> {
            buf.m_130103_(chunk.revision());
            buf.m_130072_(chunk.hash(), 64);
            buf.m_130130_(chunk.offset());
            buf.m_130087_(chunk.data());
         })
         .decoder(buf -> new DefaultMusicNetwork.Chunk(buf.m_130258_(), buf.m_130136_(64), buf.m_130242_(), buf.m_130101_(262144)))
         .consumerMainThread((chunk, context) -> ClientDefaultMusicSync.chunk(chunk, ((Context)context.get()).getNetworkManager()))
         .add();
   }

   private static DefaultMusicNetwork.Offer decodeOffer(FriendlyByteBuf buf) {
      long revision = buf.m_130258_();
      int count = buf.m_130242_();
      if (count >= 0 && count <= 128) {
         ArrayList<DefaultMusicNetwork.Track> tracks = new ArrayList<>(count);

         for (int i = 0; i < count; i++) {
            tracks.add(new DefaultMusicNetwork.Track(buf.m_130136_(64), buf.m_130136_(64), buf.m_130242_()));
         }

         return new DefaultMusicNetwork.Offer(revision, List.copyOf(tracks));
      } else {
         throw new IllegalArgumentException("Invalid music offer size");
      }
   }

   private DefaultMusicNetwork() {
   }

   public record Chunk(long revision, String hash, int offset, byte[] data) {
   }

   public record Offer(long revision, List<DefaultMusicNetwork.Track> tracks) {
   }

   public record Request(long revision, String id, String hash, int offset) {
   }

   public record Track(String id, String hash, int size) {
   }
}
