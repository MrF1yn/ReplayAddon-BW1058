package dev.mrflyn.replayaddon.commands.handler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {

     boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args);

     List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args);

     String getName();

     boolean isProtected();

     String getPermission();
}
