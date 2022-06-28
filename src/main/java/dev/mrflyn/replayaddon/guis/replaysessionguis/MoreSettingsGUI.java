package dev.mrflyn.replayaddon.guis.replaysessionguis;

import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayHandler;
import dev.mrflyn.replayaddon.guis.CustomReplaySessionSettings;
import dev.mrflyn.replayaddon.ReplayAddonMain;

import dev.mrflyn.replayaddon.spigui.SGMenu;
import dev.mrflyn.replayaddon.spigui.buttons.SGButton;
import dev.mrflyn.replayaddon.versionutils.ItemBuilder;
import dev.mrflyn.replayaddon.versionutils.XMaterial;
import me.jumper251.replay.replaysystem.replaying.ReplayHelper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MoreSettingsGUI extends SGMenu {
public static MoreSettingsGUI INSTANCE = new MoreSettingsGUI();

private Inventory cachedInv;

    public MoreSettingsGUI() {
        super(ReplayAddonMain.plugin,ReplayAddonMain.plugin.spiGUI, "More Settings", 3, null);
        setAutomaticPaginationEnabled(false);
        setIcons();
        cachedInv = getInventory();
    }

    public void updateGUI(){
        setIcons();
        for (HumanEntity viewer : cachedInv.getViewers()){
            refreshInventory(viewer);
        }
        cachedInv = getInventory();
    }

    private void setIcons(){
            //10,12,14,16
        SGButton viewerSettings = new SGButton(
                new ItemBuilder(XMaterial.OAK_SIGN.parseMaterial()).name(ChatColor.GREEN + "Viewer Settings")
                        .lore(ChatColor.GRAY+"Viewer settings for this recording.","",ChatColor.YELLOW+"Click to view!").build())
                .withListener((e) -> {
                    Player p = (Player) e.getWhoClicked();
                    if (!GameReplayHandler.playingReplays.containsKey(p)) return;
                    p.openInventory(GameReplayHandler.playingReplays.get(p).getViewerSettingsGUI().getInventory());
                });
        SGButton bookMarks = new SGButton(
                new ItemBuilder(XMaterial.BOOK.parseMaterial()).name(ChatColor.GREEN + "Bookmarks")
                        .lore(ChatColor.GRAY+"View bookmarks for this recording.","",ChatColor.YELLOW+"Click to view!").build())
                .withListener((e) -> {
                    Player p = (Player) e.getWhoClicked();
                    if (!GameReplayHandler.playingReplays.containsKey(p)) return;
                    CustomReplaySessionSettings settings = GameReplayHandler.playingReplays.get(p);
                        if(BookMarksGUI.replayIDBookmarks.containsKey(settings.getReplayID())){
                            p.openInventory(BookMarksGUI.replayIDBookmarks.get(settings.getReplayID()).getCachedInventory());
                        }
                        else {
                            p.openInventory(new BookMarksGUI(settings).getCachedInventory());
                        }
                });
        SGButton shareReplay = new SGButton(
                new ItemBuilder(XMaterial.PAPER.parseMaterial()).name(ChatColor.GREEN + "Share Replay")
                        .lore(ChatColor.GRAY + "Share this recording.", "", ChatColor.YELLOW + "Click to get link!").build())
                .withListener((e) -> {

                    Player p = (Player) e.getWhoClicked();
                    if (!GameReplayHandler.playingReplays.containsKey(p)) return;
                    CustomReplaySessionSettings settings = GameReplayHandler.playingReplays.get(p);
                    ComponentBuilder message = new ComponentBuilder(ChatColor.GOLD + "" +
                            ChatColor.BOLD + "Click to share replay!");
                    BaseComponent[] msg =
                            message.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND
                                    , "/rp view "+settings.getReplayID()) ).create();
                    p.closeInventory();
                    p.spigot().sendMessage(msg);
                });
        SGButton leave = new SGButton(
                new ItemBuilder(XMaterial.DARK_OAK_DOOR.parseMaterial()).name(ChatColor.RED + "Leave")
                        .lore("",ChatColor.YELLOW+"Click to leave this replay!").build())
                .withListener((e) -> {
                    Player p = (Player) e.getWhoClicked();
                    if (!GameReplayHandler.playingReplays.containsKey(p)) return;
                    ReplayHelper.replaySessions.get(p.getName()).stop();
                });
        setButton(10, viewerSettings);
        setButton(12, bookMarks);
        setButton(14, shareReplay);
        setButton(16, leave);

    }

    public Inventory getCachedInventory(){
        return this.cachedInv;
    }
}
