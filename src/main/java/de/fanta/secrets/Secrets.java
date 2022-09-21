package de.fanta.secrets;

import de.fanta.secrets.data.Database;
import de.fanta.secrets.data.Permissions;
import de.fanta.secrets.data.SecretsConfig;
import de.fanta.secrets.listener.PlayerInteractListener;
import de.fanta.secrets.listener.PlayerJoinListener;
import de.fanta.secrets.listener.SignCreateListener;
import de.fanta.secrets.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class Secrets extends JavaPlugin {

    private static Secrets plugin;
    private String prefix;
    private Database database;
    private SecretsConfig config;
    private Permissions permissions;
    private NamespacedKey secretSignKey;
    private List<SecretEntry> secretEntries;
    private HashMap<UUID, List<SecretEntry>> playerSecrets;


    @Override
    public void onEnable() {
        plugin = this;
        this.config = new SecretsConfig(this);
        this.permissions = new Permissions();
        this.secretSignKey = new NamespacedKey(this, "secretSign");
        prefix = ChatUtil.BLUE + "[" + ChatUtil.GREEN + "Secret" + ChatUtil.BLUE + "]";

        PluginManager pM = Bukkit.getPluginManager();
        pM.registerEvents(new PlayerJoinListener(this), this);
        pM.registerEvents(new SignCreateListener(this), this);
        pM.registerEvents(new PlayerInteractListener(this), this);

        this.database = new Database(config.getSQLConfig(), config, this);
        try {
            this.secretEntries = database.loadSecrets();
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Secrets could not be loaded.");
        }
        playerSecrets = new HashMap<>();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Secrets getPlugin() {
        return plugin;
    }

    public String getPrefix() {
        return prefix;
    }

    public Database getDatabase() {
        return database;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public NamespacedKey getSecretSignKey() {
        return secretSignKey;
    }

    public List<SecretEntry> getSecretEntries() {
        return secretEntries;
    }

    public SecretEntry getSecretEntrybyName(String entryName) {
        for (SecretEntry secretEntry : secretEntries) {
            if (secretEntry.getSecretName().equalsIgnoreCase(entryName)) {
                return secretEntry;
            }
        }
        return null;
    }

    public List<SecretEntry> getPlayerSecrets(Player player) {
        return playerSecrets.get(player.getUniqueId());
    }

    public void addPlayerSecrets(Player player, List<SecretEntry> Secrets) {
        playerSecrets.put(player.getUniqueId(), Secrets);
    }

    public void addPlayerSecret(Player player, SecretEntry secretEntry) {
        List<SecretEntry> secretList = playerSecrets.getOrDefault(player.getUniqueId(), new ArrayList<>());
        secretList.add(secretEntry);
        playerSecrets.put(player.getUniqueId(), secretList);
    }

    public boolean createSecret(String secretName, Location location) throws SQLException {
        for (SecretEntry secretEntry : secretEntries) {
            if (secretEntry.getSecretName().equalsIgnoreCase(secretName)) {
                return false;
            }
        }
        database.insertSecret(secretName, config.getServerName(), location.getWorld().getName(), location);
        secretEntries.add(new SecretEntry(secretName, config.getServerName(), location.getWorld().getName(), location, null));
        return true;
    }
}
