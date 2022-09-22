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
                ChatUtil.sendNormalMessage(player, "Du hast das Secret " + ChatUtil.BLUE + secretEntry.getSecretName() + ChatUtil.GREEN + " gefunden!");
                ChatUtil.sendTitleToPlayer(player, secretEntry.getSecretName(), ChatUtil.GREEN + "Du hast ein Secret gefunden!", ChatUtil.GREEN, 10, 60, 10, false);
                player.getWorld().playSound(player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1, 1);
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Secrets " + secretEntry.getSecretName() + " could not be saved for player " + player.getName() + ".", ex);
            }
        } else {
            ChatUtil.sendNormalMessage(player, "Du hast das Secret " + ChatUtil.BLUE + secretEntry.getSecretName() + ChatUtil.GREEN + " bereits gefunden!");
        }
    }
}
