package dev.mrflyn.replayaddon.configs.confighelper;

import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.versionutils.Util;
import org.bukkit.Bukkit;

import java.io.IOException;

public class AutoConfig {

    public AutoConfig(){

    }

    public void execute() {

        if (Bukkit.getServer().getPluginManager().getPlugin("BedWars1058") != null) {
            Util.log("Auto-Configuring Replay-Addon, BedWars1058 Found!.");
            BW1058Util util = new BW1058Util();
            if (util.getBWServerMode().equals("SHARED")||util.getBWServerMode().equals("MULTIARENA")){
                ReplayAddonMain.plugin.mainConfig.set("server-mode", "shared");
            }

            if (util.getBWServerMode().equals("BUNGEE")){
                ReplayAddonMain.plugin.mainConfig.set("server-mode", "recording");
            }

        }
        else if (Bukkit.getServer().getPluginManager().getPlugin("BedWarsProxy") != null) {
            Util.log("Auto-Configuring Replay-Addon, BedWarsProxy Found!.");
                ReplayAddonMain.plugin.mainConfig.set("server-mode", "playing");
        }
        if (Bukkit.getServer().getPluginManager().getPlugin("AdvancedReplay") != null) {
            Util.log("Auto-Configuring AdvancedReplay.");
            AdvancedReplayUtil aUtil = new AdvancedReplayUtil();
            aUtil.getConfig().set("general.upload_worlds", true);
            aUtil.getConfig().set("general.use_mysql", true);
            try {
                aUtil.getConfig().save("plugins/AdvancedReplay/config.yml");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try {
            ReplayAddonMain.plugin.mainConfig.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
