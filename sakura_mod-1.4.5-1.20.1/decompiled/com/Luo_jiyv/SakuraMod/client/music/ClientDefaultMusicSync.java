package com.Luo_jiyv.SakuraMod.client.music;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.Luo_jiyv.SakuraMod.music.MusicFiles;
import com.Luo_jiyv.SakuraMod.network.DefaultMusicNetwork;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkDirection;

@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT}
)
public final class ClientDefaultMusicSync {
   private static final ScheduledExecutorService IO = Executors.newSingleThreadScheduledExecutor(task -> {
      Thread thread = new Thread(task, "Sakura music download");
      thread.setDaemon(true);
      return thread;
   });
   private static final AtomicReference<ClientDefaultMusicSync.Pending> PENDING = new AtomicReference<>();
   private static ClientDefaultMusicSync.Download downloading;
   private static Connection offeredConnection;
   private static long offeredRevision;
   private static ClientDefaultMusicSync.ActivePack activePack;
   private static boolean reloadPending;

   public static void offer(DefaultMusicNetwork.Offer offer, Connection connection) {
      if (isCurrent(connection) && offer.revision() > 0L && (offeredConnection != connection || offer.revision() > offeredRevision) && validOffer(offer)) {
         offeredConnection = connection;
         offeredRevision = offer.revision();
         if (offer.tracks().isEmpty()) {
            activePack = null;
            reloadPending = true;
            IO.execute(() -> abort(null));
         } else {
            Set<Path> inUse = new HashSet<>(syncedFiles().values());
            IO.execute(() -> {
               abort(null);
               downloading = new ClientDefaultMusicSync.Download(connection, offer);

               try {
                  Files.createDirectories(MusicFiles.cache());
                  pruneCache(inUse);
                  downloading.nextTrack();
               } catch (RuntimeException | IOException var4) {
                  abort(var4);
               }
            });
         }
      }
   }

   public static void chunk(DefaultMusicNetwork.Chunk chunk, Connection connection) {
      if (isCurrent(connection) && offeredConnection == connection && offeredRevision == chunk.revision()) {
         ClientDefaultMusicSync.Pending expected = PENDING.get();
         if (expected != null
            && expected.connection() == connection
            && expected.revision() == chunk.revision()
            && expected.hash().equals(chunk.hash())
            && expected.offset() == chunk.offset()
            && PENDING.compareAndSet(expected, null)) {
            IO.execute(() -> {
               if (downloading != null && downloading.connection == connection && downloading.revision == chunk.revision()) {
                  try {
                     downloading.accept(chunk);
                  } catch (RuntimeException | IOException var3) {
                     abort(var3);
                  }
               }
            });
         }
      }
   }

   private static boolean validOffer(DefaultMusicNetwork.Offer offer) {
      Set<String> ids = new HashSet<>();
      long total = 0L;

      for (DefaultMusicNetwork.Track track : offer.tracks()) {
         if (!MusicFiles.DEFAULT_IDS.contains(track.id())
            || !ids.add(track.id())
            || !MusicFiles.validHash(track.hash())
            || track.size() <= 0
            || track.size() > 33554432) {
            return false;
         }

         total += track.size();
      }

      return total <= 536870912L;
   }

   static Map<String, Path> syncedFiles() {
      return activePack != null && isCurrent(activePack.connection()) ? activePack.files() : Map.of();
   }

   private static boolean isCurrent(Connection connection) {
      ClientPacketListener listener = Minecraft.m_91087_().m_91403_();
      return listener != null && listener.m_104910_() == connection && connection.m_129536_();
   }

   @SubscribeEvent
   public static void logout(LoggingOut event) {
      Connection connection = event.getConnection();
      if (offeredConnection == connection) {
         offeredConnection = null;
         offeredRevision = 0L;
         IO.execute(() -> {
            if (downloading != null && downloading.connection == connection) {
               abort(null);
            }
         });
      }

      if (activePack != null && activePack.connection() == connection) {
         activePack = null;
         reloadPending = true;
      }
   }

   @SubscribeEvent
   public static void tick(ClientTickEvent event) {
      if (event.phase == Phase.END && reloadPending) {
         Minecraft mc = Minecraft.m_91087_();
         if (mc.m_91265_() == null) {
            reloadPending = false;
            MusicAudioReload.reload().exceptionally(error -> {
               SakuraMod.LOGGER.warn("[Music] Could not refresh Sakura audio", error);
               return null;
            });
         }
      }
   }

   private static void abort(Exception error) {
      PENDING.set(null);
      ClientDefaultMusicSync.Download failed = downloading;
      downloading = null;
      if (failed != null && failed.partial != null) {
         try {
            Files.deleteIfExists(failed.partial);
         } catch (IOException var3) {
            SakuraMod.LOGGER.warn("[Music] Could not clear the partial download {}", failed.partial, var3);
         }
      }

      if (error != null) {
         SakuraMod.LOGGER.warn("[Music] Default soundtrack sync failed; keeping local music", error);
      }
   }

