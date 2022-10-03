package dev.mrflyn.replayaddon.versionutils;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayCache;
import dev.mrflyn.replayaddon.advancedreplayhook.PlayerInfo;
import dev.mrflyn.replayaddon.configs.Messages;
import me.jumper251.replay.replaysystem.replaying.Replayer;
import me.jumper251.replay.replaysystem.replaying.ReplayingUtils;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static dev.mrflyn.replayaddon.ReplayAddonMain.plugin;

public class Util {
   static Random random = new Random();

    public static String generateID(String mapName, String gameMode, String date){
        return mapName+"-"+gameMode+"-"+date;
    }

    public static String getDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        return (dtf.format(now));
    }


    public static String formattedTime(long secs){
        long MM = TimeUnit.SECONDS.toMinutes(secs) % 60;
        long SS = TimeUnit.SECONDS.toSeconds(secs) % 60;
        return String.format("%02d:%02d", MM, SS);
    }

    public static String colorize(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static <T> void log(T msg){
        ReplayAddonMain.plugin.getLogger().log(Level.INFO, msg.toString());
    }

    public static <T> void error(T msg) {
        ReplayAddonMain.plugin.getLogger().log(Level.SEVERE, msg.toString());
    }

    public static <T> void debug(T msg) {
        if(ReplayAddonMain.plugin.mainConfig.getBoolean("debug")) {
            ReplayAddonMain.plugin.getLogger().log(Level.INFO, "[DEBUG]"+msg.toString());
        }
    }

    public static void setTime(Replayer replayer, int secs) {
        boolean isPaused = replayer.isPaused();
        replayer.getUtils().jumpTo(secs);
        replayer.setPaused(isPaused);
    }

    public static String translateLang(Messages m, Player p){
//        Util.debug(plugin.playerLang.toString());
//        Util.debug(plugin.allLanguages.toString());
        return plugin.playerLang.get(p.getUniqueId()).getCurrent(m, true);
    }

    public static List<String> translateLangList(Messages m, Player p) {
//        Util.debug(plugin.playerLang.toString());
//        Util.debug(plugin.allLanguages.toString());
        return plugin.playerLang.get(p.getUniqueId()).getCurrentList(m);
    }

    public static Location lookAt(Location loc, Location lookat) {
        //Clone the loc to prevent applied changes to the input loc
        loc = loc.clone();

        // Values of change in distance (make it relative)
        double dx = lookat.getX() - loc.getX();
        double dy = lookat.getY() - loc.getY();
        double dz = lookat.getZ() - loc.getZ();

        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                loc.setYaw((float) (1.5 * Math.PI));
            } else {
                loc.setYaw((float) (0.5 * Math.PI));
            }
            loc.setYaw((float) loc.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            loc.setYaw((float) Math.PI);
        }

        // Get the distance from dx/dz
        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

        // Set pitch
        loc.setPitch((float) -Math.atan(dy / dxz));

        // Set values, convert to degrees (invert the yaw since Bukkit uses a different yaw dimension format)
        loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
        loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);

        return loc;
    }

    public static BaseComponent[] getChatTimeLine(Player watcher,int totalBoxes, int totalDuration, double seperation, int currentTime,
                                                  HashMap<Integer, List<String>> deathTimes){
//        ComponentBuilder lines = new ComponentBuilder(StringUtils.repeat(" ", "\n", 99));
        ComponentBuilder component = new ComponentBuilder(StringUtils.repeat(" ", "\n", 99)+
                Util.translateLang(Messages.CHAT_TIMELINE_START, watcher));
        double boxTime = 0.0;
        for(int i = 0; i<totalBoxes; i++){
//            if (boxTime>totalDuration)break;
            StringBuilder hoverTxt = new StringBuilder(ChatColor.GREEN + formattedTime(Math.round(Math.ceil(boxTime))));
            StringBuilder box = new StringBuilder();
            boolean deathBOX = false;
            int death = 0;
            for(int dT : deathTimes.keySet()){
                deathBOX = boxTime==dT||(boxTime<dT&&boxTime+seperation>dT);
                death = dT;
            }
            if(deathBOX){
                hoverTxt.append("\n").append(ChatColor.RED).append("Died!");
                for(String s : deathTimes.get(death)){
                    hoverTxt.append("\n").append(ChatColor.WHITE).append(s).append(ChatColor.GRAY).append(" [")
                            .append(ChatColor.GREEN).append(formattedTime(death)).append(ChatColor.GRAY).append("]");
                }
                box.append(Util.translateLang(Messages.CHAT_TIMELINE_DEATH_SYM,watcher));
            }
            else if(boxTime<=currentTime||currentTime+seperation>totalDuration){
                box.append(Util.translateLang(Messages.CHAT_TIMELINE_CURRENT_SYM,watcher));
            }
            else {
                box.append(Util.translateLang(Messages.CHAT_TIMELINE_SYM,watcher));
            }
            component = component.append(i==totalBoxes-1?StringUtils.stripEnd(box.toString(), " "):box.toString());
            ComponentBuilder hover = new ComponentBuilder(hoverTxt.toString());
            component = component.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rp jumpTo "+Math.round(Math.ceil(boxTime))));

            boxTime = boxTime+seperation;
        }
        component.append(Util.translateLang(Messages.CHAT_TIMELINE_END, watcher));
        return component.create();
    }

    public static String parsePlayerInfoPlaceholders(String s, PlayerInfo info){
        //{tabPrefix}{playerName}{tabSuffix}
        return s.replace("{tabPrefix}", info.getTabPrefix()).replace("{tabSuffix}", info.getTabSuffix())
                .replace("{tagPrefix}", info.getTabPrefix()).replace("{tagSuffix}", info.getTabSuffix())
                .replace("{playerName}", info.getName());
    }

    public static List<String> parsePlayerInfoPlaceholders(List<String> s, PlayerInfo info) {
        //{tabPrefix}{playerName}{tabSuffix}
        return s.stream().map(value ->Util.colorize(
                value.replace("{tabPrefix}", info.getTabPrefix()).replace("{tabSuffix}", info.getTabSuffix())
                .replace("{tagPrefix}", info.getTabPrefix()).replace("{tagSuffix}", info.getTabSuffix())
                .replace("{playerName}", info.getName()))).collect(Collectors.toList());
    }

    public static List<String> parseInternalPlaceholders(List<String> s, GameReplayCache cache, Player p, String time) {
        //{tabPrefix}{playerName}{tabSuffix}
        String[] date = cache.getDate().split("_");
       return s.stream().map(value -> Util.colorize(value.replace("{date}", date[0] +" "+ date[1].replace("-",":"))
                       .replace("{duration}", Util.formattedTime(cache.getTotalDuration()))
                       .replace("{mode}", cache.getGameMode())
                       .replace("{map}", cache.getMapName())
                       .replace("{server}", Util.translateLang(Messages.SERVER_NAME, p))
                       .replace("{players}", cache.getPlayersWithUUIDs().size() + "")
                       .replace("{time}", time))
                ).collect(Collectors.toList());
    }

    public static String parseInternalPlaceholders(String s, GameReplayCache cache, Player p) {
        //{tabPrefix}{playerName}{tabSuffix}
        String[] date = cache.getDate().split("_");
        return s.replace("{date}", date[0] + " " + date[1].replace("-", ":"))
                .replace("{duration}", Util.formattedTime(cache.getTotalDuration()))
                .replace("{mode}", cache.getGameMode())
                .replace("{map}", cache.getMapName())
                .replace("{server}", Util.translateLang(Messages.SERVER_NAME, p))
                .replace("{players}", cache.getPlayersWithUUIDs().size() + "");
    }



    public static <T> T getRandomElement(List<T> list){
        return list.get(random.nextInt(list.size()));
    }

    public static String getConfigValue(String path){
        return plugin.mainConfig.getString(path, "");
    }


}
