package de.fanta.secrets.data;

import de.fanta.secrets.Secrets;
import de.iani.cubesideutils.bukkit.sql.SQLConfigBukkit;
import de.iani.cubesideutils.sql.SQLConfig;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class SecretsConfig {
    private final Secrets plugin;
    private SQLConfig sqlConfig;
    private boolean useMysqlDatabase;
    private String serverName;
    private int secretItemUse;
    private int secretItemSlot;
    private List<String> secretItemWorlds;

    public SecretsConfig(Secrets plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        reloadConfig();
    }

    public void reloadConfig() {
        FileConfiguration config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveConfig();

        sqlConfig = new SQLConfigBukkit(config.getConfigurationSection("database"));
        useMysqlDatabase = config.getBoolean("database.usemysql");
        serverName = config.getString("database.servername");
        secretItemUse = config.getInt("lobbyitem.use");
        secretItemSlot = config.getInt("lobbyitem.slot");
        secretItemWorlds = config.getStringList("lobbyitem.worlds");
    }

    public SQLConfig getSQLConfig() {
        return sqlConfig;
    }

    public boolean useMysqlDatabase() {
        return useMysqlDatabase;
    }

    public String getServerName() {
        return serverName;
    }

    public int getSecretItemUse() {
        return secretItemUse;
    }

    public int getSecretItemSlot() {
        return secretItemSlot;
    }

    public List<String> getSecretItemWorlds() {
        return secretItemWorlds;
    }
}
