package dev.mrflyn.replayaddon.configs.confighelper;

import com.andrei1058.bedwars.BedWars;
import org.bukkit.Bukkit;

public class BW1058Util {

    public String getBWServerMode(){

        BedWars bw = (BedWars) Bukkit.getServer().getPluginManager().getPlugin("BedWars1058");
        return bw.getConfig().getString("serverType");
    }



}
