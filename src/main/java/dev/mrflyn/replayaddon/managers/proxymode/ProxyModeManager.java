package dev.mrflyn.replayaddon.managers.proxymode;

import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.advancedreplayhook.ProxyData;
import dev.mrflyn.replayaddon.managers.IManager;
import dev.mrflyn.replayaddon.managers.proxymode.proxylobbymanager.ProxyLobbyManager;
import dev.mrflyn.replayaddon.managers.proxymode.proxyplayingmode.ProxyPlayingManager;
import dev.mrflyn.replayaddon.versionutils.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ProxyModeManager implements IManager {
    private IProxyManager manager;
    public static HashMap<UUID, ProxyData> proxyDataCache = new HashMap<>();
    @Override
    public void init(){
        if(!ReplayAddonMain.plugin.mainConfig.getBoolean("proxy-mode.lobby.enabled")) {
            if (Bukkit.getServer().getPluginManager().getPlugin("AdvancedReplay") == null) {
                Util.log("Proxy-playing-Mode needs AdvancedReplay to run!");
                Bukkit.getServer().getPluginManager().disablePlugin(ReplayAddonMain.plugin);
                return;
            }
            manager = new ProxyPlayingManager();
        }else {
            manager = new ProxyLobbyManager();
        }
        manager.init();
        if (Bukkit.getServer().getMessenger().isOutgoingChannelRegistered(ReplayAddonMain.plugin, "BungeeCord")){
            Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(ReplayAddonMain.plugin, "BungeeCord");
        }
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(ReplayAddonMain.plugin, "BungeeCord");
    }

    public IProxyManager getManager(){
        return this.manager;
    }

    @Override
    public String getMode() {
        return manager.getMode();
    }

    @Override
    public void playRecording(Player p, String replayID) {
        manager.playRecording(p, replayID);
    }

}
