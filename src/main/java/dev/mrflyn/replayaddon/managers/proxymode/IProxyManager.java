package dev.mrflyn.replayaddon.managers.proxymode;

import org.bukkit.entity.Player;

public interface IProxyManager {

    void init();
    void playRecording(Player p, String replayID);
    String getMode();

}
