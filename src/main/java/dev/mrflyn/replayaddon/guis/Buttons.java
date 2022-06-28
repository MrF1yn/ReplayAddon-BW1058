package dev.mrflyn.replayaddon.guis;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Buttons {
    HB_TP_PLAYER(0, Material.COMPASS, "&aTeleport to Player", null, null),
    HB_DCR_SPD(2, Material.matchMaterial("SKULL_ITEM"), "&aDecrease Speed", Collections.singletonList("&aClick to decrease speed."),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQ2YjEyOTNkYjcyOWQwMTBmNTM0Y2UxMzYxYmJjNTVhZTVhOGM4ZjgzYTE5NDdhZmU3YTg2NzMyZWZjMiJ9fX0="),
    HB_10s_BWD(3, Material.matchMaterial("SKULL_ITEM"), "&a10s Backwards",null,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMzMDFhMTdjOTU1ODA3ZDg5ZjljNzJhMTkyMDdkMTM5M2I4YzU4YzRlNmU0MjBmNzE0ZjY5NmE4N2ZkZCJ9fX0="),
    HB_PLAY(4, Material.matchMaterial("SKULL_ITEM"), "&aPlay", Collections.singletonList("&aClick to play."),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDgzNDhhYTc3ZjlmYjJiOTFlZWY2NjJiNWM4MWI1Y2EzMzVkZGVlMWI5MDVmM2E4YjkyMDk1ZDBhMWYxNDEifX19"),
    HB_PAUSE(4, Material.matchMaterial("SKULL_ITEM"), "&aPause", Collections.singletonList("&aClick to pause."),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDgzNDhhYTc3ZjlmYjJiOTFlZWY2NjJiNWM4MWI1Y2EzMzVkZGVlMWI5MDVmM2E4YjkyMDk1ZDBhMWYxNDEifX19"),
    HB_10s_FWD(5, Material.matchMaterial("SKULL_ITEM"), "&a10s Forwards", null,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjU0ZmFiYjE2NjRiOGI0ZDhkYjI4ODk0NzZjNmZlZGRiYjQ1MDVlYmE0Mjg3OGM2NTNhNWQ3OTNmNzE5YjE2In19fQ=="),
    HB_INC_SPD(6, Material.matchMaterial("SKULL_ITEM"), "&aIncrease Speed", Collections.singletonList("&aClick to increase speed."),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMGZjNmRjZjczOWMxMWZlY2U0M2NkZDE4NGRlYTc5MWNmNzU3YmY3YmQ5MTUzNmZkYmM5NmZhNDdhY2ZiIn19fQ=="),
    HB_MORE_SETT(8, Material.NETHER_STAR, "&aMore Settings", null, null);



    private final Material material;
    private final String displayName, skin, data;
    private final List<String> lore;
    private final int slot;

    Buttons(int slot, Material m,String name, List<String> lore, String skin){
        this.slot = slot;
        this.material=m;
        this.displayName = name;
        this.lore = lore==null?new ArrayList<>():lore;
        this.skin = skin;
        this.data = this.name();
    }

    public int slot(){
        return this.slot;
    }

    public Material material() {
        return this.material;
    }

    public String displayName() {
        return this.displayName;
    }

    public String skin() {
        return this.skin;
    }

    public List<String> lore() {
        return this.lore;
    }

    public String data(){
        return this.data;
    }



}
