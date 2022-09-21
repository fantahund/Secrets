package de.fanta.secrets.data;

import de.fanta.secrets.SecretEntry;
import de.fanta.secrets.Secrets;
import de.fanta.secrets.utils.ItemStackUtil;
import de.iani.cubesideutils.sql.MySQLConnection;
import de.iani.cubesideutils.sql.SQLConfig;
import de.iani.cubesideutils.sql.SQLConnection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class Database {

    private final SQLConnection connection;
    private final SQLConfig config;
    private final Secrets plugin;
    private final String getSecretsQuery;
    private final String getPlayerSecretsQuery;
    private final String insertSecretQuery;
    private final String insertPlayerSecretQuery;
    private final String updateSecretInventoryItemQuery;
    private final String updateSecretLocationQuery;

    public Database(SQLConfig config, SecretsConfig secretsConfig, Secrets plugin) {
        String url = null;
        this.config = config;
        this.plugin = plugin;

        try {
            if (!secretsConfig.useMysqlDatabase()) {
                Class.forName("org.h2.Driver");
                url = "jdbc:h2:./" + config.getDatabase();
            }
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Cannot find the driver in the classpath!", ex);
        }

        try {
            if (url == null) {
                this.connection = new MySQLConnection(config.getHost(), config.getDatabase(), config.getUser(), config.getPassword());
            } else {
                this.connection = new MySQLConnection(url, null, null, null);
            }

            createTablesIfNotExist();
        } catch (SQLException ex) {
            throw new RuntimeException("Could not initialize database", ex);
        }

        getSecretsQuery = "SELECT * FROM " + config.getTablePrefix() + "_secrets";
        getPlayerSecretsQuery = "SELECT * FROM " + config.getTablePrefix() + "_player" + " WHERE uuid = ?";
        insertSecretQuery = "INSERT INTO " + config.getTablePrefix() + "_secrets" + " (secret, displayitem, server, world, x, y, z) VALUE (?, ?, ?, ?, ?, ?, ?)";
        insertPlayerSecretQuery = "INSERT INTO " + config.getTablePrefix() + "_player" + " (uuid, secret) VALUE (?, ?)";
        updateSecretInventoryItemQuery = "UPDATE " + config.getTablePrefix() + "_secrets" + " SET `displayitem` = ? WHERE `secret` = ?";
        updateSecretLocationQuery = "UPDATE " + config.getTablePrefix() + "_secrets" + " SET `world` = ?, `x` = ?, `y` = ?, `z` = ?  WHERE `secret` = ?";
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
                    "`displayitem` LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin," +
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

    public HashMap<String, SecretEntry> loadSecrets() throws SQLException {
        HashMap<String, SecretEntry> secretEntryMap = new HashMap<>();
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement statement = sqlConnection.getOrCreateStatement(getSecretsQuery);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String secretName = rs.getString(1);
                String displayItemString = rs.getString(2);
                String serverName = rs.getString(3);
                String worldName = rs.getString(4);
                long x = rs.getLong(5);
                long y = rs.getLong(6);
                long z = rs.getLong(7);

                ItemStack displayItem = ItemStackUtil.getDisplayItemfromString(displayItemString);

                SecretEntry secretEntry = new SecretEntry(secretName, serverName, worldName, new Location(null, x, y, z), displayItem != null ? displayItem : new ItemStack(Material.BARRIER));
                secretEntryMap.put(secretName.toLowerCase(), secretEntry);
            }
            return null;
        });
        return secretEntryMap;
    }

    public List<SecretEntry> loadSecretsbyPlayer(Player player) throws SQLException {
        List<SecretEntry> secretEntries = new ArrayList<>();
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement statement = sqlConnection.getOrCreateStatement(getPlayerSecretsQuery);
            statement.setString(1, player.getUniqueId().toString());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String secretName = rs.getString(2);
                if (plugin.getSecretEntries().containsKey(secretName.toLowerCase())) {
                    secretEntries.add(plugin.getSecretEntrybyName(secretName.toLowerCase()));
                }
            }
            return null;
        });
        return secretEntries;
    }

    public void insertSecret(String secretName, String serverName, String worldName, Location location, ItemStack displayItem) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(insertSecretQuery);

            smt.setString(1, secretName);
            smt.setString(2, ItemStackUtil.createDisplayItemString(displayItem));
            smt.setString(3, serverName);
            smt.setString(4, worldName);
            smt.setLong(5, location.getBlockX());
            smt.setLong(6, location.getBlockY());
            smt.setLong(7, location.getBlockZ());

            smt.executeUpdate();
            return null;
        });
    }

    public void updateSecretDisplayItem(String displayItemString, String secretName) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(updateSecretInventoryItemQuery);
            smt.setString(1, displayItemString);
            smt.setString(2, secretName);
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

    public void updateSecretPosition(String secretName, Location loc) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(updateSecretLocationQuery);

            smt.setString(1,loc.getWorld().getName());
            smt.setDouble(2,loc.getX());
            smt.setDouble(3,loc.getY());
            smt.setDouble(4,loc.getZ());
            smt.setString(5, secretName);
            smt.executeUpdate();

            return null;
        });
    }
}
