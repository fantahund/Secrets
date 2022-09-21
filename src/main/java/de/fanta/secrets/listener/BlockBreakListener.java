package de.fanta.secrets.listener;

import de.fanta.secrets.SecretEntry;
import de.fanta.secrets.Secrets;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final Secrets plugin;

    public BlockBreakListener(Secrets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getState() instanceof Sign sign) {
            SecretEntry secretEntry = plugin.getSecretEntrybySign(sign);
            if (secretEntry != null) {
                e.setCancelled(true);
            }
        }
    }

}
