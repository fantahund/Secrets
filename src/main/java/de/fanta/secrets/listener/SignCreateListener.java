package de.fanta.secrets.listener;

import de.fanta.secrets.Secrets;
import de.fanta.secrets.utils.ChatUtil;
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
                        secretCreated = plugin.createSecret(line3);
                    } catch (SQLException ex) {
                        plugin.getLogger().log(Level.SEVERE, "Secret " + line3 + " could not be created.", ex);
                        ChatUtil.sendErrorMessage(player, plugin.getMessages().getSecretCreateError(line3));
                        return;
                    }

                    e.setLine(0, plugin.getMessages().getSignLine1(line3));
                    e.setLine(1, plugin.getMessages().getSignLine2(line3));
                    e.setLine(2, plugin.getMessages().getSignLine3(line3));
                    e.setLine(3, plugin.getMessages().getSignLine4(line3));

                    if (e.getBlock().getState() instanceof Sign sign) {
                        sign.getPersistentDataContainer().set(plugin.getSecretSignKey(), PersistentDataType.STRING, line3);
                        sign.update();
                    }
                    if (secretCreated) {
                        ChatUtil.sendNormalMessage(player, plugin.getMessages().getSecretCreateSuccessful(line3));
                    } else {
                        ChatUtil.sendWarningMessage(player, plugin.getMessages().getSecretExistsCreatedNewSign(line3));
                    }
                } else {
                    ChatUtil.sendErrorMessage(player, plugin.getMessages().getSecretCreateWrongSecretLineError());
                }
            }
        }
    }
}
