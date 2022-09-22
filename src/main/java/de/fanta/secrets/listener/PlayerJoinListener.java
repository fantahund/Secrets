package de.fanta.secrets.listener;

import de.fanta.secrets.SecretEntry;
import de.fanta.secrets.Secrets;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class PlayerJoinListener implements Listener {

    private final Secrets plugin;

    public PlayerJoinListener(Secrets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        List<SecretEntry> secretEntries;
        try {
            secretEntries = plugin.getDatabase().loadSecretsbyPlayer(e.getPlayer());
        } catch (SQLException ex) {
            secretEntries = new ArrayList<>();
            plugin.getLogger().log(Level.SEVERE, "Secrets for player " + e.getPlayer().getName() + " could not be loaded.", ex);
        }

        plugin.addPlayerSecrets(e.getPlayer(), secretEntries);
    }
}
