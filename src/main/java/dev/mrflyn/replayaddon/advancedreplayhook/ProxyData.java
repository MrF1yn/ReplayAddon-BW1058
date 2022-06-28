package dev.mrflyn.replayaddon.advancedreplayhook;

public class ProxyData {

    private final String uuid;
    private final String name;
    private final String replayID;
    private final String lobbyName;
    private final Integer startTime;
    private final Double x;
    private final Double y;
    private final Double z;

    public ProxyData(String uuid, String name, String replayID, String lobbyName, Integer startTime, Double x, Double y, Double z) {
        this.uuid = uuid;
        this.name = name;
        this.replayID = replayID;
        this.lobbyName = lobbyName;
        this.startTime = startTime;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getReplayID() {
        return replayID;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }
}
