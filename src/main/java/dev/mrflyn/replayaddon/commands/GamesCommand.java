package dev.mrflyn.replayaddon.commands;

import dev.mrflyn.replayaddon.commands.handler.SubCommand;
import dev.mrflyn.replayaddon.configs.Messages;
import dev.mrflyn.replayaddon.guis.PlayerReplaysGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GamesCommand implements SubCommand {

    public GamesCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if(!PlayerReplaysGUI.playerReplayGuiCache.containsKey(p)){
            p.sendMessage(ChatColor.RED+"No Replays exist. If you played a game recently please rejoin the server to update your replays.");
            return true;
        }
        PlayerReplaysGUI gui = PlayerReplaysGUI.playerReplayGuiCache.get(p);
        p.openInventory(gui.getCachedInventory());
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }

    @Override
    public String getName() {
        return "games";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "replayAddon.command.games";
    }
}
