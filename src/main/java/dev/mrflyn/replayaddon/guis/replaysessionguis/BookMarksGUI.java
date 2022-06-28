package dev.mrflyn.replayaddon.guis.replaysessionguis;


import dev.mrflyn.replayaddon.configs.Messages;
import dev.mrflyn.replayaddon.guis.CustomReplaySessionSettings;
import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayCache;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayHandler;
import dev.mrflyn.replayaddon.spigui.SGMenu;
import dev.mrflyn.replayaddon.spigui.buttons.SGButton;
import dev.mrflyn.replayaddon.versionutils.ItemBuilder;
import dev.mrflyn.replayaddon.versionutils.Util;
import dev.mrflyn.replayaddon.versionutils.XMaterial;
import me.jumper251.replay.replaysystem.replaying.ReplayHelper;
import me.jumper251.replay.replaysystem.replaying.Replayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;


public class BookMarksGUI extends SGMenu {
    private final CustomReplaySessionSettings settings;
    private Inventory cachedInv;
    public static HashMap<String, BookMarksGUI>replayIDBookmarks =new HashMap<>();
    DecimalFormat format = new DecimalFormat("#.##");


    public BookMarksGUI(CustomReplaySessionSettings settings) {
        super(ReplayAddonMain.plugin,ReplayAddonMain.plugin.spiGUI, Util.translateLang(Messages.BOOKMARK_GUI_TITLE, settings.getWatcher()), 5, null);
        setAutomaticPaginationEnabled(true);
        this.settings = settings;
        SGButton button = new SGButton(new ItemBuilder(XMaterial.matchXMaterial(Util.translateLang(Messages.GUI_FILLER_MATERIAL, settings.getWatcher())).orElse(XMaterial.BLACK_STAINED_GLASS_PANE)
                .parseItem())
                .name(Util.translateLang(Messages.GUI_FILLER_DISPLAYNAME, settings.getWatcher()))
                .lore(Util.translateLangList(Messages.GUI_FILLER_LORE, settings.getWatcher()))
                .build());
        for(int i = 0; i<45;i++){
            if(i<9){
                setButton(i, button);
                stickSlot(i);
            }
            else if(i%9==0&&i<36){
                setButton(i, button);
                setButton(i+8, button);
                stickSlot(i);
                stickSlot(i+8);
            }
            else if(i>35){
                setButton(i, button);
                stickSlot(i);
            }
        }
        SGButton back= new SGButton(new ItemBuilder(
                XMaterial.matchXMaterial(Util.translateLang(Messages.GUI_BACK_MATERIAL, settings.getWatcher()))
                        .orElse(XMaterial.ARROW).parseItem())
                .name(Util.translateLang(Messages.GUI_BACK_DISPLAYNAME, settings.getWatcher()))
                .lore(Util.translateLangList(Messages.GUI_BACK_LORE, settings.getWatcher()))
                .build())
                .withListener((e) -> {
                    Player p = (Player) e.getWhoClicked();
                    if (!GameReplayHandler.playingReplays.containsKey(p)) return;
                    p.openInventory(MoreSettingsGUI.INSTANCE.getCachedInventory());
                });
        setButton(40, back);
        setIcons();
        cachedInv = getInventory();
        replayIDBookmarks.put(settings.getReplayID(), this);
    }

    public Inventory getCachedInventory(){
        return this.cachedInv;
    }

    public void updateGUI(){
        setIcons();
        for (HumanEntity viewer : cachedInv.getViewers()){
            refreshInventory(viewer);
        }
        cachedInv = getInventory();
    }


    private void setIcons(){
        this.clearAllButStickiedSlots();
        GameReplayCache cache = GameReplayHandler.replayCacheID.get(settings.getReplayID());
        for(String s : cache.getUUIDsWithDeathTimes().keySet()){
            String name = cache.getPlayerName(s);
            

            for (int t : cache.getUUIDsWithDeathTimes().get(s)) {
                SGButton button = new SGButton(new ItemBuilder(XMaterial.matchXMaterial(Util.translateLang(Messages.BOOKMARK_ITEM_MATERIAL, settings.getWatcher()))
                        .orElse(XMaterial.PAPER)
                        .parseItem())
                        .name(Util.parseInternalPlaceholders(Util.translateLang(Messages.BOOKMARK_ITEM_DISPLAYNAME ,settings.getWatcher()),cache, settings.getWatcher()))
                        .lore(Util.parsePlayerInfoPlaceholders(
                                Util.parseInternalPlaceholders(Util.translateLangList(Messages.BOOKMARK_ITEM_LORE, settings.getWatcher()),cache,settings.getWatcher(),
                                        Util.formattedTime(t)),
                                cache.getPlayerInfo(UUID.fromString(s))))
                        .build())
                        .withListener((e) -> {
                            Player p = (Player) e.getWhoClicked();
                            if (!GameReplayHandler.playingReplays.containsKey(p)) return;
                            if (!ReplayHelper.replaySessions.containsKey(p.getName())) return;
                            Replayer replayer = ReplayHelper.replaySessions.get(p.getName());
                            Util.setTime(replayer, t-2);
                            Location loc = replayer.getNPCList().get(name).getLocation().clone();
                            if(e.getClick() == ClickType.LEFT){
                                p.setFlying(true);
                                Bukkit.getScheduler().runTaskLater(ReplayAddonMain.plugin, ()->{
                                    p.teleport(Util.lookAt(loc.clone().add(3,1,3), loc));
                                }, 2L);
                                p.sendMessage(ChatColor.GREEN+"Skipped to "+Util.formattedTime(t-2)+" seconds.");
                            }else if(e.getClick() == ClickType.RIGHT){
                                ComponentBuilder message = new ComponentBuilder(Util.translateLang(Messages.BOOKMARK_CLICK_MSG, settings.getWatcher()));
                                BaseComponent[] msg =
                                        message.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                                "/rp view "+settings.getReplayID()+" "+(t-2)+" "+format.format(loc.getX())+
                                                        ":"+format.format(loc.getY())+":"+format.format(loc.getZ())
                                        )).create();
                                p.closeInventory();
                                p.spigot().sendMessage(msg);

                            }
                        });
                addButton(button);
            }
        }
    }





}