   private static void pruneCache(Set<Path> inUse) throws IOException {
      ArrayList<ClientDefaultMusicSync.CacheEntry> entries = new ArrayList<>();
      long total = 0L;

      try (DirectoryStream<Path> files = Files.newDirectoryStream(MusicFiles.cache())) {
         for (Path file : files) {
            if (Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
               String name = file.getFileName().toString();
               if (name.startsWith("sakura-") && name.endsWith(".part")) {
                  Files.delete(file);
               } else if (name.endsWith(".ogg") && MusicFiles.validHash(name.substring(0, name.length() - 4))) {
                  long size = Files.size(file);
                  total += size;
                  entries.add(new ClientDefaultMusicSync.CacheEntry(file, size, Files.getLastModifiedTime(file).toMillis()));
               }
            }
         }
      }

      entries.sort(Comparator.comparingLong(ClientDefaultMusicSync.CacheEntry::modified));

      for (ClientDefaultMusicSync.CacheEntry entry : entries) {
         if (total <= 536870912L) {
            break;
         }

         if (!inUse.contains(entry.file())) {
            Files.delete(entry.file());
            total -= entry.size();
         }
      }
   }

   private ClientDefaultMusicSync() {
   }

   static {
      IO.scheduleWithFixedDelay(() -> {
         if (downloading != null && (!downloading.connection.m_129536_() || Util.m_137550_() - downloading.lastProgress > 30000L)) {
            abort(new IOException("Music download timed out or disconnected"));
         }
      }, 5L, 5L, TimeUnit.SECONDS);
   }

   private record ActivePack(Connection connection, Map<String, Path> files) {
   }

   private record CacheEntry(Path file, long size, long modified) {
   }

   private static final class Download {
      private final Connection connection;
      private final long revision;
      private final List<DefaultMusicNetwork.Track> tracks;
      private final Map<String, Path> files = new HashMap<>();
      private int index;
      private int offset;
      private MessageDigest digest;
      private Path partial;
      private long lastProgress;
      private int downloaded;

      private Download(Connection connection, DefaultMusicNetwork.Offer offer) {
         this.connection = connection;
         this.revision = offer.revision();
         this.tracks = offer.tracks();
      }

      private void nextTrack() throws IOException {
         while (this.index < this.tracks.size()) {
            DefaultMusicNetwork.Track track = this.tracks.get(this.index);
            Path local = MusicFiles.defaultAudio(track.id());
            Path cached = cacheFile(track);
            Path existing = matches(local, track) ? local : (matches(cached, track) ? cached : null);
            if (existing == null) {
               this.partial = Files.createTempFile(MusicFiles.cache(), "sakura-", ".part");
               this.digest = MusicFiles.digest();
               this.offset = 0;
               this.request(track);
               return;
            }

            this.files.put(track.id(), existing);
            this.index++;
         }

         Map<String, Path> finished = Map.copyOf(this.files);
         ClientDefaultMusicSync.downloading = null;
         Minecraft.m_91087_()
            .execute(
               () -> {
                  if (ClientDefaultMusicSync.isCurrent(this.connection)
                     && ClientDefaultMusicSync.offeredConnection == this.connection
                     && ClientDefaultMusicSync.offeredRevision == this.revision) {
                     ClientDefaultMusicSync.activePack = new ClientDefaultMusicSync.ActivePack(this.connection, finished);
                     ClientDefaultMusicSync.reloadPending = true;
                     SakuraMod.LOGGER.info("[Music] Synced {} default tracks ({} downloaded)", finished.size(), this.downloaded);
                  }
               }
            );
      }

      private void request(DefaultMusicNetwork.Track track) throws IOException {
         if (!this.connection.m_129536_()) {
            throw new IOException("Disconnected during music download");
         } else {
            this.lastProgress = Util.m_137550_();
            ClientDefaultMusicSync.PENDING.set(new ClientDefaultMusicSync.Pending(this.connection, this.revision, track.hash(), this.offset));
            DefaultMusicNetwork.CHANNEL
               .sendTo(new DefaultMusicNetwork.Request(this.revision, track.id(), track.hash(), this.offset), this.connection, NetworkDirection.PLAY_TO_SERVER);
         }
      }

      private void accept(DefaultMusicNetwork.Chunk chunk) throws IOException {
         DefaultMusicNetwork.Track track = this.tracks.get(this.index);
         if (track.hash().equals(chunk.hash()) && this.offset == chunk.offset()) {
            int expected = Math.min(262144, track.size() - this.offset);
            if (chunk.data().length != expected) {
               throw new IOException("Incomplete data for " + track.id());
            } else {
               Files.write(this.partial, chunk.data(), StandardOpenOption.APPEND);
               this.digest.update(chunk.data());
               this.offset += expected;
               if (this.offset < track.size()) {
                  this.request(track);
               } else if (!track.hash().equals(HexFormat.of().formatHex(this.digest.digest()))) {
                  throw new IOException("Checksum mismatch for " + track.id());
               } else {
                  Path cached = cacheFile(track);
                  Files.move(this.partial, cached, StandardCopyOption.REPLACE_EXISTING);
                  this.partial = null;
                  this.files.put(track.id(), cached);
                  this.downloaded++;
                  this.index++;
                  this.nextTrack();
               }
            }
         }
      }

      private static Path cacheFile(DefaultMusicNetwork.Track track) {
         return MusicFiles.cache().resolve(track.hash() + ".ogg");
      }

      private static boolean matches(Path file, DefaultMusicNetwork.Track track) throws IOException {
         return Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS) && Files.size(file) == track.size() && MusicFiles.hash(file).equals(track.hash());
      }
   }

   private record Pending(Connection connection, long revision, String hash, int offset) {
   }
}
