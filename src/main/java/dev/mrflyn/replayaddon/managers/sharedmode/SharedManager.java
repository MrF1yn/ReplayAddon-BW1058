package dev.mrflyn.replayaddon.managers.sharedmode;

import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayHandler;
import dev.mrflyn.replayaddon.commands.ReloadCommand;
import dev.mrflyn.replayaddon.guis.CustomReplaySessionSettings;
import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.commands.GamesCommand;
import dev.mrflyn.replayaddon.commands.JumpToCommand;
import dev.mrflyn.replayaddon.commands.ViewCommand;
import dev.mrflyn.replayaddon.commands.handler.MainCommand;
import dev.mrflyn.replayaddon.managers.IManager;
import dev.mrflyn.replayaddon.versionutils.Util;
import me.jumper251.replay.ReplaySystem;
import me.jumper251.replay.filesystem.saving.ReplaySaver;
import me.jumper251.replay.replaysystem.Replay;
import me.jumper251.replay.replaysystem.replaying.ReplayHelper;
import me.jumper251.replay.utils.fetcher.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SharedManager implements IManager {

    @Override
    public void init(){
        if(Bukkit.getServer().getPluginManager().getPlugin("AdvancedReplay")==null){
            Util.log("SharedMode needs AdvancedReplay to run!");
            Bukkit.getServer().getPluginManager().disablePlugin(ReplayAddonMain.plugin);
            return;
        }
        if (Bukkit.getServer().getPluginManager().getPlugin("BedWars1058") == null) {
            Util.log("SharedMode needs BedWars1058 to run!");
            Bukkit.getServer().getPluginManager().disablePlugin(ReplayAddonMain.plugin);
            return;
        }



        Bukkit.getServer().getPluginManager().registerEvents(new SharedListener(), ReplayAddonMain.plugin);
        MainCommand command = new MainCommand(
                new GamesCommand(),
                new ViewCommand(),
                new JumpToCommand(),
                new ReloadCommand()
        );
        ReplayAddonMain.plugin.getCommand("rp").setExecutor(command);
        ReplayAddonMain.plugin.getCommand("rp").setTabCompleter(command);

    }

    @Override
    public String getMode() {
        return "shared-mode";
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
