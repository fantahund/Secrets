package de.fanta.secrets.data;

import de.fanta.secrets.SecretEntry;
import de.fanta.secrets.Secrets;
import de.fanta.secrets.utils.ItemStackUtil;
import de.iani.cubesideutils.sql.MySQLConnection;
import de.iani.cubesideutils.sql.SQLConfig;
import de.iani.cubesideutils.sql.SQLConnection;
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
import java.util.concurrent.atomic.AtomicLong;

public class Database {

    private final SQLConnection connection;
    private final SQLConfig config;
    private final Secrets plugin;
    private final String getSecretsQuery;
    private final String getPlayerSecretsQuery;
    private final String insertSecretQuery;
    private final String insertPlayerSecretQuery;
    private final String updateSecretInventoryItemQuery;
    private final String getLastUpdateQuery;
    private final String updateLastUpdateQuery;
    private final String deleteSecretQuery;
    private final String deletePlayerSecretsQuery;

    public Database(SQLConfig config, Secrets plugin) {
        this.config = config;
        this.plugin = plugin;

        try {
            this.connection = new MySQLConnection(config.getHost(), config.getDatabase(), config.getUser(), config.getPassword());

            createTablesIfNotExist();
        } catch (SQLException ex) {
            throw new RuntimeException("Could not initialize database", ex);
        }

        getSecretsQuery = "SELECT * FROM " + config.getTablePrefix() + "_secrets";
        getPlayerSecretsQuery = "SELECT * FROM " + config.getTablePrefix() + "_player" + " WHERE uuid = ?";
        insertSecretQuery = "INSERT INTO " + config.getTablePrefix() + "_secrets" + " (secret, displayitem) VALUE (?, ?)";
        insertPlayerSecretQuery = "INSERT INTO " + config.getTablePrefix() + "_player" + " (uuid, secret) VALUE (?, ?)";
        updateSecretInventoryItemQuery = "UPDATE " + config.getTablePrefix() + "_secrets" + " SET `displayitem` = ? WHERE `secret` = ?";
        getLastUpdateQuery = "SELECT `lastupdatetime` FROM " + config.getTablePrefix() + "_update";
        updateLastUpdateQuery = "UPDATE " + config.getTablePrefix() + "_update" + " SET `lastupdatetime` = ?";
        deleteSecretQuery = "DELETE FROM " + config.getTablePrefix() + "_secrets" + " WHERE `secret` = ?";
        deletePlayerSecretsQuery = "DELETE FROM " + config.getTablePrefix() + "_player" + " WHERE `secret` = ?";
    }


    private void createTablesIfNotExist() throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            Statement smt = connection.createStatement();
            PreparedStatement checkTableStatment = sqlConnection.getOrCreateStatement("SHOW TABLES LIKE \"secrets_secrets\"");
            ResultSet checkrs = checkTableStatment.executeQuery();
            if (checkrs.next()) {
                return null;
            }

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
            smt.executeUpdate("CREATE TABLE IF NOT EXISTS " + config.getTablePrefix() + "_update" + " (" +
                    "`lastupdatetime` BIGINT," +
                    "PRIMARY KEY (`lastupdatetime`)" +
                    ")");

            PreparedStatement getUpdateStatement = sqlConnection.getOrCreateStatement("SELECT `lastupdatetime` FROM " + config.getTablePrefix() + "_update");
            ResultSet rs = getUpdateStatement.executeQuery();
            if (!rs.next()) {
                PreparedStatement setUpdateStatement = sqlConnection.getOrCreateStatement("INSERT INTO " + config.getTablePrefix() + "_update" + " (lastupdatetime) VALUE (?)");
                setUpdateStatement.setLong(1, System.currentTimeMillis());
                setUpdateStatement.executeUpdate();
            }
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

                ItemStack displayItem = ItemStackUtil.getDisplayItemfromString(displayItemString);
                SecretEntry secretEntry = new SecretEntry(secretName, displayItem != null ? displayItem : new ItemStack(Material.BARRIER));
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

    public void insertSecret(String secretName, ItemStack displayItem) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(insertSecretQuery);

            smt.setString(1, secretName);
            smt.setString(2, ItemStackUtil.createDisplayItemString(displayItem));
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

    public long getLastUpdateTime() throws SQLException {
        AtomicLong lastUpdateTime = new AtomicLong();
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement statement = sqlConnection.getOrCreateStatement(getLastUpdateQuery);
            ResultSet rs = statement.executeQuery();
            rs.next();
            lastUpdateTime.set(rs.getLong(1));
            return null;
        });
        return lastUpdateTime.get();
    }

    public void updateUpdateTime(long currentTime) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(updateLastUpdateQuery);

            smt.setLong(1, currentTime);
            smt.executeUpdate();

            return null;
        });
    }

    public void deleteSecret(String secretName) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(deleteSecretQuery);

            smt.setString(1, secretName);
            smt.executeUpdate();

            return null;
        });
    }

    public void deletePlayerSecret(String secretName) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(deletePlayerSecretsQuery);

            smt.setString(1, secretName);
            smt.executeUpdate();

            return null;
        });
    }
}
