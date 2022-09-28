package dev.mrflyn.replayaddon.commands.handler;
import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.configs.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

import static dev.mrflyn.replayaddon.ReplayAddonMain.plugin;

public class MainCommand implements CommandExecutor, TabCompleter {
    private HashMap<String, SubCommand> registeredSubCommands = new HashMap<>();
    private List <String> results = new ArrayList<>();

    public MainCommand(SubCommand... subCommands){
       for(SubCommand cmd : subCommands){
           registeredSubCommands.put(cmd.getName(), cmd);
       }
    }

    public void registerSubCommand(SubCommand command){
        if(registeredSubCommands.containsKey(command.getName()))return;
        registeredSubCommands.put(command.getName(), command);
    }

    public void unregisterSubCommand(SubCommand command) {
        if (!registeredSubCommands.containsKey(command.getName())) return;
        registeredSubCommands.remove(command.getName());
    }

    public void unregisterAll() {
        registeredSubCommands.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            //doesnt exist
            sender.sendMessage("Available Commands:-");
            String s = "";
            for(String name : registeredSubCommands.keySet()){
                s = s + "/rp "+name + "\n";
            }
            sender.sendMessage(s);
            return true;
        }
        if(!registeredSubCommands.containsKey(args[0])){
            //doesnt exist
            return true;
        }
        SubCommand subCommand = registeredSubCommands.get(args[0]);
        if(subCommand.isProtected()&&!(sender.hasPermission(subCommand.getPermission())||sender.hasPermission("replayAddon.admin"))) {
            //NO permission
            if(!(sender instanceof Player))
                sender.sendMessage(plugin.allLanguages.get("en").getCurrent(Messages.NO_PERM_MESSAGE, true));
            else {
                Player p = (Player)sender;
                p.sendMessage(plugin.allLanguages.get("en").getCurrent(Messages.NO_PERM_MESSAGE, true));
            }
            return true;
        }
        List<String> fargs = new ArrayList<>(Arrays.asList(args));
        fargs.remove(0);
        return subCommand.onSubCommand(sender,cmd,label, fargs.toArray(new String[0]));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            results.clear();
            results.addAll(registeredSubCommands.keySet());
            return sortedResults(args[0], results);
        }
        if(args.length>1){
            results.clear();
            if(!registeredSubCommands.containsKey(args[0])){
                //doesnt exist
                return results;
            }
            SubCommand subCommand = registeredSubCommands.get(args[0]);
            List<String> fargs = new ArrayList<>(Arrays.asList(args));
            fargs.remove(0);
            return subCommand.suggestTabCompletes(sender,cmd,label,fargs.toArray(new String[0]));
        }

        return null;
    }

    public static List <String> sortedResults(String arg, List<String> results) {
        final List < String > completions = new ArrayList < > ();
        StringUtil.copyPartialMatches(arg, results, completions);
        Collections.sort(completions);
        results.clear();
        results.addAll(completions);
        return results;
    }
}

