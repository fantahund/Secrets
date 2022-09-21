package de.fanta.secrets.listener;

import de.fanta.secrets.Secrets;
import de.fanta.secrets.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.logging.Level;

public class SignCreateListener implements Listener {

    private final Secrets plugin;

    public SignCreateListener(Secrets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void createSecretSign(SignChangeEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission(plugin.getPermissions().getCreateSecretPermission())) {
            String line1 = e.getLine(0);
            String line3 = e.getLine(2);

            if (line1 != null && line1.equalsIgnoreCase("[secret]")) {
                if (line3 != null && !line3.startsWith(" ") && !line3.equals("")) {
                    boolean secretCreated;
                    try {
                        secretCreated = plugin.createSecret(line3, e.getBlock().getLocation());
                    } catch (SQLException ex) {
                        plugin.getLogger().log(Level.SEVERE, "Secret " + line3 + " could not be created.");
                        ChatUtil.sendErrorMessage(player, "Secret " + line3 + " konnte nicht erstellt werden!");
                        return;
                    }

                    if (secretCreated) {
                        e.setLine(0, plugin.getPrefix());
                        e.setLine(2, ChatUtil.PINK + line3);

                        if (e.getBlock().getState() instanceof Sign sign) {
                            sign.getPersistentDataContainer().set(plugin.getSecretSignKey(), PersistentDataType.STRING, line3);
                            sign.update();
                        }

                        ChatUtil.sendNormalMessage(player, "Secret " + line3 + " wurde erstellt.");

                    } else {
                        ChatUtil.sendErrorMessage(player, "Secret " + line3 + " existiert bereits.");
                    }
                } else {
                    ChatUtil.sendErrorMessage(player, "Du musst denn Secret Name in die 3 Zeile schreiben.");
                }
            }
        }
    }
}
