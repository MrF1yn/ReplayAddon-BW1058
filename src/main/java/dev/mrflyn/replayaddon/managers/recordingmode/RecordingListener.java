package dev.mrflyn.replayaddon.managers.recordingmode;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayCache;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayHandler;
import dev.mrflyn.replayaddon.versionutils.Util;
import me.jumper251.replay.replaysystem.Replay;
import me.jumper251.replay.utils.ReplayManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

import static dev.mrflyn.replayaddon.ReplayAddonMain.plugin;

public class RecordingListener implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent e){
                Player p = e.getPlayer();
                String name = p.getName();
                String language =
                        (!plugin.allLanguages.containsKey(BedWars.getAPI().getLangIso(p)))?"en":BedWars.getAPI().getLangIso(p);
                Util.debug(language);
                Util.debug(BedWars.getAPI().getLangIso(p));
                UUID uuid = p.getUniqueId();
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        String lang = plugin.db.getPlayerLanguage(uuid);
                        if (lang == null) {
                                plugin.db.createPlayerLanguage(uuid, name, language);
                                plugin.playerLang.put(uuid, plugin.allLanguages.get(language));
                        } else {
                                plugin.playerLang.put(uuid, plugin.allLanguages.get(lang));
                        }
                        Util.debug("loaded on join.");
                });
        }

        @EventHandler
        public void onLeave(PlayerQuitEvent e){
                Player p = e.getPlayer();
                String language = BedWars.getAPI().getLangIso(p);
                UUID uuid = p.getUniqueId();
                Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> {
                        plugin.db.updatePlayerLanguage(uuid, language);
                });
        }

        @EventHandler
        public void onStart(GameStateChangeEvent e){
                GameReplayHandler.startRecording(e);
                if(e.getNewState() == GameState.restarting) {
                        Util.debug("end");
                        GameReplayHandler.stopRecording(e, false);
                }

        }

        @EventHandler
        public void onDeath(PlayerKillEvent e){
                IArena arena = e.getArena();
                Player victim = e.getVictim();
                if(!GameReplayHandler.ongoingRecordings.containsKey(arena))return;
                GameReplayCache cache = GameReplayHandler.ongoingRecordings.get(arena);
                Replay replay = ReplayManager.activeReplays.get(cache.getReplayName());
                HashMap<String, List<Integer>> deathTimes = cache.getUUIDsWithDeathTimes();
                if(deathTimes.containsKey(victim.getUniqueId().toString())){
                        deathTimes.get(victim.getUniqueId().toString())
                                .add(replay.getRecorder().getCurrentTick()/20);

                }
                else {
                        deathTimes.put(victim.getUniqueId().toString(),
                                new ArrayList<>(Collections.singletonList(replay.getRecorder().getCurrentTick() / 20)));
                }
        }


}
