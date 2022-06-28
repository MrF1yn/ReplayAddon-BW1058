package dev.mrflyn.replayaddon.managers.proxymode.proxyplayingmode;



import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayHandler;
import dev.mrflyn.replayaddon.advancedreplayhook.ProxyData;
import dev.mrflyn.replayaddon.advancedreplayhook.StartQueue;
import dev.mrflyn.replayaddon.guis.Button;
import dev.mrflyn.replayaddon.guis.GuiHandler;
import dev.mrflyn.replayaddon.guis.replaysessionguis.BookMarksGUI;
import dev.mrflyn.replayaddon.guis.replaysessionguis.MoreSettingsGUI;
import dev.mrflyn.replayaddon.managers.proxymode.ProxyModeManager;
import dev.mrflyn.replayaddon.versionutils.Util;
import me.jumper251.replay.api.ReplaySessionFinishEvent;
import me.jumper251.replay.api.ReplaySessionStartEvent;
import me.jumper251.replay.replaysystem.replaying.ReplayHelper;
import me.jumper251.replay.replaysystem.replaying.Replayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.*;
import static dev.mrflyn.replayaddon.ReplayAddonMain.plugin;

public class ProxyPlayingListener implements Listener {

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
                Player p = e.getPlayer();
                if(GameReplayHandler.playingReplays.containsKey(e.getPlayer())) {
                        GameReplayHandler.playingReplays.get(e.getPlayer()).getChatTimelineHandler().terminate();
                        GameReplayHandler.playingReplays.remove(e.getPlayer());
                }
                BookMarksGUI.replayIDBookmarks.remove(e.getReplay().getId());
                Bukkit.getScheduler().runTask(plugin, ()->{
                        e.getPlayer().setFlySpeed(0.1F);
                });
                if(!ProxyModeManager.proxyDataCache.containsKey(p.getUniqueId()))return;
                ProxyData data = ProxyModeManager.proxyDataCache.get(p.getUniqueId());
                try {
                        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(byteArray);
                        out.writeUTF("Connect");
                        out.writeUTF(data.getLobbyName());
                        p.sendPluginMessage(plugin, "BungeeCord", byteArray.toByteArray());
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                // if lobby server is unreachable
                                if (p==null)return;
                                if (p.isOnline()) {
                                        p.kickPlayer(ChatColor.RED+"You are not allowed here!");
                                }
                        }, 30L);
                }catch (Exception ex){
                        ex.printStackTrace();
                }

        }


        @EventHandler
        public void onJoin(PlayerJoinEvent e){
                Player p = e.getPlayer();
                UUID uuid = p.getUniqueId();
                boolean hasPerm = p.hasPermission("replayAddon.admin");
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        String lang = plugin.db.getPlayerLanguage(uuid);
                        if (lang == null) {
                                plugin.playerLang.put(uuid, plugin.allLanguages.get("en"));
                        } else {
                                plugin.playerLang.put(uuid, plugin.allLanguages.get(lang));
                        }
                        ProxyData data = plugin.db.getProxyData(uuid);
                        if (data==null&&!hasPerm){
                                Bukkit.getScheduler().runTask(plugin, ()->{
                                        p.kickPlayer(ChatColor.RED+"You are not authorized to enter this server!");
                                });
                                return;
                        }
                        if(data==null)return;
                        ProxyModeManager.proxyDataCache.put(uuid,data);
                        plugin.db.deleteProxyData(data);
                        StringBuilder command = new StringBuilder("rp view "+data.getReplayID());
                        if (data.getStartTime()!=null){
                                command.append(" ").append(data.getStartTime());
                        }
                        if (data.getX()!=null&&data.getY()!=null&&data.getZ()!=null){
                                command.append(" ").append(data.getX()).append(":").append(data.getY()).append(":").append(data.getZ());
                        }
                        Bukkit.getScheduler().runTaskLater(plugin, ()->{
                                p.performCommand(command.toString());
                        },20L);
                        Util.debug("loaded on join.");
                });
        }

        @EventHandler
        public void onLeave(PlayerQuitEvent e){
                if(GameReplayHandler.playingReplays.containsKey(e.getPlayer())) {
                        GameReplayHandler.playingReplays.get(e.getPlayer()).getChatTimelineHandler().terminate();
                        GameReplayHandler.playingReplays.remove(e.getPlayer());
                }
//                for(GameReplayCache cache : GameReplayHandler.replayCachePerPlayer.get(e.getPlayer().getUniqueId())){
//                        GameReplayHandler.replayCacheID.remove(cache.getReplayName());
//                }
                GameReplayHandler.replayCachePerPlayer.remove(e.getPlayer().getUniqueId());
                ProxyModeManager.proxyDataCache.remove(e.getPlayer().getUniqueId());
                GuiHandler.onLeave(e.getPlayer());
                GameReplayHandler.startQueue.remove(e.getPlayer());
                e.getPlayer().setFlySpeed(0.1F);
        }


}
