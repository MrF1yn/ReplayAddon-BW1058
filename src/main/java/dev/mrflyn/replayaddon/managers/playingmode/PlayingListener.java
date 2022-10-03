package dev.mrflyn.replayaddon.managers.playingmode;


import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayCache;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayHandler;
import dev.mrflyn.replayaddon.advancedreplayhook.StartQueue;
import dev.mrflyn.replayaddon.guis.Button;
import dev.mrflyn.replayaddon.guis.GuiHandler;
import dev.mrflyn.replayaddon.guis.replaysessionguis.BookMarksGUI;
import dev.mrflyn.replayaddon.guis.replaysessionguis.MoreSettingsGUI;
import dev.mrflyn.replayaddon.versionutils.Util;
import me.jumper251.replay.api.ReplaySessionFinishEvent;
import me.jumper251.replay.api.ReplaySessionStartEvent;
import me.jumper251.replay.replaysystem.Replay;
import me.jumper251.replay.replaysystem.replaying.ReplayHelper;
import me.jumper251.replay.replaysystem.replaying.Replayer;
import me.jumper251.replay.utils.ReplayManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

import static dev.mrflyn.replayaddon.ReplayAddonMain.plugin;

public class PlayingListener implements Listener {

        @EventHandler
        public void onReplaySessionStart(ReplaySessionStartEvent e){
                Player p = e.getPlayer();
                if(!GameReplayHandler.playingReplays.containsKey(p))return;
                if(!e.getReplayer().getReplay().getId().equals(GameReplayHandler.playingReplays.get(p).getReplayID()))return;
                e.willHandleButtons(true);
                Util.debug("custom replay");
                for(Button b : plugin.initializedButtons.values()){
                        if (!b.type.equals("HB_PLAY"))
                                p.getInventory().setItem(b.slot, b.itemStack);
                }
                new BookMarksGUI(GameReplayHandler.playingReplays.get(p));
                if(GameReplayHandler.startQueue.containsKey(p)){
                        e.getReplayer().setPaused(true);
                        StartQueue task = GameReplayHandler.startQueue.get(p);
                        if(task.startTime!=null)
                                Util.setTime(e.getReplayer(), task.startTime-3);
                        if(task.location!=null&&task.location.size()==3) {
                                Location loc = new Location(p.getWorld(), task.location.get(0), task.location.get(1), task.location.get(2));
                                Bukkit.getScheduler().runTaskLater(ReplayAddonMain.plugin, ()->{
                                        p.setFlying(true);
                                        p.teleport(Util.lookAt(loc.clone().add(3,1,3), loc));
                                }, 2L);
                        }
                        GameReplayHandler.startQueue.remove(p);
                        e.getReplayer().setPaused(false);
                }
        }

