package de.fanta.secrets.listener;

import de.fanta.secrets.SecretEntry;
import de.fanta.secrets.Secrets;
import de.fanta.secrets.api.PlayerFoundSecretEvent;
import de.fanta.secrets.utils.ChatUtil;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.logging.Level;

public class PlayerSecretFoundListener implements Listener {

    private final Secrets plugin;

    public PlayerSecretFoundListener(Secrets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSecretFound(PlayerFoundSecretEvent e) {
        Player player = e.getPlayer();
        SecretEntry secretEntry = e.getSecretEntry();
        if (!e.hasPlayerFoundSecret()) {
            try {
                plugin.addPlayerSecret(player, secretEntry);
                plugin.getDatabase().insertPlayerSecret(player, secretEntry.getSecretName());
                ChatUtil.sendNormalMessage(player, plugin.getMessages().getSecretFound(secretEntry.getSecretName()));
                ChatUtil.sendTitleToPlayer(player, plugin.getMessages().getSecretFoundTitle(secretEntry.getSecretName()), plugin.getMessages().getSecretFoundSubTitle(secretEntry.getSecretName()), ChatUtil.GREEN, 10, 60, 10, false);
                player.getWorld().playSound(player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1, 1);
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Secrets " + secretEntry.getSecretName() + " could not be saved for player " + player.getName() + ".", ex);
            }
        } else {
            ChatUtil.sendWarningMessage(player, plugin.getMessages().getSecretAlwaysFound(secretEntry.getSecretName()));
        }
    }
}
