package dev.mrflyn.replayaddon.commands;

import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayCache;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayHandler;
import dev.mrflyn.replayaddon.advancedreplayhook.StartQueue;
import dev.mrflyn.replayaddon.commands.handler.MainCommand;
import dev.mrflyn.replayaddon.commands.handler.SubCommand;
import dev.mrflyn.replayaddon.versionutils.Util;
import me.jumper251.replay.replaysystem.replaying.ReplayHelper;
import me.jumper251.replay.replaysystem.replaying.Replayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class JumpToCommand implements SubCommand {

    public JumpToCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if(args.length<1){
            p.sendMessage(ChatColor.RED+"The correct format is /rp jumpTo <time in seconds>.");
            return true;
        }
        int time;
        try{
            time = Integer.parseInt(args[0]);
            if(time<0){
                p.sendMessage(ChatColor.RED+"Time needs to be a positive integer.");
                return true;
            }
        }catch (Exception e){
            p.sendMessage(ChatColor.RED+"Time needs to be a positive integer.");
            return true;
        }
        if(!ReplayHelper.replaySessions.containsKey(p.getName())){
            p.sendMessage(ChatColor.RED+"You need to be in a replay to use this command.");
            return true;
        }
        if(time<1)time=1;
        Replayer replayer = ReplayHelper.replaySessions.get(p.getName());
        Util.setTime(replayer, time);
        p.sendMessage(ChatColor.GREEN+"Skipped to "+time+" seconds.");
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            results.add("<time in seconds>");
            return MainCommand.sortedResults(args[0], results);
        }
        return null;
    }

    @Override
    public String getName() {
        return "jumpTo";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "replayAddon.command.jumpTo";
    }
}
