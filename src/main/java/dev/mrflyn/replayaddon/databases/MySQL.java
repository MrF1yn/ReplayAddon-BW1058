package dev.mrflyn.replayaddon.databases;
import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.mrflyn.replayaddon.advancedreplayhook.GameReplayCache;
import dev.mrflyn.replayaddon.advancedreplayhook.ProxyData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import static dev.mrflyn.replayaddon.ReplayAddonMain.plugin;


public class MySQL implements IDatabase {
    Gson gson = new Gson();

    private HikariDataSource dataSource;
    private final String host;
    private final String database;
    private final String user;
    private final String pass;
    private final int port;
    private final boolean ssl;
    private final boolean certificateVerification;
    private final int poolSize;
    private final int maxLifetime;
    
    /**
     * Create new MySQL connection.
     */
    public MySQL() {
        this.host = plugin.dbConfig.getString("database.mysql.host");
        this.database = plugin.dbConfig.getString("database.mysql.database");
        this.user = plugin.dbConfig.getString("database.mysql.username");
        this.pass = plugin.dbConfig.getString("database.mysql.password");
        this.port = plugin.dbConfig.getInt("database.mysql.port");
        this.ssl = plugin.dbConfig.getBoolean("database.mysql.ssl");
        this.certificateVerification = plugin.dbConfig.getBoolean("database.mysql.verify-certificate", true);
        this.poolSize = plugin.dbConfig.getInt("database.mysql.pool-size", 10);
        this.maxLifetime = plugin.dbConfig.getInt("database.mysql.max-lifetime", 1800);
    }

    @Override
    public String name() {
        return "MySQL";
    }

    /**
     * Creates the SQL connection pool and tries to connect.
     *
     * @return true if connected successfully.
     */
    public boolean connect() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setPoolName("ReplayAddonBW1058MySQLPool");

        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setMaxLifetime(maxLifetime * 1000L);

        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);

        hikariConfig.setUsername(user);
        hikariConfig.setPassword(pass);

        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(ssl));
        if (!certificateVerification) {
            hikariConfig.addDataSourceProperty("verifyServerCertificate", String.valueOf(false));
        }

        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");
        hikariConfig.addDataSourceProperty("useUnicode", "true");

        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("jdbcCompliantTruncation", "false");

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "275");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // Recover if connection gets interrupted
        hikariConfig.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));

        dataSource = new HikariDataSource(hikariConfig);
        try {
            dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    @Override
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS game_replay (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "replay_id VARCHAR(200), replay_data LONGTEXT);";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
            sql = "CREATE TABLE IF NOT EXISTS proxy_mode_data (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "uuid VARCHAR(200), username VARCHAR(200), proxy_data LONGTEXT);";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
            sql = "DELETE FROM proxy_mode_data;";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
            sql = "CREATE TABLE IF NOT EXISTS player_data (id SERIAL PRIMARY KEY, " +
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
        try (Connection connection = dataSource.getConnection()) {
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
        String sql = "SELECT * FROM game_replay WHERE LOCATE(?, replay_data)!=0;";
        try (Connection connection = dataSource.getConnection()) {
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
        try (Connection connection = dataSource.getConnection()) {
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
        try (Connection connection = dataSource.getConnection()) {
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
        try (Connection connection = dataSource.getConnection()) {
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
        try (Connection connection = dataSource.getConnection()) {
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
        try (Connection connection = dataSource.getConnection()) {
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
        try (Connection connection = dataSource.getConnection()) {
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
        try (Connection connection = dataSource.getConnection()) {
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
        try (Connection connection = dataSource.getConnection()) {
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
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, data.getUUID());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

