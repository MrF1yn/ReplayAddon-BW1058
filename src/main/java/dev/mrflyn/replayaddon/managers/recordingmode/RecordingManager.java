package dev.mrflyn.replayaddon.managers.recordingmode;

import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.commands.AutoConfigCommand;
import dev.mrflyn.replayaddon.commands.ReloadCommand;
import dev.mrflyn.replayaddon.commands.handler.MainCommand;
import dev.mrflyn.replayaddon.databases.SQLite;
import dev.mrflyn.replayaddon.managers.IManager;
import dev.mrflyn.replayaddon.versionutils.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RecordingManager implements IManager {

    @Override
    public void init(){

        if(Bukkit.getServer().getPluginManager().getPlugin("AdvancedReplay")==null
                || !Bukkit.getServer().getPluginManager().getPlugin("AdvancedReplay").getDescription().getAuthors().contains("MrF1yn")){
            Util.error("RecordingMode needs AdvancedReplay-Extended to run!");
            Bukkit.getServer().getPluginManager().disablePlugin(ReplayAddonMain.plugin);
            return;
        }
        if (Bukkit.getServer().getPluginManager().getPlugin("BedWars1058") == null) {
            Util.error("RecordingMode needs BedWars1058 to run!");
            Bukkit.getServer().getPluginManager().disablePlugin(ReplayAddonMain.plugin);
            return;
        }
        if  (ReplayAddonMain.plugin.db instanceof SQLite){
            Util.error("Recording Mode needs either MySQL or PostgreSQL. Connect to the same database as the other servers.");
            Bukkit.getServer().getPluginManager().disablePlugin(ReplayAddonMain.plugin);
            return;
        }

        Bukkit.getServer().getPluginManager().registerEvents(new RecordingListener(), ReplayAddonMain.plugin);
        MainCommand command = new MainCommand(
                new ReloadCommand(),
                new AutoConfigCommand()
        );
        ReplayAddonMain.plugin.getCommand("rp").setExecutor(command);
        ReplayAddonMain.plugin.getCommand("rp").setTabCompleter(command);

    }

    @Override
    public String getMode() {
        return "recording-mode";
    }

    @Override
    public void playRecording(Player p, String replayID) {

    }

}
