package dev.mrflyn.replayaddon.advancedreplayhook;

import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.guis.ChatTimeline;
import dev.mrflyn.replayaddon.managers.sharedmode.SharedManager;
import dev.mrflyn.replayaddon.spigui.buttons.SGButton;
import dev.mrflyn.replayaddon.versionutils.ItemBuilder;
import dev.mrflyn.replayaddon.versionutils.Util;
import me.jumper251.replay.api.IReplayHook;
import me.jumper251.replay.api.Monitor;
import me.jumper251.replay.replaysystem.data.ActionData;
import me.jumper251.replay.replaysystem.data.ActionType;
import me.jumper251.replay.replaysystem.data.types.PacketData;
import me.jumper251.replay.replaysystem.replaying.ReplayHelper;
import me.jumper251.replay.replaysystem.replaying.Replayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatTimelineHook implements IReplayHook, Monitor {
//40
    private final ChatTimeline parent;
    private int count;
    private final double seperation;
    private final double totalBoxes = 40.0;
    private int previousTime;
    private final HashMap<Integer, List<String>> deathTimeWithNames;
    public ChatTimelineHook(ChatTimeline parent){
        this.parent = parent;
        seperation = parent.getCache().getTotalDuration()/totalBoxes;
        deathTimeWithNames = new HashMap<>();
        for(String s : parent.getCache().getUUIDsWithDeathTimes().keySet()){
            String name = parent.getCache().getPlayerName(s);
            for (int t : parent.getCache().getUUIDsWithDeathTimes().get(s)) {
                if (!deathTimeWithNames.containsKey(t)) {
                    deathTimeWithNames.put(t, new ArrayList<>());
                }
                deathTimeWithNames.get(t).add(name);
            }
        }
    }


    @Override
    public List<PacketData> onRecord(String s) {
        return null;
    }

    @Override
    public void onPlay(ActionData actionData, Replayer replayer) {
        if(replayer.getCurrentTicks()==previousTime)return;
        previousTime = replayer.getCurrentTicks();
        if(replayer.getCurrentTicks()%20!=0)return;
        Util.debug(count++);
        if(actionData!=null&&actionData.getType()== ActionType.CUSTOM)return;
        if(!replayer.getReplay().getId().equals(parent.getCache().getReplayName()))return;
        if(!parent.isEnabled())return;
        parent.getWatcher().spigot().sendMessage(Util.getChatTimeLine(
                parent.getWatcher(),
                (int) totalBoxes,
                parent.getCache().getTotalDuration(),
                seperation,
                replayer.getCurrentTicks()/20,
                deathTimeWithNames));

    }
}
