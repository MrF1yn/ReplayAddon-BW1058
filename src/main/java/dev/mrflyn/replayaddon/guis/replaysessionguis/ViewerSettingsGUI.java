package dev.mrflyn.replayaddon.guis.replaysessionguis;

import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayHandler;
import dev.mrflyn.replayaddon.guis.CustomReplaySessionSettings;
import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.spigui.SGMenu;
import dev.mrflyn.replayaddon.spigui.buttons.SGButton;
import dev.mrflyn.replayaddon.versionutils.ItemBuilder;
import dev.mrflyn.replayaddon.versionutils.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ViewerSettingsGUI extends SGMenu {
    private final Player player;
    private final CustomReplaySessionSettings settings;
    public ViewerSettingsGUI(Player p, CustomReplaySessionSettings settings) {
        super(ReplayAddonMain.plugin,ReplayAddonMain.plugin.spiGUI, "Viewer Settings", 4, null);
        setAutomaticPaginationEnabled(false);
        this.player = p;
        this.settings = settings;
        setIcons();
    }

    private void setIcons(){
        //12,13,14,31
        ItemBuilder chatTimeline = new ItemBuilder(XMaterial.GRAY_DYE.parseItem())
                .name(ChatColor.RED + "Chat Timeline").lore(ChatColor.YELLOW + "Click to enable!");
        NBTItem nChatTimeLine = new NBTItem(chatTimeline.build());
        nChatTimeLine.setString("replay-addon", "disabled");
        SGButton iChatTimeline = new SGButton(nChatTimeLine.getItem())
                .withListener((e) ->
                {
                    NBTItem ct = null;
                    ItemStack item;
                    if(!settings.isChatTimeline()){
                        item = new ItemBuilder(XMaterial.LIME_DYE.parseItem())
                                .name(ChatColor.GREEN + "Chat Timeline").lore(ChatColor.YELLOW + "Click to disable!").build();
                        this.settings.setChatTimeline(true);
                        ct = new NBTItem(item);
                        ct.setString("replay-addon", "enabled");
                    }else{
                        item = new ItemBuilder(XMaterial.GRAY_DYE.parseItem())
                                .name(ChatColor.RED + "Chat Timeline").lore(ChatColor.YELLOW + "Click to enable!").build();
                        this.settings.setChatTimeline(false);
                        ct = new NBTItem(item);
                        ct.setString("replay-addon", "disabled");
                    }
                    getButton(12).setIcon(item);
                    refreshInventory(e.getWhoClicked());
                });
        ItemBuilder showSpectators = new ItemBuilder(XMaterial.GRAY_DYE.parseItem())
                .name(ChatColor.RED + "Show Spectators").lore(ChatColor.YELLOW + "Click to enable!");
        NBTItem nShowSpectators = new NBTItem(showSpectators.build());
        nShowSpectators.setString("replay-addon", "disabled");
        SGButton iShowSpectators = new SGButton(nShowSpectators.getItem())
                .withListener((e) ->
                {
                    NBTItem ct = null;
                    ItemStack item;
                    if (!settings.isShowSpectators()) {
                        item = new ItemBuilder(XMaterial.LIME_DYE.parseItem())
                                .name(ChatColor.GREEN + "Show Spectators").lore(ChatColor.YELLOW + "Click to disable!").build();
                        this.settings.setShowSpectators(true);
                        ct = new NBTItem(item);
                        ct.setString("replay-addon", "enabled");
                    } else {
                        item = new ItemBuilder(XMaterial.GRAY_DYE.parseItem())
                                .name(ChatColor.RED + "Show Spectators").lore(ChatColor.YELLOW + "Click to enable!").build();
                        this.settings.setShowSpectators(false);
                        ct = new NBTItem(item);
                        ct.setString("replay-addon", "disabled");
                    }
                    getButton(13).setIcon(item);
                    refreshInventory(e.getWhoClicked());
                });
        ItemBuilder flySpeed = new ItemBuilder(XMaterial.PAPER.parseItem())
                .name(ChatColor.RED + "Fly Speed")
                .lore(ChatColor.GREEN + "Currently Selected: "+ChatColor.GOLD+"1x",
                        "",
                        ChatColor.GRAY+"Click to set Fly Speed to "+ChatColor.GOLD+"2x.",
                        "",
                        ChatColor.YELLOW+"Click to cycle!"
                        );
        NBTItem nFlySpeed = new NBTItem(flySpeed.build());
        nFlySpeed.setInteger("replay-addon", 1);
        this.settings.setFlySpeed(1);
        SGButton iFlySpeed = new SGButton(nFlySpeed.getItem())
                .withListener((e) ->
                {
                    NBTItem ct = null;
                    ItemStack item = null;
                    float speed;
                    if(this.settings.getFlySpeed()<5&&this.settings.getFlySpeed()>=1) {
                        speed = this.settings.getFlySpeed() + 1;
                        float nxtSpeed = this.settings.getFlySpeed()+2;
                        if(nxtSpeed>5)nxtSpeed=1;
                        item = new ItemBuilder(XMaterial.PAPER.parseItem())
                                .name(ChatColor.RED + "Fly Speed")
                                .lore(ChatColor.GREEN + "Currently Selected: "+ChatColor.GOLD+(int)speed+"x.",
                                        "",
                                        ChatColor.GRAY+"Click to set Fly Speed to "+ChatColor.GOLD+(int)nxtSpeed+"x.",
                                        "",
                                        ChatColor.YELLOW+"Click to cycle!"
                                ).build();
                        NBTItem item1= new NBTItem(item);
                        item1.setFloat("replay-addon", speed);
                    }
                    else if(this.settings.getFlySpeed()==5){
                        speed = 0.5F;
                        item = new ItemBuilder(XMaterial.PAPER.parseItem())
                                .name(ChatColor.RED + "Fly Speed")
                                .lore(ChatColor.GREEN + "Currently Selected: "+ChatColor.GOLD+speed+"x.",
                                        "",
                                        ChatColor.GRAY+"Click to set Fly Speed to "+ChatColor.GOLD+"1x.",
                                        "",
                                        ChatColor.YELLOW+"Click to cycle!"
                                ).build();
                        NBTItem item1= new NBTItem(item);
                        item1.setFloat("replay-addon", speed);
                    }else if(this.settings.getFlySpeed()==0.5){
                        speed = 1;
                        item = new ItemBuilder(XMaterial.PAPER.parseItem())
                                .name(ChatColor.RED + "Fly Speed")
                                .lore(ChatColor.GREEN + "Currently Selected: "+ChatColor.GOLD+speed+"x.",
                                        "",
                                        ChatColor.GRAY+"Click to set Fly Speed to "+ChatColor.GOLD+"1x.",
                                        "",
                                        ChatColor.YELLOW+"Click to cycle!"
                                ).build();
                        NBTItem item1= new NBTItem(item);
                        item1.setFloat("replay-addon", speed);
                    }
                    else {
                        speed = 1;
                        item = new ItemBuilder(XMaterial.PAPER.parseItem())
                                .name(ChatColor.RED + "Fly Speed")
                                .lore(ChatColor.GREEN + "Currently Selected: "+ChatColor.GOLD+speed+"x.",
                                        "",
                                        ChatColor.GRAY+"Click to set Fly Speed to "+ChatColor.GOLD+(speed+1)+"x.",
                                        "",
                                        ChatColor.YELLOW+"Click to cycle!"
                                ).build();
                        NBTItem item1= new NBTItem(item);
                        item1.setFloat("replay-addon", speed);
                    }
                    this.settings.setFlySpeed(speed);
                    getButton(14).setIcon(item);
                    refreshInventory(e.getWhoClicked());
                });
        SGButton back= new SGButton(new ItemBuilder(Material.ARROW).name(ChatColor.GREEN+"Back to Main Menu.")
                .build())
                .withListener((e) -> {
                    Player p = (Player) e.getWhoClicked();
                    if (!GameReplayHandler.playingReplays.containsKey(p)) return;
                    p.openInventory(MoreSettingsGUI.INSTANCE.getCachedInventory());
                });
            setButton(12, iChatTimeline);
            setButton(13, iShowSpectators);
            setButton(14, iFlySpeed);
            setButton(31, back);
    }





}