        @EventHandler
        public void onInteract(PlayerInteractEvent e){
                Player p = e.getPlayer();
                if(!GameReplayHandler.playingReplays.containsKey(p))return;
                if (!ReplayHelper.replaySessions.containsKey(p.getName()))return;
                if(e.getItem() == null)return;
                NBTItem item = new NBTItem(e.getItem());
                if(!item.hasKey("replay-addon-bw1058"))return;
                e.setCancelled(true);
                Replayer replayer = ReplayHelper.replaySessions.get(p.getName());
                switch (item.getString("replay-addon-bw1058")){
                        case "HB_TP_PLAYER":
                                ReplayHelper.createTeleporter(p, replayer);
                                break;
                        case "HB_MORE_SETT":
                                p.openInventory(MoreSettingsGUI.INSTANCE.getCachedInventory());
                                break;
                        case "HB_PLAY":
                                if(!replayer.isPaused())return;
                                replayer.setPaused(false);
                                ReplayHelper.sendTitle(p, " ", "§a➤", 20);
                                Button pause = plugin.initializedButtons.get("HB_PAUSE");
                                p.getInventory().setItem(pause.slot, pause.itemStack);
                                break;
                        case "HB_PAUSE":
                                if(replayer.isPaused())return;
                                replayer.setPaused(true);
                                ReplayHelper.sendTitle(p, " ", "§c❙❙", 20);
                                Button play = plugin.initializedButtons.get("HB_PLAY");
                                p.getInventory().setItem(play.slot, play.itemStack);
                                break;
                        case "HB_DCR_SPD":
                                if (replayer.getSpeed() == 4) {
                                        replayer.setSpeed(3);
                                } else if (replayer.getSpeed() == 3) {
                                        replayer.setSpeed(2);
                                } else if (replayer.getSpeed() == 2) {
                                        replayer.setSpeed(1);
                                } else if (replayer.getSpeed() == 1) {
                                        replayer.setSpeed(0.5D);
                                } else if (replayer.getSpeed() == 0.5D) {
                                        replayer.setSpeed(0.25D);
                                }
                                break;
                        case "HB_INC_SPD":
                                if (replayer.getSpeed() < 1) {
                                        replayer.setSpeed(1);
                                }else if (replayer.getSpeed() == 1) {
                                        replayer.setSpeed(2);
                                }else if (replayer.getSpeed() == 2) {
                                        replayer.setSpeed(3);
                                }else if (replayer.getSpeed() == 3) {
                                        replayer.setSpeed(4);
                                }
                                break;
                        case "HB_10s_FWD":
                                replayer.getUtils().forward();
                                ReplayHelper.sendTitle(p, " ", "§a»»", 20);
                                break;
                        case "HB_10s_BWD":
                                replayer.getUtils().backward();
                                ReplayHelper.sendTitle(p, " ", "§c««", 20);
                                break;
                }
        }


        @EventHandler
        public void onReplaySessionEnd(ReplaySessionFinishEvent e){
                if(GameReplayHandler.playingReplays.containsKey(e.getPlayer())) {
                        GameReplayHandler.playingReplays.get(e.getPlayer()).getChatTimelineHandler().terminate();
                        GameReplayHandler.playingReplays.remove(e.getPlayer());
                }
                BookMarksGUI.replayIDBookmarks.remove(e.getReplay().getId());
                Bukkit.getScheduler().runTask(plugin, ()->{
                        e.getPlayer().setFlySpeed(0.1F);
                });

        }


        @EventHandler
        public void onJoin(PlayerJoinEvent e){
                Player p = e.getPlayer();
                UUID uuid = p.getUniqueId();
                String name = p.getName();
//                String info = p.getName()+":"+p.getUniqueId().toString();
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        String lang = plugin.db.getPlayerLanguage(uuid);
                        if (lang == null) {
                                plugin.playerLang.put(uuid, plugin.allLanguages.get("en"));
                        } else {
                                plugin.playerLang.put(uuid, plugin.allLanguages.get(lang));
                        }
                        List<GameReplayCache> caches = plugin.db.getGameReplayCaches(name);
                        GameReplayHandler.replayCachePerPlayer.put(uuid, caches);
                        for (GameReplayCache cache : caches) {
                                GameReplayHandler.replayCacheID.put(cache.getReplayName(), cache);
                        }
                        Util.debug("loaded on join.");
                        Bukkit.getScheduler().runTask(plugin, () -> {
                                GuiHandler.onJoin(p);
                        });

                });
        }

        @EventHandler
        public void onLeave(PlayerQuitEvent e){
                if(GameReplayHandler.playingReplays.containsKey(e.getPlayer())) {
                        GameReplayHandler.playingReplays.get(e.getPlayer()).getChatTimelineHandler().terminate();
                        GameReplayHandler.playingReplays.remove(e.getPlayer());
                }
                for(GameReplayCache cache : GameReplayHandler.replayCachePerPlayer.get(e.getPlayer().getUniqueId())){
                        GameReplayHandler.replayCacheID.remove(cache.getReplayName());
                }
                GameReplayHandler.replayCachePerPlayer.remove(e.getPlayer().getUniqueId());
                GuiHandler.onLeave(e.getPlayer());
                GameReplayHandler.startQueue.remove(e.getPlayer());
                e.getPlayer().setFlySpeed(0.1F);
                Player p = e.getPlayer();
                UUID uuid = p.getUniqueId();
        }


}
