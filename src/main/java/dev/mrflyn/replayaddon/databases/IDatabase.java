package dev.mrflyn.replayaddon.databases;

import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayCache;
import dev.mrflyn.replayaddon.advancedreplayhook.ProxyData;

import java.util.List;
import java.util.UUID;

public interface IDatabase {

    String name();

    boolean connect();

    void init();

    void saveGameReplayCache(GameReplayCache cache);

    List<GameReplayCache> getGameReplayCaches(String player);

    GameReplayCache getGameReplayCache(String id);

    String getPlayerLanguage(UUID uuid);

    String getPlayerLanguage(String userName);

    void createPlayerLanguage(UUID uuid,String userName, String language);

    void updatePlayerLanguage(UUID uuid, String language);

    ProxyData getProxyData(UUID uuid);

    ProxyData getProxyData(String userName);

    void setProxyData(ProxyData data);

    void deleteProxyData(ProxyData data);

}
