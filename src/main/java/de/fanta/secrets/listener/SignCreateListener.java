package de.fanta.secrets.listener;

import de.fanta.secrets.SecretEntry;
import de.fanta.secrets.Secrets;
import de.fanta.secrets.data.SecretsConfig;
import de.fanta.secrets.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
    private final SecretsConfig config;

    public SignCreateListener(Secrets plugin, SecretsConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void createSecretSign(SignChangeEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission(plugin.getPermissions().getCreateSecretPermission())) {
            String line1 = e.getLine(0);
            String line3 = e.getLine(2);
            String line4 = e.getLine(3);

            if (line1 != null && line1.equalsIgnoreCase("[secret]")) {
                if (line3 != null && !line3.startsWith(" ") && !line3.equals("")) {
                    boolean secretCreated;
                    try {
                        secretCreated = plugin.createSecret(line3, e.getBlock().getLocation());
                    } catch (SQLException ex) {
                        plugin.getLogger().log(Level.SEVERE, "Secret " + line3 + " could not be created.", ex);
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
                        if (line4 != null && line4.equalsIgnoreCase("Override")) {
                            SecretEntry secretEntry = plugin.getSecretEntrybyName(line3.toLowerCase());
                            if (secretEntry != null) {
                                if (secretEntry.getServerName().equalsIgnoreCase(config.getServerName())) {
                                    Location oldSecretLocation = secretEntry.getSecretLocation();

                                    e.setLine(0, plugin.getPrefix());
                                    e.setLine(2, ChatUtil.PINK + line3);
                                    e.setLine(3, "");

                                    try {
                                        plugin.getDatabase().updateSecretPosition(secretEntry.getSecretName(), e.getBlock().getLocation());
                                    } catch (SQLException ex) {
                                        plugin.getLogger().log(Level.SEVERE, "Secret Location could not be overwritten.", ex);
                                        ChatUtil.sendErrorMessage(player, "Secret konnte nicht überschrieben werden (Datenbankfehler)");
                                    }
                                    oldSecretLocation.getWorld().getBlockAt(oldSecretLocation).setType(Material.AIR);
                                    secretEntry.setLocation(e.getBlock().getLocation());

                                    if (e.getBlock().getState() instanceof Sign sign) {
                                        sign.getPersistentDataContainer().set(plugin.getSecretSignKey(), PersistentDataType.STRING, line3);
                                        sign.update();
                                    }

                                    ChatUtil.sendNormalMessage(player, "Secret " + line3 + " wurde überschrieben.");
                                } else {
                                    ChatUtil.sendErrorMessage(player, "Secret ist nicht auf diesem Server und kann nicht überschrieben werden");
                                }
                            }
                        } else {
                            ChatUtil.sendErrorMessage(player, "Secret " + line3 + " existiert bereits.");
                        }
                    }
                } else {
                    ChatUtil.sendErrorMessage(player, "Du musst denn Secret Name in die 3 Zeile schreiben.");
                }
            }
        }
    }
}
