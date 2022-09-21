package de.fanta.secrets.listener;

import de.fanta.secrets.Secrets;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {
    private final Secrets plugin;

    public PlayerLeaveListener(Secrets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (plugin.getPlayerSecrets(e.getPlayer()) != null) {
            plugin.removePlayerSecrets(e.getPlayer());
        }
    }
}
