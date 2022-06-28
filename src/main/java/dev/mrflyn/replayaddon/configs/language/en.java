package dev.mrflyn.replayaddon.configs.language;


import dev.mrflyn.replayaddon.configs.Messages;
import dev.mrflyn.replayaddon.versionutils.Util;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Field;

import java.util.Arrays;
import java.util.List;


import static dev.mrflyn.replayaddon.ReplayAddonMain.plugin;

public class en implements ILang {
    public YamlConfiguration configuration;
    public String NO_PERM_MESSAGE = "&cYou do not have the required permission to execute this command!";
    public String SERVER_NAME = "&aReplayAddon-BETA";
    public String CHAT_TIMELINE_SYM = "&f∎ " ;
    public String CHAT_TIMELINE_DEATH_SYM = "&6∎ ";
    public String CHAT_TIMELINE_DEATH_SYM_TITLE = "&cDied!";
    public String CHAT_TIMELINE_CURRENT_SYM = "&a∎ ";
    public String CHAT_TIMELINE_START = "";
    public String CHAT_TIMELINE_END = "";
    public String GUI_FILLER_MATERIAL = "BLACK_STAINED_GLASS_PANE";
    public String GUI_FILLER_DISPLAYNAME = "";
    public List<String> GUI_FILLER_LORE = Arrays.asList("");
    public String GUI_BACK_MATERIAL = "ARROW";
    public String GUI_BACK_DISPLAYNAME = "&aBack to Main Menu.";
    public List<String> GUI_BACK_LORE = Arrays.asList("");
    public String MORESETTINGS_GUI_TITLE = "More Settings";
    public String MORESETTINGS_VIEWERSETTINGS_DISPLAYNAME = "&aViewer Settings";
    public List<String> MORESETTINGS_VIEWERSETTINGS_LORE = Arrays.asList(
            "&7Viewer settings for this recording.",
            "",
            "&eClick to view!"
            );
    public String MORESETTINGS_BOOKMARKS_DISPLAYNAME = "&aBookmarks";
    public List<String> MORESETTINGS_BOOKMARKS_LORE = Arrays.asList(
            "&7View bookmarks for this recording.",
            "",
            "&eClick to view!"
    );
    public String MORESETTINGS_SHARE_DISPLAYNAME = "&aShare Replay";
    public String MORESETTINGS_SHARE_CLICKMSG = "&6&lClick to share replay!";
    public List<String> MORESETTINGS_SHARE_LORE = Arrays.asList(
            "&7Share this recording.",
            "",
            "&eClick to get link!"
    );
    public String MORESETTINGS_LEAVE_DISPLAYNAME = "&cLeave";
    public List<String> MORESETTINGS_LEAVE_LORE = Arrays.asList(
            "",
            "&eClick to leave this replay!"
    );
    public String BOOKMARK_GUI_TITLE = "Bookmarks";
    public String BOOKMARK_ITEM_MATERIAL = "PAPER";
    public String BOOKMARK_ITEM_DISPLAYNAME = "&aDied.";
    public List<String> BOOKMARK_ITEM_LORE = Arrays.asList(
            "&7Time: &a{time} seconds.",
            "",
            "&aPlayer: &f{tabPrefix}{playerName}{tabSuffix}",
            "",
            "&eLeft Click to go to this time!",
            "&eRight Click to share!"
    );
    public String BOOKMARK_CLICK_MSG = "&6&lClick to share bookmark!";
    public String VIEWERSETTINGS_GUI_TITLE = "Viewer Settings";
    public String VIEWERSETTINGS_CT_DISPLAYNAME = "Chat Timeline";
    public String VIEWERSETTINGS_SHOWSPEC_DISPLAYNAME = "Show Spectators";
    public String VIEWERSETTINGS_FLYSPEED_DISPLAYNAME = "&cFly Speed";
    public List<String> VIEWERSETTINGS_FLYSPEED_LORE = Arrays.asList(
            "&aCurrently Selected: &6{speed}x.",
            "",
            "&7Click to set Fly Speed to &6{nxtSpeed}x.",
            "",
            "&eClick to cycle!"
    );
    public String ENABLED_COLOR = "&a";
    public String DISABLED_COLOR = "&c";
    public List<String> ENABLED_LORE = Arrays.asList(
            "&eClick to disable!");
    public List<String> DISABLED_LORE = Arrays.asList(
            "&eClick to enable!");
    public String PLAYERREPLAYS_GUI_TITLE = "Saved Replays";
    public String PLAYERREPLAYS_REPLAY_MATERIAL = "BED";
    public String PLAYERREPLAYS_REPLAY_DISPLAYNAME = "&aBedWars";
    public List<String> PLAYERREPLAYS_REPLAY_LORE = Arrays.asList(
            "&7{date}",
            "&7Duration: {duration}",
            "",
            "&7Mode: &a{mode}",
            "&7Map: &a{map}",
            "",
            "&7Server: {server}",
            "&7Players: {players}",
            "",
            "&eClick to view replay!"
    );



    public en(){}


    @Override
    public void createFile(){
        try {
            File file = new File(plugin.getDataFolder()+"/languages", "en.yml");
            if (!file.exists()) {
                file.createNewFile();
            }
            configuration = YamlConfiguration.loadConfiguration(file);
            configuration.options().header("\\u220e is this symbol https://unicode-table.com/en/220E/ \n" +
                    "It looks like this in the config due to how its encoded to a file but will work fine in-game.");
//            configuration.addDefault(Messages.SERVER_NAME.name(), SERVER_NAME);
//            configuration.addDefault(Messages.NO_PERM_MESSAGE.name(), NO_PERM_MESSAGE);
//            configuration.addDefault(Messages.CHAT_TIMELINE_SYM.name(), CHAT_TIMELINE_SYM);
//            configuration.addDefault(Messages.CHAT_TIMELINE_DEATH_SYM.name(), CHAT_TIMELINE_DEATH_SYM);
//            configuration.addDefault(Messages.CHAT_TIMELINE_CURRENT_SYM.name(), CHAT_TIMELINE_CURRENT_SYM);
//            configuration.addDefault(Messages.CHAT_TIMELINE_START.name(), CHAT_TIMELINE_START);
//            configuration.addDefault(Messages.CHAT_TIMELINE_END.name(), CHAT_TIMELINE_END);
            for (Field field : Messages.class.getFields()){
                configuration.addDefault(field.getName(), this.getClass().getField(field.getName()).get(this));
            }


            configuration.options().copyDefaults(true);
            configuration.options().copyHeader(true);
            configuration.save(file);
            configuration = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder()+"/languages", "en.yml"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String getCurrent(Messages m, boolean colorize){
        String msg = configuration.getString(m.name());
        return colorize? Util.colorize(msg):msg;
    }

    @Override
    public List<String> getCurrentList(Messages m) {
        return configuration.getStringList(m.name());
    }

}
