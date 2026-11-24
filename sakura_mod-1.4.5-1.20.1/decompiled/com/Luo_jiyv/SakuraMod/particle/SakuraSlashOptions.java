package com.Luo_jiyv.SakuraMod.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public record SakuraSlashOptions(int ownerId) implements ParticleOptions {
   public static final Codec<SakuraSlashOptions> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(Codec.INT.fieldOf("owner").forGetter(SakuraSlashOptions::ownerId)).apply(instance, SakuraSlashOptions::new)
   );
   public static final Deserializer<SakuraSlashOptions> DESERIALIZER = new Deserializer<SakuraSlashOptions>() {
      public SakuraSlashOptions fromCommand(ParticleType<SakuraSlashOptions> type, StringReader reader) throws CommandSyntaxException {
         if (reader.canRead() && reader.peek() == ' ') {
            int cursor = reader.getCursor();
            reader.skip();
            if (reader.canRead() && reader.peek() == '@') {
               reader.skip();
               return new SakuraSlashOptions(reader.readInt());
            } else {
               reader.setCursor(cursor);
               return new SakuraSlashOptions(-1);
            }
         } else {
            return new SakuraSlashOptions(-1);
         }
      }

      public SakuraSlashOptions fromNetwork(ParticleType<SakuraSlashOptions> type, FriendlyByteBuf buffer) {
         return new SakuraSlashOptions(buffer.readInt());
      }
   };

   public ParticleType<SakuraSlashOptions> m_6012_() {
      return (ParticleType<SakuraSlashOptions>)ModParticles.SAKURA_SLASH.get();
   }

   public void m_7711_(FriendlyByteBuf buffer) {
      buffer.writeInt(this.ownerId);
   }

   public String m_5942_() {
      return BuiltInRegistries.f_257034_.m_7981_(this.m_6012_()) + " @" + this.ownerId;
   }
}
