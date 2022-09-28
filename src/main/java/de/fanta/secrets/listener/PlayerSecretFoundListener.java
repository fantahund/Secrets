package de.fanta.secrets.listener;

import de.fanta.secrets.SecretEntry;
import de.fanta.secrets.Secrets;
import de.fanta.secrets.api.PlayerFoundSecretEvent;
import de.fanta.secrets.data.SecretsConfig;
import de.fanta.secrets.utils.ChatUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.logging.Level;

public class PlayerSecretFoundListener implements Listener {

    private final Secrets plugin;
    private final SecretsConfig config;

    public PlayerSecretFoundListener(Secrets plugin, SecretsConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void onSecretFound(PlayerFoundSecretEvent e) {
        Player player = e.getPlayer();
        SecretEntry secretEntry = e.getSecretEntry();
        if (!e.hasPlayerFoundSecret()) {
            try {
                plugin.addPlayerSecret(player, secretEntry);
                plugin.getDatabase().insertPlayerSecret(player, secretEntry.getSecretName());
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Secrets " + secretEntry.getSecretName() + " could not be saved for player " + player.getName() + ".", ex);
                return;
            }

            ChatUtil.sendNormalMessage(player, plugin.getMessages().getSecretFound(secretEntry.getSecretName()));
            ChatUtil.sendTitleToPlayer(player, plugin.getMessages().getSecretFoundTitle(secretEntry.getSecretName()), plugin.getMessages().getSecretFoundSubTitle(secretEntry.getSecretName()), ChatUtil.GREEN, 10, 60, 10, false);
            player.getWorld().playSound(player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1, 1);

            if (config.getCommandReward()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), config.getCommandRewardCommand().replace("%player%", e.getPlayer().getName()));
            }

            if (config.getEconomyReward()) {
                Economy economy = plugin.getEconomy();
                if (economy != null) {
                    double reward = config.getEconomyRewardAmount();
                    economy.depositPlayer(e.getPlayer(), reward);
                    ChatUtil.sendNormalMessage(e.getPlayer(), plugin.getMessages().getEconomyReward(reward, reward > 1 ? economy.currencyNamePlural() : economy.currencyNameSingular()));
                }
            }
        } else {
            ChatUtil.sendWarningMessage(player, plugin.getMessages().getSecretAlwaysFound(secretEntry.getSecretName()));
        }
    }
}
