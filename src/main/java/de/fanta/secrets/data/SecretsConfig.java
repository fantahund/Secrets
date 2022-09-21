package de.fanta.secrets.data;

import de.fanta.secrets.Secrets;
import de.iani.cubesideutils.bukkit.sql.SQLConfigBukkit;
import de.iani.cubesideutils.sql.SQLConfig;
import org.bukkit.configuration.file.FileConfiguration;

public class SecretsConfig {
    private final Secrets plugin;
    private SQLConfig sqlConfig;
    private boolean useMysqlDatabase;
    private String serverName;

    public SecretsConfig(Secrets plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        reloadConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        sqlConfig = new SQLConfigBukkit(config.getConfigurationSection("database"));
        useMysqlDatabase = config.getBoolean("database.usemysql");
        serverName = config.getString("database.servername");
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
}
