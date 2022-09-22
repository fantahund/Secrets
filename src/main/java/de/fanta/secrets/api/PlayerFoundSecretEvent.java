package de.fanta.secrets.api;

import de.fanta.secrets.SecretEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerFoundSecretEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    private final SecretEntry secretEntry;
    private final boolean playerFoundSecret;

    public PlayerFoundSecretEvent(Player player, SecretEntry secretEntry, boolean playerFoundSecret) {
        super(player);
        this.secretEntry = secretEntry;
        this.playerFoundSecret = playerFoundSecret;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public SecretEntry getSecretEntry() {
        return secretEntry;
    }

    public boolean hasPlayerFoundSecret() {
        return playerFoundSecret;
    }
}
