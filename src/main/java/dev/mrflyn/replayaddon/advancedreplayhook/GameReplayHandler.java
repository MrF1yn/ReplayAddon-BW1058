package dev.mrflyn.replayaddon.advancedreplayhook;

import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.events.gameplay.GameStateChangeEvent;
import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.guis.CustomReplaySessionSettings;
import dev.mrflyn.replayaddon.versionutils.SupportPAPI;
import dev.mrflyn.replayaddon.versionutils.Util;
import dev.mrflyn.replayaddon.guis.PlayerReplaysGUI;
import me.jumper251.replay.api.ReplayAPI;
import me.jumper251.replay.replaysystem.Replay;
import me.jumper251.replay.utils.ReplayManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class GameReplayHandler {
    public static HashMap<UUID, List<GameReplayCache>> replayCachePerPlayer = new HashMap<>();
    public static HashMap<String, GameReplayCache> replayCacheID = new HashMap<>();
    public static HashMap<IArena,GameReplayCache> ongoingRecordings = new HashMap<>();
    public static HashMap<Player, StartQueue> startQueue = new HashMap<>();
    public static HashMap<Player , CustomReplaySessionSettings> playingReplays = new HashMap<>();


    public static void startRecording(GameStateChangeEvent e){
        if(e.getNewState() != GameState.playing)return;
        Util.debug("start called!");
        List<PlayerInfo> playerInfos = new ArrayList<>();
        for (Player p : e.getArena().getPlayers()){
            playerInfos.add(new PlayerInfo(
                            p.getName(),
                            p.getUniqueId(),
                            SupportPAPI.getSupportPAPI().replace(p, Util.getConfigValue("recording-settings.player-tabPrefix")),
                            SupportPAPI.getSupportPAPI().replace(p, Util.getConfigValue("recording-settings.player-tabSuffix")),
                            SupportPAPI.getSupportPAPI().replace(p, Util.getConfigValue("recording-settings.player-tagPrefix")),
                            SupportPAPI.getSupportPAPI().replace(p, Util.getConfigValue("recording-settings.player-tagSuffix"))
                    )
            );
        }
        String replayName = Util.generateID(e.getArena().getDisplayName(),e.getArena().getGroup(),Util.getDate());
        //TODO: Start Recording
        ReplayAPI.getInstance().recordReplay(replayName,
                Bukkit.getConsoleSender(), e.getArena().getPlayers());
        GameReplayCache cache = new GameReplayCache(
                playerInfos,
                e.getArena().getDisplayName(),
                e.getArena().getGroup(),
                Util.getDate(),
                replayName
                );
        ongoingRecordings.put(e.getArena(), cache);
        Util.debug("started!");
        //debug msg
    }

    public static void stopRecording(GameStateChangeEvent e, boolean updateCache){
        Util.debug("stop called!");
        if(!ongoingRecordings.containsKey(e.getArena()))return;
        //TODO: RECORD DEATHS DONE
        //debug msg
        GameReplayCache cache = ongoingRecordings.get(e.getArena());
        List<String> spectators = new ArrayList<>();
        for (Player p : e.getArena().getSpectators()){
            spectators.add(p.getName());
        }
        cache.setSpectators(spectators);
        Replay replay = ReplayManager.activeReplays.get(cache.getReplayName());
        cache.setTotalDuration(replay.getRecorder().getCurrentTick()/20);
        ongoingRecordings.remove(e.getArena());
        ReplayAPI.getInstance().stopReplay(cache.getReplayName(), true);
        //TODO: SAVE CACHE,update local cache if sharedMode
        Bukkit.getScheduler().runTaskAsynchronously(ReplayAddonMain.plugin, ()->{
            ReplayAddonMain.plugin.db.saveGameReplayCache(cache);
            if (!updateCache)return;
            for(UUID uuid : cache.getPlayersWithUUIDs()){
                if(replayCachePerPlayer.containsKey(uuid)){
                    replayCachePerPlayer.get(uuid).add(cache);
                }else {
                    replayCachePerPlayer.put(uuid, new ArrayList<>(Collections.singletonList(cache)));
                }
                replayCacheID.put(cache.getReplayName(),cache);
                Bukkit.getScheduler().runTask(ReplayAddonMain.plugin, ()->{
                    Player p = Bukkit.getPlayer(uuid);
                    if(PlayerReplaysGUI.playerReplayGuiCache.containsKey(p)){
                        PlayerReplaysGUI gui = PlayerReplaysGUI.playerReplayGuiCache.get(p);
                        gui.setReplays(replayCachePerPlayer.get(uuid));
                        gui.updateGUI();
                    }

                });
            }
        });
        Util.debug("stopped!");
    }

    public static void playRecording(Player p, String replayID){
        ReplayAddonMain.plugin.getManager().playRecording(p ,replayID);
    }





}
