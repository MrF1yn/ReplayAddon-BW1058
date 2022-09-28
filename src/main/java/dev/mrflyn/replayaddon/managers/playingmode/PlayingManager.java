package dev.mrflyn.replayaddon.managers.playingmode;

import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayHandler;
import dev.mrflyn.replayaddon.commands.*;
import dev.mrflyn.replayaddon.commands.handler.MainCommand;
import dev.mrflyn.replayaddon.databases.SQLite;
import dev.mrflyn.replayaddon.guis.CustomReplaySessionSettings;
import dev.mrflyn.replayaddon.managers.IManager;
import dev.mrflyn.replayaddon.managers.sharedmode.SharedListener;
import dev.mrflyn.replayaddon.versionutils.Util;
import me.jumper251.replay.ReplaySystem;
import me.jumper251.replay.filesystem.saving.ReplaySaver;
import me.jumper251.replay.replaysystem.Replay;
import me.jumper251.replay.replaysystem.replaying.ReplayHelper;
import me.jumper251.replay.utils.fetcher.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayingManager implements IManager {


    @Override
    public void init(){
        if(Bukkit.getServer().getPluginManager().getPlugin("AdvancedReplay")==null){
            Util.log("Playing Mode needs AdvancedReplay to run!");
            Bukkit.getServer().getPluginManager().disablePlugin(ReplayAddonMain.plugin);
            return;
        }
        if  (ReplayAddonMain.plugin.db instanceof SQLite){
            Util.log("Playing Mode needs either MySQL or PostgreSQL. Connect to the same database as the other servers.");
            Bukkit.getServer().getPluginManager().disablePlugin(ReplayAddonMain.plugin);
            return;
        }

        Bukkit.getServer().getPluginManager().registerEvents(new PlayingListener(), ReplayAddonMain.plugin);
        MainCommand command = new MainCommand(
                new GamesCommand(),
                new ViewCommand(),
                new JumpToCommand(),
                new ReloadCommand(),
                new AutoConfigCommand()
        );
        ReplayAddonMain.plugin.getCommand("rp").setExecutor(command);
        ReplayAddonMain.plugin.getCommand("rp").setTabCompleter(command);

    }

    @Override
    public String getMode() {
        return "playing-mode";
    }

    @Override
    public void playRecording(Player p, String replayID) {
        if (ReplaySaver.exists(replayID) && !ReplayHelper.replaySessions.containsKey(p.getName())) {
            p.sendMessage(ReplaySystem.PREFIX + "Loading replay §e" + replayID + "§7...");
            try {
                ReplaySaver.load(replayID, new Consumer<Replay>() {

                    @Override
                    public void accept(Replay replay) {
                        p.sendMessage(ReplaySystem.PREFIX + "Replay loaded. Duration §e" + (replay.getData().getDuration() / 20) + "§7 seconds.");
                        GameReplayHandler.playingReplays.put(p, new CustomReplaySessionSettings(p, replayID));
                        replay.play(p);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();

                p.sendMessage(ReplaySystem.PREFIX + "§cError while loading §o" + replayID + ".replay. §r§cCheck console for more details.");
            }
        } else {
            p.sendMessage(ReplaySystem.PREFIX + "§cReplay not found.");
        }
    }

}
