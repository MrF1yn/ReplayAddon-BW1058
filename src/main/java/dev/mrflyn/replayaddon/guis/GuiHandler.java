package dev.mrflyn.replayaddon.guis;

import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayHandler;
import org.bukkit.entity.Player;

public class GuiHandler {

    public static void onJoin(Player p){
        //load playerReplayGui
        if(PlayerReplaysGUI.playerReplayGuiCache.containsKey(p))return;
        PlayerReplaysGUI gui = new PlayerReplaysGUI(p, GameReplayHandler.replayCachePerPlayer.get(p.getUniqueId()));
        PlayerReplaysGUI.playerReplayGuiCache.put(p, gui);
    }

    public static void onLeave(Player p) {
        //unload playerReplayGui
        if (!PlayerReplaysGUI.playerReplayGuiCache.containsKey(p)) return;
        PlayerReplaysGUI.playerReplayGuiCache.remove(p);
    }




}
