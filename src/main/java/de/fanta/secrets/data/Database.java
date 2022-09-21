package de.fanta.secrets.data;

import de.fanta.secrets.SecretEntry;
import de.fanta.secrets.Secrets;
import de.iani.cubesideutils.sql.MySQLConnection;
import de.iani.cubesideutils.sql.SQLConfig;
import de.iani.cubesideutils.sql.SQLConnection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private final SQLConnection connection;
    private final SQLConfig config;
    private final Secrets plugin;
    private final String getSecretsQuery;
    private final String getPlayerSecretsQuery;
    private final String insertSecretQuery;
    private final String insertPlayerSecretQuery;

    public Database(SQLConfig config, SecretsConfig secretsConfig, Secrets plugin) {
        String url = null;
        this.config = config;
        this.plugin = plugin;

        try {
            if (!secretsConfig.useMysqlDatabase()) {
                Class.forName("org.h2.Driver");
                url = "jdbc:h2:./" + config.getDatabase();
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find the driver in the classpath!", e);
        }

        try {
            if (url == null) {
                this.connection = new MySQLConnection(config.getHost(), config.getDatabase(), config.getUser(), config.getPassword());
            } else {
                this.connection = new MySQLConnection(url, null, null, null);
            }

            createTablesIfNotExist();
        } catch (SQLException e) {
            throw new RuntimeException("Could not initialize database", e);
        }

        getSecretsQuery = "SELECT * FROM " + config.getTablePrefix() + "_secrets";
        getPlayerSecretsQuery = "SELECT * FROM " + config.getTablePrefix() + "_player" + " WHERE uuid = ?";
        insertSecretQuery = "INSERT INTO " + config.getTablePrefix() + "_secrets" + " (secret, server, world, x, y, z) VALUE (?, ?, ?, ?, ?, ?)";
        insertPlayerSecretQuery = "INSERT INTO " + config.getTablePrefix() + "_player" + " (uuid, secret) VALUE (?, ?)";
    }


    private void createTablesIfNotExist() throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            Statement smt = connection.createStatement();
            smt.executeUpdate("CREATE TABLE IF NOT EXISTS " + config.getTablePrefix() + "_player" + " (" +
                    "`uuid` char(36)," +
                    "`secret` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin," +
                    "PRIMARY KEY (`uuid`, `secret`)" +
                    ")");
            smt.executeUpdate("CREATE TABLE IF NOT EXISTS " + config.getTablePrefix() + "_secrets" + " (" +
                    "`secret` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin," +
                    "`server` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin," +
                    "`world` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin," +
                    "`x` BIGINT," +
                    "`y` BIGINT," +
                    "`z` BIGINT," +
                    "PRIMARY KEY (`secret`)" +
                    ")");
            smt.close();
            return null;
        });
    }

    public List<SecretEntry> loadSecrets() throws SQLException {
        List<SecretEntry> shopInfos = new ArrayList<>();
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement statement = sqlConnection.getOrCreateStatement(getSecretsQuery);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String secretName = rs.getString(1);
                String serverName = rs.getString(2);
                String worldName = rs.getString(3);
                long x = rs.getLong(4);
                long y = rs.getLong(5);
                long z = rs.getLong(6);

                SecretEntry secretEntry = new SecretEntry(secretName, serverName, worldName, new Location(null, x, y, z));
                shopInfos.add(secretEntry);
            }
            return null;
        });
        return shopInfos;
    }

    public List<SecretEntry> loadSecretsbyPlayer(Player player) throws SQLException {
        List<SecretEntry> secretEntries = new ArrayList<>();
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement statement = sqlConnection.getOrCreateStatement(getPlayerSecretsQuery);
            statement.setString(1, player.getUniqueId().toString());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String secretName = rs.getString(2);

                for (SecretEntry secretEntry : plugin.getSecretEntries()) {
                    if (secretEntry.getSecretName().equals(secretName)) {
                        secretEntries.add(secretEntry);
                        break;
                    }
                }
            }
            return null;
        });
        return secretEntries;
    }

    public void insertSecret(String secretName, String serverName, String worldName, Location location) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(insertSecretQuery);

            smt.setString(1, secretName);
            smt.setString(2, serverName);
            smt.setString(3, worldName);
            smt.setLong(4, location.getBlockX());
            smt.setLong(5, location.getBlockY());
            smt.setLong(6, location.getBlockZ());

            smt.executeUpdate();
            return null;
        });
    }

    public void insertPlayerSecret(Player player, String secretName) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(insertPlayerSecretQuery);

            smt.setString(1, player.getUniqueId().toString());
            smt.setString(2, secretName);

            smt.executeUpdate();
            return null;
        });
    }
}
