package dev.mrflyn.replayaddon.advancedreplayhook;

import java.util.UUID;

public class PlayerInfo {

    private String name;
    private UUID uuid;
    private String tabPrefix;
    private String tabSuffix;
    private String tagPrefix;
    private String tagSuffix;

    public PlayerInfo(String name, UUID uuid, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix) {
        this.name = name;
        this.uuid = uuid;
        this.tabPrefix = tabPrefix;
        this.tabSuffix = tabSuffix;
        this.tagPrefix = tagPrefix;
        this.tagSuffix = tagSuffix;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getTabPrefix() {
        return tabPrefix;
    }

    public String getTabSuffix() {
        return tabSuffix;
    }

    public String getTagPrefix() {
        return tagPrefix;
    }

    public String getTagSuffix() {
        return tagSuffix;
    }
}
