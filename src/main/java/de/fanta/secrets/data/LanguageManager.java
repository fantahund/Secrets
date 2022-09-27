package de.fanta.secrets.data;

import de.fanta.secrets.Secrets;
import de.iani.cubesideutils.StringUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.logging.Level;

public class LanguageManager {
    private final Secrets plugin;
    private final String[] languages;
    private final File languageFolder;
    private final FileConfiguration currentMessages;


    public LanguageManager(Secrets plugin, SecretsConfig config) {
        this.plugin = plugin;
        this.languageFolder = new File(plugin.getDataFolder().getAbsolutePath() + "/languages");
        this.languages = new String[]{"de_DE", "en_EN"};
        loadLanguageFiles();
        File currentLanguage = new File(languageFolder, config.getLanguage() + ".yml");
        this.currentMessages = YamlConfiguration.loadConfiguration(currentLanguage);
    }

    public void loadLanguageFiles() {
        for (String string : languages) {
            File languageConfig = new File(languageFolder, string + ".yml");

            if (!languageConfig.exists()) {
                plugin.getLogger().log(Level.INFO, "create " + languageConfig.getAbsolutePath());
                languageConfig.getParentFile().mkdirs();
                plugin.saveResource("languages/" + string + ".yml", false);
            }
        }
    }

    public String getMessage(String path) {
        String message = currentMessages.getString(path);
        if (message == null) {
            return "Error loading the message " + path + "." + " Please report to an admin!";
        }
        return StringUtil.convertColors(message);
    }
}