package dev.mrflyn.replayaddon.databases;


import com.google.gson.Gson;
import dev.mrflyn.replayaddon.ReplayAddonMain;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayCache;
import dev.mrflyn.replayaddon.advancedreplayhook.ProxyData;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLite implements IDatabase {
    Gson gson = new Gson();
    private String url;

    private Connection connection;

    @Override
    public String name() {
        return "SQLite";
    }

    @Override
    public boolean connect() {
        File folder = new File(ReplayAddonMain.plugin.getDataFolder() + "/Cache");
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                ReplayAddonMain.plugin.getLogger().severe("Could not create /Cache folder!");
            }
        }
        File dataFolder = new File(folder.getPath() + "/cache.db");
        if (!dataFolder.exists()) {
            try {
                if (!dataFolder.createNewFile()) {
                    ReplayAddonMain.plugin.getLogger().severe("Could not create /Cache/cache.db file!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        this.url = "jdbc:sqlite:" + dataFolder;
        try {
            Class.forName("org.sqlite.JDBC");
            DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            if (e instanceof ClassNotFoundException) {
                ReplayAddonMain.plugin.getLogger().severe("Could Not Found SQLite Driver on your system!");
            }
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void init() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS game_replay (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "replay_id VARCHAR(200), replay_data TEXT);";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
            sql = "CREATE TABLE IF NOT EXISTS proxy_mode_data (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "uuid VARCHAR(200), username VARCHAR(200), proxy_data TEXT);";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
            sql = "DELETE FROM proxy_mode_data;";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
            sql = "CREATE TABLE IF NOT EXISTS player_data (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uuid VARCHAR(200), username VARCHAR(200), language VARCHAR(200) NOT NULL DEFAULT 'en');";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void saveGameReplayCache(GameReplayCache cache) {
        String sql = "INSERT INTO game_replay (replay_id, replay_data) VALUES (?, ?);";
        try  {
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, cache.getReplayName());
                statement.setString(2, gson.toJson(cache).toString());
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<GameReplayCache> getGameReplayCaches(String player) {
        List<GameReplayCache> caches = new ArrayList<>();
        String sql = "SELECT * FROM game_replay WHERE INSTR(replay_data, ?)!=0;";
        try  {
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, player);
                ResultSet result = statement.executeQuery();
                while (result.next()){
                    caches.add(gson.fromJson(result.getString("replay_data"), GameReplayCache.class));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return caches;
    }
    @Override
    public GameReplayCache getGameReplayCache(String id) {
        GameReplayCache cache = null;
        String sql = "SELECT * FROM game_replay WHERE replay_id=?;";
        try  {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, id);
                ResultSet result = statement.executeQuery();
                if(result.next()){
                    cache = gson.fromJson(result.getString("replay_data"), GameReplayCache.class);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cache;
    }

    @Override
    public String getPlayerLanguage(UUID uuid) {
        String sql = "SELECT * FROM player_data WHERE uuid=?;";
        try  {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return result.getString("language");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getPlayerLanguage(String userName) {
        String sql = "SELECT * FROM player_data WHERE username=?;";
        try  {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, userName);
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return result.getString("language");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updatePlayerLanguage(UUID uuid, String language) {
        String sql = "UPDATE player_data SET language=? WHERE uuid=?;";
        try  {
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, language);
                statement.setString(2, uuid.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createPlayerLanguage(UUID uuid,String userName, String language) {
        String sql = "INSERT INTO player_data (uuid, username, language) VALUES (?, ?, ?);";
        try  {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                statement.setString(2, userName);
                statement.setString(3, language);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public ProxyData getProxyData(UUID uuid) {
        String sql = "SELECT * FROM proxy_mode_data WHERE uuid=?;";
        try  {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return gson.fromJson(result.getString("proxy_data"), ProxyData.class);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ProxyData getProxyData(String userName) {
        String sql = "SELECT * FROM proxy_mode_data WHERE username=?;";
        try  {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, userName);
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return gson.fromJson(result.getString("proxy_data"), ProxyData.class);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setProxyData(ProxyData data) {
        String sql = "INSERT INTO proxy_mode_data (uuid, username, proxy_data) VALUES (?, ?, ?);";
        try  {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, data.getUUID());
                statement.setString(2, data.getName());
                statement.setString(3, gson.toJson(data));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteProxyData(ProxyData data) {
        String sql = "DELETE FROM proxy_mode_data WHERE uuid=?;";
        try  {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, data.getUUID());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
