package dev.mrflyn.replayaddon.managers.proxymode.proxylobbymanager;

import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.advancedreplayhook.ProxyData;
import dev.mrflyn.replayaddon.commands.GamesCommand;
import dev.mrflyn.replayaddon.commands.ReloadCommand;
import dev.mrflyn.replayaddon.commands.ViewCommand;
import dev.mrflyn.replayaddon.commands.handler.MainCommand;
import dev.mrflyn.replayaddon.managers.proxymode.IProxyManager;
import dev.mrflyn.replayaddon.managers.sharedmode.SharedListener;
import dev.mrflyn.replayaddon.versionutils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static dev.mrflyn.replayaddon.ReplayAddonMain.plugin;

public class ProxyLobbyManager implements IProxyManager {
    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(new ProxyLobbyListener(), ReplayAddonMain.plugin);
        MainCommand command = new MainCommand(
                new GamesCommand(),
                new ViewCommand(),
                new ReloadCommand()
        );
        ReplayAddonMain.plugin.getCommand("rp").setExecutor(command);
        ReplayAddonMain.plugin.getCommand("rp").setTabCompleter(command);
    }

    @Override
    public void playRecording(Player p, String replayID) {
        String uuid = p.getUniqueId().toString();
        String name = p.getName();
        Bukkit.getScheduler().runTaskAsynchronously(ReplayAddonMain.plugin, ()->{
            if(plugin.db.getGameReplayCache(replayID)==null){
                p.sendMessage(ChatColor.RED + "Replay not found!");
                return;
            }
            ProxyData data = new ProxyData(
                    uuid,
                    name,
                    replayID,
                    ReplayAddonMain.plugin.mainConfig.getString("proxy-mode.lobby.server-name"),
                    null,null,null,null

            );
            ReplayAddonMain.plugin.db.setProxyData(data);
            try {
                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(byteArray);
                out.writeUTF("Connect");
                out.writeUTF(Util.getRandomElement(
                        plugin.mainConfig.getStringList("proxy-mode.lobby.playing-servers")
                ));
                p.sendPluginMessage(plugin, "BungeeCord", byteArray.toByteArray());
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }

    @Override
    public String getMode() {
        return "proxy-lobby-mode";
    }
}
