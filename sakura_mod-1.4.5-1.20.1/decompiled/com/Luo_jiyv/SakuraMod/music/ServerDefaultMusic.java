package com.Luo_jiyv.SakuraMod.music;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.Luo_jiyv.SakuraMod.network.DefaultMusicNetwork;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkDirection;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class ServerDefaultMusic {
   private static final Set<ServerDefaultMusic.ReadKey> READING = ConcurrentHashMap.newKeySet();
   private static final Map<Connection, Map<String, Integer>> OFFSETS = new HashMap<>();
   @Nullable
   private static MinecraftServer serving;
   private static ServerDefaultMusic.Library library;
   private static CompletableFuture<Integer> scanning;

   @SubscribeEvent
   public static void start(ServerStartedEvent event) {
      serving = event.getServer();
      refresh(serving, false);
   }

   @SubscribeEvent
   public static void stop(ServerStoppedEvent event) {
      if (serving == event.getServer()) {
         serving = null;
         library = null;
         scanning = null;
         OFFSETS.clear();
      }
   }

   @SubscribeEvent
   public static void login(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player && serving == player.m_20194_() && library != null && !library.files().isEmpty()) {
         offer(player, false);
      }
   }

   public static CompletableFuture<Integer> reload(MinecraftServer server) {
      return refresh(server, true);
   }

   private static CompletableFuture<Integer> refresh(MinecraftServer server, boolean manual) {
      if (serving != server) {
         return CompletableFuture.failedFuture(new IOException("Server is stopping"));
      } else if (scanning != null) {
         return manual ? scanning.thenApply(count -> {
            server.m_6846_().m_11314_().stream().filter(p -> p.f_8906_.f_9742_.m_129531_()).forEach(p -> offer(p, true));
            return (Integer)count;
         }) : scanning;
      } else {
         CompletableFuture<Integer> result = new CompletableFuture<>();
         scanning = result;
         CompletableFuture.supplyAsync(ServerDefaultMusic::scan, Util.m_183992_()).whenCompleteAsync((files, error) -> {
            if (serving != server) {
               result.completeExceptionally(new IOException("Server is stopping"));
            } else {
               scanning = null;
               if (error != null) {
                  SakuraMod.LOGGER.warn("[Music] Could not index default music in {}", MusicFiles.defaults(), error);
                  result.completeExceptionally(error);
               } else {
                  boolean hadTracks = library != null && !library.files().isEmpty();
                  library = new ServerDefaultMusic.Library(library == null ? 1L : library.revision() + 1L, (Map<String, ServerDefaultMusic.Audio>)files);
                  OFFSETS.clear();

                  for (ServerPlayer player : server.m_6846_().m_11314_()) {
                     if (hadTracks || !files.isEmpty() || manual && player.f_8906_.f_9742_.m_129531_()) {
                        offer(player, manual);
                     }
                  }

                  result.complete(files.size());
               }
            }
         }, server);
         return result;
      }
   }

   private static void offer(ServerPlayer player, boolean manual) {
      Connection connection = player.f_8906_.f_9742_;
      boolean local = connection.m_129531_();
      if ((manual || !local) && connection.m_129536_() && DefaultMusicNetwork.CHANNEL.isRemotePresent(connection)) {
         List<DefaultMusicNetwork.Track> tracks = local ? List.of() : library.files().values().stream().map(ServerDefaultMusic.Audio::track).toList();
         if (!tracks.isEmpty()) {
            OFFSETS.put(connection, new HashMap<>());
         }

         DefaultMusicNetwork.CHANNEL.sendTo(new DefaultMusicNetwork.Offer(library.revision(), tracks), connection, NetworkDirection.PLAY_TO_CLIENT);
      }
   }

   @SubscribeEvent
   public static void logout(PlayerLoggedOutEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         OFFSETS.remove(player.f_8906_.f_9742_);
      }
   }

   public static void request(DefaultMusicNetwork.Request request, @Nullable ServerPlayer player) {
      if (player != null && library != null && serving == player.m_20194_() && library.revision() == request.revision()) {
         ServerDefaultMusic.Audio audio = library.files().get(request.id());
         if (audio != null
            && audio.track().hash().equals(request.hash())
            && request.offset() >= 0
            && request.offset() < audio.track().size()
            && request.offset() % 262144 == 0) {
            Connection connection = player.f_8906_.f_9742_;
            ServerDefaultMusic.ReadKey read = new ServerDefaultMusic.ReadKey(connection, request.revision());
            Map<String, Integer> offsets = OFFSETS.get(connection);
            if (offsets != null && offsets.getOrDefault(request.id(), 0) == request.offset() && READING.add(read)) {
               offsets.put(request.id(), request.offset() + Math.min(262144, audio.track().size() - request.offset()));
               Util.m_183992_()
                  .execute(
                     () -> {
                        byte[] bytes;
                        try {
                           bytes = connection.m_129536_() ? readChunk(audio, request.offset()) : new byte[0];
                        } catch (IOException var9) {
                           SakuraMod.LOGGER.warn("[Music] Could not read default track {}", audio.track().id(), var9);
                           bytes = new byte[0];
                        } finally {
                           READING.remove(read);
                        }

                        if (connection.m_129536_()) {
                           DefaultMusicNetwork.CHANNEL
                              .sendTo(
                                 new DefaultMusicNetwork.Chunk(request.revision(), request.hash(), request.offset(), bytes),
                                 connection,
                                 NetworkDirection.PLAY_TO_CLIENT
                              );
                        }
                     }
                  );
            }
         }
      }
   }

   private static byte[] readChunk(ServerDefaultMusic.Audio audio, int offset) throws IOException {
      int count = Math.min(262144, audio.track().size() - offset);

      byte[] var5;
      try (FileChannel file = FileChannel.open(audio.file(), StandardOpenOption.READ)) {
         if (file.size() != audio.track().size()) {
            throw new IOException("Default track changed; use /sakura_music reload server");
         }

         ByteBuffer buffer = ByteBuffer.allocate(count);
         file.position((long)offset);

         while (buffer.hasRemaining()) {
            if (file.read(buffer) < 0) {
               throw new EOFException("Default track was truncated");
            }
         }

         var5 = buffer.array();
      }

      return var5;
   }

   private static Map<String, ServerDefaultMusic.Audio> scan() {
      Map<String, ServerDefaultMusic.Audio> result = new HashMap<>();

      try {
         MusicFiles.prepareDefaults();
         Path root = MusicFiles.defaults().toRealPath();
         long total = 0L;

         for (String id : MusicFiles.DEFAULT_IDS.stream().sorted().toList()) {
            Path candidate = MusicFiles.defaultAudio(id);
            if (Files.isRegularFile(candidate)) {
               Path file = candidate.toRealPath();
               long size = Files.size(file);
               if (file.startsWith(root) && size > 0L && size <= 33554432L && total + size <= 536870912L && result.size() < 128) {
                  result.put(id, new ServerDefaultMusic.Audio(new DefaultMusicNetwork.Track(id, MusicFiles.hash(file), (int)size), file));
                  total += size;
               }
            }
         }
      } catch (IOException var10) {
         throw new CompletionException(var10);
      }

      if (!result.isEmpty()) {
         SakuraMod.LOGGER.info("[Music] Serving {} default tracks", result.size());
      }

      return Map.copyOf(result);
   }

   private ServerDefaultMusic() {
   }

   private record Audio(DefaultMusicNetwork.Track track, Path file) {
   }

   private record Library(long revision, Map<String, ServerDefaultMusic.Audio> files) {
   }

   private record ReadKey(Connection connection, long revision) {
   }
}
