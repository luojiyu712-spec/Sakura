package com.Luo_jiyv.SakuraMod.client.music;

import java.util.List;
import java.util.stream.Stream;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class MusicTracks {
   public static final MusicTrack SERENE_NIGHT = new MusicTrack("serene_night", "Serene Night", MusicTrack.Tag.NIGHT);
   public static final MusicTrack RIMMAS = new MusicTrack("rimmas", "Rimmas", MusicTrack.Tag.COLD);
   public static final MusicTrack ANOMALY = new MusicTrack("a_twisted_path", "A Twisted Path", MusicTrack.Tag.ADVANCEMENT);
   public static final MusicTrack BLOOD_RAIN = new MusicTrack("blood_rain_falling", "Blood Rain Falling", MusicTrack.Tag.WEATHER);
   public static final MusicTrack MIST = new MusicTrack("have_they_come_for_us", "Have They Come For Us", MusicTrack.Tag.WEATHER);
   public static final List<MusicTrack> ALL = List.of(
      SERENE_NIGHT,
      RIMMAS,
      new MusicTrack("a_place_of_our_own", "A Place of Our Own", MusicTrack.Tag.NORMAL),
      new MusicTrack("alive", "Alive", MusicTrack.Tag.NORMAL),
      new MusicTrack("ambition", "Ambition", MusicTrack.Tag.NORMAL),
      new MusicTrack("back_in_the_saddle", "Back in the Saddle", MusicTrack.Tag.NORMAL),
      new MusicTrack("back_to_work", "Back to Work", MusicTrack.Tag.NORMAL),
      new MusicTrack("blue", "Blue", MusicTrack.Tag.NORMAL),
      new MusicTrack("bright_side_of_life", "Bright Side of Life", MusicTrack.Tag.NORMAL),
      new MusicTrack("carry_on", "Carry On", MusicTrack.Tag.NORMAL),
      new MusicTrack("cruel_world", "Cruel World", MusicTrack.Tag.NORMAL),
      new MusicTrack("everythings_going_to_be_okay", "Everythings Going to Be Okay", MusicTrack.Tag.NORMAL),
      new MusicTrack("fair_glade", "Fair Glade", MusicTrack.Tag.NORMAL),
      new MusicTrack("far_away", "Far Away", MusicTrack.Tag.NORMAL),
      new MusicTrack("hold_on", "Hold On", MusicTrack.Tag.NORMAL),
      new MusicTrack("honor", "Honor", MusicTrack.Tag.NORMAL),
      new MusicTrack("illusion", "Illusion", MusicTrack.Tag.NORMAL),
      new MusicTrack("lone_hunter", "Lone Hunter", MusicTrack.Tag.NORMAL),
      new MusicTrack("long_way_home", "Long Way Home", MusicTrack.Tag.NORMAL),
      new MusicTrack("lovely_day", "Lovely Day", MusicTrack.Tag.NORMAL),
      new MusicTrack("move", "Move", MusicTrack.Tag.NORMAL),
      new MusicTrack("muffalo", "Muffalo", MusicTrack.Tag.NORMAL),
      new MusicTrack("my_friend", "My Friend", MusicTrack.Tag.NORMAL),
      new MusicTrack("never_forgotten", "Never Forgotten", MusicTrack.Tag.NORMAL),
      new MusicTrack("nimbostratus", "Nimbostratus", MusicTrack.Tag.NORMAL),
      new MusicTrack("odyssey", "Odyssey", MusicTrack.Tag.NORMAL),
      new MusicTrack("on_the_road", "On the Road", MusicTrack.Tag.NORMAL),
      new MusicTrack("one_can_dream_to_fly", "One Can Dream to Fly", MusicTrack.Tag.NORMAL),
      new MusicTrack("one_thousand_stars", "One Thousand Stars", MusicTrack.Tag.NORMAL),
      new MusicTrack("outlaw", "Outlaw", MusicTrack.Tag.NORMAL),
      new MusicTrack("passenger", "Passenger", MusicTrack.Tag.NORMAL),
      new MusicTrack("protect_our_home", "Protect Our Home", MusicTrack.Tag.NORMAL),
      new MusicTrack("remember_our_past", "Remember Our Past", MusicTrack.Tag.NORMAL),
      new MusicTrack("rise", "Rise", MusicTrack.Tag.NORMAL),
      new MusicTrack("rustic_ranching", "Rustic Ranching", MusicTrack.Tag.NORMAL),
      new MusicTrack("serenity", "Serenity", MusicTrack.Tag.NORMAL),
      new MusicTrack("simple_life", "Simple Life", MusicTrack.Tag.NORMAL),
      new MusicTrack("solitude", "Solitude", MusicTrack.Tag.NORMAL),
      new MusicTrack("spacekattommy", "Spacekattommy", MusicTrack.Tag.NORMAL),
      new MusicTrack("stay_strong", "Stay Strong", MusicTrack.Tag.NORMAL),
      new MusicTrack("strange_feeling", "Strange Feeling", MusicTrack.Tag.NORMAL),
      new MusicTrack("sunlight", "Sunlight", MusicTrack.Tag.NORMAL),
      new MusicTrack("the_fallen", "The Fallen", MusicTrack.Tag.NORMAL),
      new MusicTrack("tough_life", "Tough Life", MusicTrack.Tag.NORMAL),
      new MusicTrack("trust_me", "Trust Me", MusicTrack.Tag.NORMAL),
      new MusicTrack("united", "United", MusicTrack.Tag.NORMAL),
      new MusicTrack("valiant", "Valiant", MusicTrack.Tag.NORMAL),
      new MusicTrack("we_will", "We Will", MusicTrack.Tag.NORMAL),
      new MusicTrack("what_we_once_had", "What We Once Had", MusicTrack.Tag.NORMAL),
      new MusicTrack("ate_without_table", "Ate Without Table", MusicTrack.Tag.BATTLE),
      new MusicTrack("code_red", "Code Red", MusicTrack.Tag.BATTLE),
      new MusicTrack("delusion", "Delusion", MusicTrack.Tag.BATTLE),
      new MusicTrack("final_stand", "Final Stand", MusicTrack.Tag.BATTLE),
      new MusicTrack("legacy", "Legacy", MusicTrack.Tag.BATTLE),
      new MusicTrack("rimraid", "Rimraid", MusicTrack.Tag.BATTLE),
      new MusicTrack("triumph", "Triumph", MusicTrack.Tag.BATTLE),
      ANOMALY,
      BLOOD_RAIN,
      MIST
   );
   private static MusicTracks.Library library = MusicTracks.Library.withCustom(List.of());

   static void setCustomTracks(List<MusicTrack> custom) {
      library = MusicTracks.Library.withCustom(custom);
   }

   public static List<MusicTrack> all() {
      return library.all();
   }

   static List<MusicTrack> normal() {
      return library.normal();
   }

   static List<MusicTrack> battle() {
      return library.battle();
   }

   private MusicTracks() {
   }

   private record Library(List<MusicTrack> all, List<MusicTrack> normal, List<MusicTrack> battle) {
      static MusicTracks.Library withCustom(List<MusicTrack> custom) {
         List<MusicTrack> all = Stream.concat(MusicTracks.ALL.stream(), custom.stream()).toList();
         return new MusicTracks.Library(
            all,
            all.stream().filter(track -> track.tag() == MusicTrack.Tag.NORMAL).toList(),
            all.stream().filter(track -> track.tag() == MusicTrack.Tag.BATTLE).toList()
         );
      }
   }
}
