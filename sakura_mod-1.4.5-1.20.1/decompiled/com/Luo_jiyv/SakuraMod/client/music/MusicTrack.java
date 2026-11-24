package com.Luo_jiyv.SakuraMod.client.music;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class MusicTrack {
   private final String id;
   private final String displayName;
   private final MusicTrack.Tag tag;
   private final ResourceLocation soundLocation;
   private final SoundEvent soundEvent;

   MusicTrack(String id, String displayName, MusicTrack.Tag tag) {
      this.id = id;
      this.displayName = displayName;
      this.tag = tag;
      this.soundLocation = ResourceLocation.fromNamespaceAndPath("sakura_mod", "music/" + id);
      this.soundEvent = SoundEvent.m_262824_(this.soundLocation);
   }

   public String id() {
      return this.id;
   }

   public String displayName() {
      return this.displayName;
   }

   public MusicTrack.Tag tag() {
      return this.tag;
   }

   public boolean isEventTrack() {
      return this.tag == MusicTrack.Tag.ADVANCEMENT || this.tag == MusicTrack.Tag.WEATHER;
   }

   public boolean isCustom() {
      return this.id.startsWith("custom/");
   }

   public ResourceLocation soundLocation() {
      return this.soundLocation;
   }

   public SoundEvent soundEvent() {
      return this.soundEvent;
   }

   public static enum Tag {
      NORMAL,
      NIGHT,
      COLD,
      BATTLE,
      ADVANCEMENT,
      WEATHER;
   }
}
