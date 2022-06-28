package dev.mrflyn.replayaddon.guis;

import org.bukkit.inventory.ItemStack;

public class Button {
    public ItemStack itemStack;
    public int slot;
    public String type;

    public Button(ItemStack stack,String type, int slot){
        this.itemStack = stack;
        this.slot = slot;
        this.type = type;
    }



}
