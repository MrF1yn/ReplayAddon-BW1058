package dev.mrflyn.replayaddon.commands;

import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayCache;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayHandler;
import dev.mrflyn.replayaddon.advancedreplayhook.ProxyData;
import dev.mrflyn.replayaddon.advancedreplayhook.StartQueue;
import dev.mrflyn.replayaddon.commands.handler.MainCommand;
import dev.mrflyn.replayaddon.commands.handler.SubCommand;
import dev.mrflyn.replayaddon.guis.PlayerReplaysGUI;
import dev.mrflyn.replayaddon.managers.proxymode.ProxyModeManager;
import dev.mrflyn.replayaddon.managers.proxymode.proxylobbymanager.ProxyLobbyManager;
import dev.mrflyn.replayaddon.versionutils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import static dev.mrflyn.replayaddon.ReplayAddonMain.plugin;

public class ViewCommand implements SubCommand {

    public ViewCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if(args.length<1){
            p.sendMessage(ChatColor.RED+"The correct format is /rp view <replayID> <startTime(optional)> <location[x:y:z](optional).");
            return true;
        }
        String replayID = args[0];
        Integer tstartTime = null;
        List<Double> loc = new ArrayList<>();
        if(args.length>1){
            try {
                tstartTime = Integer.parseInt(args[1]);
            }catch (Exception e){
                p.sendMessage(ChatColor.RED+"The start time has to be a number.");
                return true;
            }
        }if(args.length>2){
            try {
                String[] locs = args[2].split(":");
                double x = Double.parseDouble(locs[0]);
                double y = Double.parseDouble(locs[1]);
                double z = Double.parseDouble(locs[2]);
                loc.add(x);
                loc.add(y);
                loc.add(z);
            }catch (Exception e){
                p.sendMessage(ChatColor.RED+"The x:y:z has to be a number.");
                return true;
            }
        }
        Integer startTime = tstartTime;

        if(ReplayAddonMain.plugin.getManager() instanceof ProxyModeManager){
            ProxyModeManager manager = (ProxyModeManager) ReplayAddonMain.plugin.getManager();
            if (manager.getManager() instanceof ProxyLobbyManager){

                ProxyData data = new ProxyData(
                        p.getUniqueId().toString(),
                        p.getName(),
                        replayID,
                        ReplayAddonMain.plugin.mainConfig.getString("proxy-mode.lobby.server-name"),
                        startTime,
                        loc.isEmpty()?null:loc.get(0),
                        loc.isEmpty()?null:loc.get(1),
                        loc.isEmpty()?null:loc.get(2)
                );
                Bukkit.getScheduler().runTaskAsynchronously(ReplayAddonMain.plugin, ()->{
                    ReplayAddonMain.plugin.db.setProxyData(data);
                    try {
                        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(byteArray);
                        out.writeUTF("Connect");
                        out.writeUTF(Util.getRandomElement(
                                plugin.mainConfig.getStringList("proxy-mode.lobby.playing-servers")
                        ));
                        p.sendPluginMessage(plugin, "BungeeCord", byteArray.toByteArray());
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                });
                return true;
            }
        }


        if(!GameReplayHandler.replayCacheID.containsKey(replayID)) {
            Bukkit.getScheduler().runTaskAsynchronously(ReplayAddonMain.plugin, () -> {
                GameReplayCache cache = ReplayAddonMain.plugin.db.getGameReplayCache(replayID);
                if (cache != null) {
                    GameReplayHandler.replayCacheID.put(replayID, cache);
                    Bukkit.getScheduler().runTask(ReplayAddonMain.plugin, ()-> {
                        ReplayAddonMain.plugin.getManager().playRecording(p, replayID);
                        if(startTime!=null&&loc.size()==3)
                            GameReplayHandler.startQueue.put(p, new StartQueue(startTime, loc));
                        else if(startTime!=null||loc.size()==3)
                            GameReplayHandler.startQueue.put(p, new StartQueue(startTime, loc));
                    });
                }else {
                    p.sendMessage(ChatColor.RED+"Replay not found!");
                }
            });
        }
        else {
            ReplayAddonMain.plugin.getManager().playRecording(p, replayID);
            if(startTime!=null&&loc.size()==3)
                GameReplayHandler.startQueue.put(p, new StartQueue(startTime, loc));
            else if(startTime!=null||loc.size()==3)
                GameReplayHandler.startQueue.put(p, new StartQueue(startTime, loc));
        }
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            results.add("<replayID>");
            return MainCommand.sortedResults(args[0], results);
        }
        if (args.length == 2) {
            results.add("<startTime(optional)>");
            return MainCommand.sortedResults(args[1], results);
        }
        if (args.length == 3) {
            results.add("<location[x-y-z](optional)");
            return MainCommand.sortedResults(args[2], results);
        }
        return null;
    }

    @Override
    public String getName() {
        return "view";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "replayAddon.command.view";
    }
}
