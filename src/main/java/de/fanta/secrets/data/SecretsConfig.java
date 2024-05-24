package de.fanta.secrets.data;

import de.fanta.secrets.Secrets;
import de.iani.cubesideutils.bukkit.sql.SQLConfigBukkit;
import de.iani.cubesideutils.sql.SQLConfig;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class SecretsConfig {
    private final Secrets plugin;
    private SQLConfig sqlConfig;
    private boolean secretItemUse;
    private int secretItemSlot;
    private List<String> secretItemWorlds;
    private String language;
    private boolean commandReward;
    private String commandRewardCommand;
    private boolean economyReward;
    private double economyRewardAmount;
    private boolean convert;

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
        secretItemUse = config.getBoolean("lobbyitem.use");
        secretItemSlot = config.getInt("lobbyitem.slot");
        secretItemWorlds = config.getStringList("lobbyitem.worlds");
        language = config.getString("language");
        commandReward = config.getBoolean("rewards.commandReward.enable");
        commandRewardCommand = config.getString("rewards.commandReward.command");
        economyReward = config.getBoolean("rewards.economyReward.enable");
        economyRewardAmount = config.getDouble("rewards.economyReward.amount");
        convert = config.getBoolean("convert");
    }

    public SQLConfig getSQLConfig() {
        return sqlConfig;
    }

    public boolean getSecretItemUse() {
        return secretItemUse;
    }

    public int getSecretItemSlot() {
        return secretItemSlot;
    }

    public List<String> getSecretItemWorlds() {
        return secretItemWorlds;
    }

    public String getLanguage() {
        return language;
    }

    public boolean getCommandReward() {
        return commandReward;
    }

    public String getCommandRewardCommand() {
        return commandRewardCommand;
    }

    public boolean getEconomyReward() {
        return economyReward;
    }

    public double getEconomyRewardAmount() {
        return economyRewardAmount;
    }

    public boolean isConvertEnabled() {
        return convert;
    }
}
