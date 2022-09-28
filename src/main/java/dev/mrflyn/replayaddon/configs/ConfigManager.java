package dev.mrflyn.replayaddon.configs;

import dev.mrflyn.replayaddon.configs.confighelper.AutoConfig;
import dev.mrflyn.replayaddon.guis.Buttons;
import dev.mrflyn.replayaddon.versionutils.Util;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;

import static dev.mrflyn.replayaddon.ReplayAddonMain.plugin;

public class ConfigManager {

    public static void loadConfigs(){
        plugin.mainConfig = new YamlFile(plugin.getDataFolder()+"/config.yml");
        plugin.dbConfig = new YamlFile(plugin.getDataFolder()+"/database.yml");
        try {
            plugin.dbConfig.loadWithComments();
            plugin.mainConfig.loadWithComments();
            if (plugin.mainConfig.getString("server-mode").equalsIgnoreCase("none")){
                new AutoConfig().execute();
                Util.log("Auto-Configured. Restarting Server.");
                Bukkit.getServer().shutdown();
            }
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
        plugin.buttons = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder()+"/buttons.yml"));
    }

    public static void saveConfigs(){
        plugin.saveResource("config.yml", false);
        plugin.saveResource("database.yml", false);
        saveButtons();
    }


    public static void saveButtons() {
        try {
            File file = new File(plugin.getDataFolder(), "buttons.yml");
            if (file.exists()) return;
            file.createNewFile();
            plugin.buttons = YamlConfiguration.loadConfiguration(file);
            String prefix = "buttons.";
            for(Buttons button : Buttons.values()){

                plugin.buttons.set(prefix + button.name() + ".slot", button.slot());
                plugin.buttons.set(prefix + button.name() + ".material", button.material().toString());
                plugin.buttons.set(prefix + button.name() + ".displayName", button.displayName());
                plugin.buttons.set(prefix + button.name() + ".lore", button.lore());
                plugin.buttons.set(prefix + button.name() + ".skin", button.skin());
                plugin.buttons.set(prefix + button.name() + ".data", button.data());


            }
            plugin.buttons.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
