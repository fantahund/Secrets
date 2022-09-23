package de.fanta.secrets.listener;

import de.fanta.secrets.SecretEntry;
import de.fanta.secrets.Secrets;
import de.fanta.secrets.api.PlayerFoundSecretEvent;
import de.fanta.secrets.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

public class PlayerInteractListener implements Listener {

    private final Secrets plugin;

    public PlayerInteractListener(Secrets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSecretClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if ((e.getAction() == Action.RIGHT_CLICK_BLOCK) && ((e.getClickedBlock().getState() instanceof Sign sign))) {
            if (sign.getPersistentDataContainer().has(plugin.getSecretSignKey(), PersistentDataType.STRING)) {
                String secretName = sign.getPersistentDataContainer().get(plugin.getSecretSignKey(), PersistentDataType.STRING);
                if (secretName != null) {
                    SecretEntry secretEntry = plugin.getSecretEntrybyName(secretName);
                    if (secretEntry != null) {
                        PlayerFoundSecretEvent playerFoundSecretEvent = new PlayerFoundSecretEvent(e.getPlayer(), secretEntry, plugin.getPlayerSecrets(player).contains(secretEntry));
                        Bukkit.getServer().getPluginManager().callEvent(playerFoundSecretEvent);
                    } else {
                        ChatUtil.sendErrorMessage(player, "Secret " + secretName + " existiert nicht!");
                    }
                }
            }
        }
    }


}
