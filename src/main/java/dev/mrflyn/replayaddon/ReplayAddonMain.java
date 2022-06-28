package dev.mrflyn.replayaddon;

import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.mrflyn.replayaddon.configs.ConfigManager;
import dev.mrflyn.replayaddon.configs.language.en;
import dev.mrflyn.replayaddon.configs.language.ILang;
import dev.mrflyn.replayaddon.databases.IDatabase;
import dev.mrflyn.replayaddon.databases.MySQL;
import dev.mrflyn.replayaddon.databases.PostgreSQL;
import dev.mrflyn.replayaddon.databases.SQLite;
import dev.mrflyn.replayaddon.guis.Button;
import dev.mrflyn.replayaddon.managers.IManager;
import dev.mrflyn.replayaddon.managers.playingmode.PlayingManager;
import dev.mrflyn.replayaddon.managers.proxymode.ProxyModeManager;
import dev.mrflyn.replayaddon.managers.recordingmode.RecordingManager;
import dev.mrflyn.replayaddon.managers.sharedmode.SharedManager;
import dev.mrflyn.replayaddon.spigui.SpiGUI;
import dev.mrflyn.replayaddon.versionutils.*;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ReplayAddonMain extends JavaPlugin {

    public static ReplayAddonMain plugin;
    public YamlConfiguration mainConfig;
    public YamlConfiguration buttons;
    public IDatabase db;
    public HashMap<String, Button> initializedButtons;
    private IManager manager;
    public SpiGUI spiGUI;
    public HashMap<String, ILang> allLanguages = new HashMap<>();
    public HashMap<UUID, ILang> playerLang = new HashMap<>();

    public void onEnable(){
        plugin = this;
        spiGUI = new SpiGUI(this);
        initializedButtons = new HashMap<>();
        initializeDependencies();
        ConfigManager.saveConfigs();
        ConfigManager.loadConfigs();
        updateLanguages();
        initButtons();
        if(mainConfig.getBoolean("storage.mysql.enabled")){
            db = new MySQL();
        }else if(mainConfig.getBoolean("storage.postgresql.enabled")){
            db = new PostgreSQL();
        }else {
            db = new SQLite();
        }
        if(!db.connect()){
            Util.log(db.name()+" database connection unsuccessful.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Util.log(db.name()+" database connection successful.");
        db.init();
        if(mainConfig.getBoolean("proxy-mode.enabled")){
            manager = new ProxyModeManager();
            if(!((db instanceof MySQL)||(db instanceof PostgreSQL))){
                Util.log("proxy-mode requires mysql or postgresql connection!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }else {
            switch (mainConfig.getString("server-mode")) {
                case "recording":
                    manager = new RecordingManager();
                    break;
                case "playing":
                    manager = new PlayingManager();
                    break;
                case "shared":
                    manager = new SharedManager();
                    break;
            }
        }
        manager.init();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI")!=null){

            SupportPAPI.setSupportPAPI(new SupportPAPI.withPAPI());

        }
        Util.log("running in "+manager.getMode()+".");

    }

    public void updateLanguages() {
        File file = new File(getDataFolder()+"/languages");
        Util.debug("Directory Created: "+file.mkdir());
        allLanguages.clear();
        ILang english = new en();Util.debug(english.getClass().getSimpleName());
        english.createFile();allLanguages.put(english.getClass().getSimpleName(), english);
        HashMap<UUID, ILang> newPlayerLang = new HashMap<>();
        for(UUID uuid : playerLang.keySet()){
            newPlayerLang.put(uuid, allLanguages.get(playerLang.get(uuid).getClass().getSimpleName()));
        }
        playerLang = newPlayerLang;

    }

    public void reloadConfigs(){
        reloadConfig();
        updateLanguages();
    }

    public IManager getManager(){
        return this.manager;
    }

    private void initializeDependencies(){
//        <dependency>
//            <groupId>me.minidigger</groupId>
//            <artifactId>minimessage-text</artifactId>
//            <version>2.1.0</version>
//            <scope>provided</scope>
//        </dependency>
//        <dependency>
//            <groupId>net.kyori</groupId>
//            <artifactId>text-adapter-bukkit</artifactId>
//            <version>3.0.6</version>
//            <scope>provided</scope>
//        </dependency>
        BukkitLibraryManager libraryManager = new BukkitLibraryManager(plugin);
        Library postgresql = Library.builder()
                .groupId("org{}postgresql") // "{}" is replaced with ".", useful to avoid unwanted changes made by maven-shade-plugin
                .artifactId("postgresql")
                .version("42.3.5")
                .id("postgresql")
                .relocate("org{}postgresql",
                        "dev{}mrflyn{}replayaddon{}libs{}org{}postgresql")
                .build();

        libraryManager.addMavenCentral();
        libraryManager.loadLibrary(postgresql);
        try {
            Class.forName("dev.mrflyn.replayaddon.libs.org.postgresql.Driver");
            System.out.println("Libby working!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initButtons(){
        String prefix = "buttons.";
        for(String bName : buttons.getConfigurationSection("buttons").getKeys(false)){
            List<String> lore = new ArrayList<>();
            for(String s : buttons.getStringList(prefix+bName+".lore")){
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            XMaterial material = XMaterial.matchXMaterial(buttons.getString(prefix+bName+".material")).orElse(XMaterial.BARRIER);
            ItemStack item = null;
            if(material.parseMaterial().name().equalsIgnoreCase("SKULL_ITEM")||
                    material.parseMaterial().name().equalsIgnoreCase("PLAYER_HEAD")){
                item = new ItemBuilder(material.parseMaterial())
                        .name(ChatColor
                                .translateAlternateColorCodes('&', buttons.getString(prefix + bName + ".displayName")))
                        .lore(lore)
                        .build();
                String skin = buttons.getString(prefix+bName+".skin");
                if (skin!=null && !skin.equals("")) {
                    item = new ItemBuilder(SkullCreator.itemWithBase64(XMaterial.PLAYER_HEAD.parseItem(), skin))
                            .name(ChatColor
                                    .translateAlternateColorCodes('&', buttons.getString(prefix + bName + ".displayName")))
                            .lore(lore)
                            .build();
                }
            }
            else {
                item = new ItemBuilder(material.parseMaterial())
                        .name(ChatColor
                                .translateAlternateColorCodes('&', buttons.getString(prefix + bName + ".displayName")))
                        .lore(lore)
                        .build();
            }
            NBTItem nbtItem = new NBTItem(item);
            nbtItem.setString("replay-addon-bw1058", buttons.getString(prefix+bName+".data"));
            item = nbtItem.getItem();
            Button button = new Button(item,bName,buttons.getInt(prefix+bName+".slot"));
            initializedButtons.put(bName, button);
        }
    }


}
