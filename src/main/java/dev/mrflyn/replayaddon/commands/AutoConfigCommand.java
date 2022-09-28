package dev.mrflyn.replayaddon.commands;

import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayCache;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayHandler;
import dev.mrflyn.replayaddon.advancedreplayhook.ProxyData;
import dev.mrflyn.replayaddon.advancedreplayhook.StartQueue;
import dev.mrflyn.replayaddon.commands.handler.MainCommand;
import dev.mrflyn.replayaddon.commands.handler.SubCommand;
import dev.mrflyn.replayaddon.configs.confighelper.AutoConfig;
import dev.mrflyn.replayaddon.managers.proxymode.ProxyModeManager;
import dev.mrflyn.replayaddon.managers.proxymode.proxylobbymanager.ProxyLobbyManager;
import dev.mrflyn.replayaddon.versionutils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import static dev.mrflyn.replayaddon.ReplayAddonMain.plugin;

public class AutoConfigCommand implements SubCommand {

    public AutoConfigCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args) {
        new AutoConfig().execute();
        sender.sendMessage("Auto-Configured. Please restart the server.");
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }

    @Override
    public String getName() {
        return "autoConfig";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "replayAddon.command.autoConfig";
    }
}
