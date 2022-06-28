package dev.mrflyn.replayaddon.advancedreplayhook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GameReplayCache {
    //Name:UUID
    private HashMap<String, PlayerInfo> playersWithNames = new HashMap<>();
    private HashMap<UUID, PlayerInfo> playersWithUUIDs = new HashMap<>();
    //UUID -> DeathTimes(SS)
    private HashMap<String, List<Integer>> UUIDsWithDeathTimes;
    private List<String> spectators;
    //SS
    private int totalDuration;
    private String mapName = "";
    //YY-MM-DD (TIME(MM:SS))
    private String date;
    private String gameMode;
    private String replayName;

    public GameReplayCache(List<PlayerInfo> players, String mapName,String gameMode, String date, String replayName){
        this.date = date;
        this.gameMode = gameMode;
        this.mapName = mapName;
        for(PlayerInfo player : players){
            playersWithUUIDs.put(player.getUuid(), player);
            playersWithNames.put(player.getName(), player);
        }
        this.UUIDsWithDeathTimes = new HashMap<>();
        this.spectators = new ArrayList<>();
        this.replayName = replayName;
    }

    public String getPlayerName(String uuid){
        UUID uuid1 = UUID.fromString(uuid);
        if (playersWithUUIDs.containsKey(uuid1)){
            return playersWithUUIDs.get(uuid1).getName();
        }
        return "";
    }

    public PlayerInfo getPlayerInfo(UUID uuid){
        return playersWithUUIDs.get(uuid);
    }

    public PlayerInfo getPlayerInfo(String name) {
        return playersWithNames.get(name);
    }

    public String getReplayName() {
        return replayName;
    }

    public void setReplayName(String replayName) {
        this.replayName = replayName;
    }

    public List<UUID> getPlayersWithUUIDs() {
        return new ArrayList<>(playersWithUUIDs.keySet());
    }


    public HashMap<String, List<Integer>> getUUIDsWithDeathTimes() {
        return UUIDsWithDeathTimes;
    }

    public void setUUIDsWithDeathTimes(HashMap<String, List<Integer>> UUIDsWithDeathTimes) {
        this.UUIDsWithDeathTimes = UUIDsWithDeathTimes;
    }

    public List<String> getSpectators() {
        return spectators;
    }

    public void setSpectators(List<String> spectators) {
        this.spectators = spectators;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }
}
