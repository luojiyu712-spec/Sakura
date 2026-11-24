package com.Luo_jiyv.SakuraMod.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class RandomTeleporterAsync {
   private static ExecutorService executor;

   private RandomTeleporterAsync() {
   }

   static ExecutorService get() {
      if (executor == null) {
         executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), task -> {
            Thread thread = new Thread(task, "sakura-teleporter-biomes");
            thread.setDaemon(true);
            thread.setPriority(1);
            return thread;
         });
      }

      return executor;
   }

   static void shutdown() {
      if (executor != null) {
         executor.shutdownNow();
         executor = null;
      }
   }
}
