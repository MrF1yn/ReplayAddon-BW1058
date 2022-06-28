package dev.mrflyn.replayaddon.configs;

import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.guis.Buttons;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static dev.mrflyn.replayaddon.ReplayAddonMain.plugin;

public class ConfigManager {

    public static void loadConfigs(){
        plugin.mainConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder()+"/config.yml"));
        plugin.buttons = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder()+"/buttons.yml"));
    }

    public static void saveConfigs(){
        plugin.saveResource("config.yml", false);
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
