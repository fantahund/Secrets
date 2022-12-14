package de.fanta.secrets;

import de.fanta.secrets.commands.SecretDeletePlayerSecretsCommand;
import de.fanta.secrets.commands.SecretDeleteSecretCommand;
import de.fanta.secrets.commands.SecretGiveDisplayItemCommand;
import de.fanta.secrets.commands.SecretListCommand;
import de.fanta.secrets.commands.SecretSetDisplayItemCommand;
import de.fanta.secrets.commands.SecretsLoadfromDatabaseCommand;
import de.fanta.secrets.data.Database;
import de.fanta.secrets.data.LanguageManager;
import de.fanta.secrets.data.Messages;
import de.fanta.secrets.data.Permissions;
import de.fanta.secrets.data.SecretsConfig;
import de.fanta.secrets.listener.BlockBreakListener;
import de.fanta.secrets.listener.LobbyItemListener;
import de.fanta.secrets.listener.PlayerInteractListener;
import de.fanta.secrets.listener.PlayerJoinListener;
import de.fanta.secrets.listener.PlayerLeaveListener;
import de.fanta.secrets.listener.PlayerSecretFoundListener;
import de.fanta.secrets.listener.SignCreateListener;
import de.fanta.secrets.utils.ChatUtil;
import de.fanta.secrets.utils.guiutils.WindowManager;
import de.iani.cubesideutils.bukkit.commands.CommandRouter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
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
    private Messages messages;
    private NamespacedKey secretSignKey;
    private HashMap<String, SecretEntry> secretEntries;
    private HashMap<UUID, List<SecretEntry>> playerSecrets;
    private long lastUpdateTime;
    private boolean isPaperServer;
    private LanguageManager languageManager;
    private Economy economy;

    @Override
    public void onEnable() {
        plugin = this;
        this.config = new SecretsConfig(this);
        this.languageManager = new LanguageManager(this, config);
        this.permissions = new Permissions();
        this.messages = new Messages(languageManager);
        this.secretSignKey = new NamespacedKey(this, "secretSign");
        new WindowManager(this);
        prefix = messages.getPrefix();

        PluginManager pM = Bukkit.getPluginManager();
        pM.registerEvents(new PlayerJoinListener(this), this);
        pM.registerEvents(new PlayerLeaveListener(this), this);
        pM.registerEvents(new SignCreateListener(this), this);
        pM.registerEvents(new PlayerInteractListener(this), this);
        pM.registerEvents(new BlockBreakListener(this), this);
        pM.registerEvents(new LobbyItemListener(this, config), this);
        pM.registerEvents(new PlayerSecretFoundListener(this, config), this);

        CommandRouter commandRouter = new CommandRouter(getCommand("secrets"));
        commandRouter.addCommandMapping(new SecretListCommand(plugin));
        commandRouter.addCommandMapping(new SecretSetDisplayItemCommand(plugin), "setdisplayitem");
        commandRouter.addCommandMapping(new SecretsLoadfromDatabaseCommand(plugin), "loadfromdatabase");
        commandRouter.addCommandMapping(new SecretDeleteSecretCommand(plugin), "deletesecret");
        commandRouter.addCommandMapping(new SecretDeletePlayerSecretsCommand(plugin), "deleteplayersecrets");
        commandRouter.addCommandMapping(new SecretGiveDisplayItemCommand(plugin), "givedisplayitem");

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
            }
        }

        this.database = new Database(config.getSQLConfig(), this);


        try {
            this.lastUpdateTime = database.getLastUpdateTime();
        } catch (SQLException ex) {
            getLogger().log(Level.SEVERE, "Update Time could not be obtained.", ex);
        }

        try {
            SkullMeta.class.getDeclaredMethod("getPlayerProfile");
            isPaperServer = true;
        } catch (Exception e) {
            getLogger().log(Level.INFO, "Server version spigot. We recommend to use Paper.");
            isPaperServer = false;
        }

        new bStats(this, config).registerbStats();

        startUpdateTimer();

        loadSecretsfromDatabase();

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

    public Messages getMessages() {
        return messages;
    }

    public NamespacedKey getSecretSignKey() {
        return secretSignKey;
    }

    public HashMap<String, SecretEntry> getSecretEntries() {
        return secretEntries;
    }

    public SecretEntry getSecretEntrybyName(String entryName) {
        if (secretEntries.containsKey(entryName.toLowerCase())) {
            return secretEntries.get(entryName.toLowerCase());
        }
        return null;
    }

    public List<SecretEntry> getPlayerSecrets(Player player) {
        return playerSecrets.get(player.getUniqueId());
    }

    public void removePlayerSecrets(Player player) {
        playerSecrets.remove(player.getUniqueId());
    }

    public void removeSecrets(SecretEntry secretEntry) {
        secretEntries.remove(secretEntry.getSecretName());
    }

    public void addPlayerSecrets(Player player, List<SecretEntry> Secrets) {
        playerSecrets.put(player.getUniqueId(), Secrets);
    }

    public void addPlayerSecret(Player player, SecretEntry secretEntry) {
        List<SecretEntry> secretList = playerSecrets.getOrDefault(player.getUniqueId(), new ArrayList<>());
        secretList.add(secretEntry);
        playerSecrets.put(player.getUniqueId(), secretList);
    }

    public boolean createSecret(String secretName) throws SQLException {
        if (secretEntries.containsKey(secretName.toLowerCase())) {
            return false;
        }

        ItemStack itemStack = new ItemStack(Material.STONE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatUtil.GREEN + secretName);
        itemStack.setItemMeta(itemMeta);

        updateCheck();
        database.insertSecret(secretName, itemStack);
        secretEntries.put(secretName.toLowerCase(), new SecretEntry(secretName, itemStack));

        setUpdateTime();

        return true;
    }

    public void setUpdateTime() {
        long updateTime = System.currentTimeMillis();
        try {
            database.updateUpdateTime(updateTime);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.lastUpdateTime = updateTime;
    }

    public SecretEntry getSecretEntrybySign(Sign sign) {
        if (!sign.getPersistentDataContainer().has(plugin.getSecretSignKey(), PersistentDataType.STRING)) {
            return null;
        }

        String secretName = sign.getPersistentDataContainer().get(plugin.getSecretSignKey(), PersistentDataType.STRING);
        return plugin.getSecretEntrybyName(secretName);
    }

    public void loadSecretsfromDatabase() {
        if (secretEntries != null && !secretEntries.isEmpty()) {
            secretEntries.clear();
        }
        try {
            this.secretEntries = database.loadSecrets();
        } catch (SQLException ex) {
            getLogger().log(Level.SEVERE, "Secrets could not be loaded.", ex);
        }

        List<SecretEntry> secretEntries;
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                secretEntries = plugin.getDatabase().loadSecretsbyPlayer(player);
            } catch (SQLException ex) {
                secretEntries = new ArrayList<>();
                plugin.getLogger().log(Level.SEVERE, "Secrets for player " + player + " could not be loaded.", ex);
            }

            plugin.addPlayerSecrets(player, secretEntries);
        }
    }

    private void startUpdateTimer() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::updateCheck, 60 * 20 * 5, 60 * 20 * 5);
    }

    public void updateCheck() {
        long lastUpdate;
        try {
            lastUpdate = database.getLastUpdateTime();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (lastUpdate > lastUpdateTime) {
            loadSecretsfromDatabase();
        }

        this.lastUpdateTime = lastUpdate;
    }

    public boolean isPaperServer() {
        return isPaperServer;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public Economy getEconomy() {
        return economy;
    }
}
