package dev.mrflyn.replayaddon.configs.confighelper;


import me.jumper251.replay.ReplaySystem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class AdvancedReplayUtil {

    public FileConfiguration getConfig(){
        ReplaySystem replay = (ReplaySystem) Bukkit.getServer().getPluginManager().getPlugin("AdvancedReplay");
        return replay.getConfig();
    }



}
