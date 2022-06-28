package dev.mrflyn.replayaddon.guis;

import dev.mrflyn.replayaddon.advancedreplayhook.ChatTimelineHook;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayCache;
import dev.mrflyn.replayaddon.versionutils.Util;
import me.jumper251.replay.api.ReplayAPI;
import me.jumper251.replay.replaysystem.replaying.Replayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ChatTimeline {
    private boolean enabled;
    private Player watcher;
    private ChatTimelineHook hook;
    private Replayer replayer;
    private GameReplayCache cache;

    public ChatTimeline(Player watcher, Replayer replayer, GameReplayCache cache){
        this.enabled = false;
        this.watcher = watcher;
        this.replayer = replayer;
        this.cache = cache;
        this.hook = new ChatTimelineHook(this);
        ReplayAPI.getInstance().getHookManager().registerHook(this.hook);
        Util.debug("ReplayHook registered for: "+cache.getReplayName());
    }

    public void terminate(){
        ReplayAPI.getInstance().getHookManager().unregisterHook(this.hook);
        Util.debug("ReplayHook terminated for: "+cache.getReplayName());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Player getWatcher() {
        return watcher;
    }

    public ChatTimelineHook getHook() {
        return hook;
    }

    public Replayer getReplayer() {
        return replayer;
    }

    public GameReplayCache getCache() {
        return cache;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
