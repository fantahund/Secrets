package de.fanta.secrets;

import de.fanta.secrets.data.SecretsConfig;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

public record bStats(Secrets plugin, SecretsConfig config) {

    public void registerbStats() {
        int pluginId = 16510;
        Metrics metrics = new Metrics(plugin, pluginId);
        metrics.addCustomChart(new SimplePie("use_lobby-item", () -> String.valueOf(config.getSecretItemUse())));
        metrics.addCustomChart(new SimplePie("Language", config::getLanguage));
    }
}
