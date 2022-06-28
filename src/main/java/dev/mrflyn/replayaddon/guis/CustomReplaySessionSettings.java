package dev.mrflyn.replayaddon.guis;

import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayHandler;
import dev.mrflyn.replayaddon.guis.replaysessionguis.ViewerSettingsGUI;
import me.jumper251.replay.replaysystem.replaying.ReplayHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CustomReplaySessionSettings {
    private final Player watcher;
    private final String replayID;
    private boolean chatTimeline;
    private boolean showSpectators;
    private float flySpeed;
    private ChatTimeline chatTimelineHandler;
    private ViewerSettingsGUI  viewerSettingsGUI;

    public CustomReplaySessionSettings(Player watcher, String replayID){
        this.watcher = watcher;
        this.replayID = replayID;
        this.chatTimeline = false;
        this.showSpectators = false;
        this.flySpeed = 1;
        this.viewerSettingsGUI = new ViewerSettingsGUI(watcher, this);
        this.chatTimelineHandler = new ChatTimeline(watcher,
                ReplayHelper.replaySessions.get(watcher.getName()),
                GameReplayHandler.replayCacheID.get(replayID));

    }

    public Player getWatcher() {
        return watcher;
    }

    public String getReplayID() {
        return replayID;
    }

    public boolean isChatTimeline() {
        return chatTimeline;
    }

    public void setChatTimeline(boolean chatTimeline) {
        this.chatTimeline = chatTimeline;
        chatTimelineHandler.setEnabled(chatTimeline);

    }

    public boolean isShowSpectators() {
        return showSpectators;
    }

    public void setShowSpectators(boolean showSpectators) {
        this.showSpectators = showSpectators;
    }

    public float getFlySpeed() {
        return flySpeed;
    }

    public void setFlySpeed(float flySpeed) {
        this.flySpeed = flySpeed;
        Bukkit.getScheduler().runTask(ReplayAddonMain.plugin, ()-> {
            watcher.setFlySpeed(flySpeed / 10F);
        });
    }

    public ViewerSettingsGUI getViewerSettingsGUI() {
        return viewerSettingsGUI;
    }

    public ChatTimeline getChatTimelineHandler() {
        return chatTimelineHandler;
    }
}
