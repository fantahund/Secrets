package de.fanta.secrets.listener;

import de.fanta.secrets.SecretEntry;
import de.fanta.secrets.Secrets;
import de.fanta.secrets.utils.ChatUtil;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.logging.Level;

public class PlayerInteractListener implements Listener {

    private final Secrets plugin;

    public PlayerInteractListener(Secrets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSecretClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if ((e.getAction() == Action.RIGHT_CLICK_BLOCK) && ((e.getClickedBlock().getState() instanceof Sign sign))) {
            if (sign.getPersistentDataContainer().has(plugin.getSecretSignKey())) {
                String secretName = sign.getPersistentDataContainer().get(plugin.getSecretSignKey(), PersistentDataType.STRING);
                SecretEntry secretEntry = plugin.getSecretEntrybyName(secretName);
                if (secretEntry != null) {
                    if (!plugin.getPlayerSecrets(player).contains(secretEntry)) {
                        try {
                            plugin.addPlayerSecret(player, secretEntry);
                            plugin.getDatabase().insertPlayerSecret(player, secretEntry.getSecretName());
                            ChatUtil.sendNormalMessage(player, "Du hast das Secret " + ChatUtil.BLUE + secretEntry.getSecretName() + ChatUtil.GREEN + " gefunden!");
                        } catch (SQLException ex) {
                            plugin.getLogger().log(Level.SEVERE, "Secrets " + secretName + " could not be saved for player " + player.getName() + ".", ex);
                        }
                    } else {
                        ChatUtil.sendNormalMessage(player, "Du hast das Secret " + ChatUtil.BLUE + secretEntry.getSecretName() + ChatUtil.GREEN + " bereits gefunden!");
                    }
                } else {
                    ChatUtil.sendErrorMessage(player, "Secret " + secretName + " existiert nicht!");
                }
            }
        }
    }
}
