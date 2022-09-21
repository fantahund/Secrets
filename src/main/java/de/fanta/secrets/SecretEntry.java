package de.fanta.secrets;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SecretEntry {
    private final String secretName;
    private final String serverName;
    private final String worldName;
    private final Location secretLocation;
    private ItemStack displayStack;

    public SecretEntry(String secretName, String serverName, String worldName, Location secretLocation, @Nullable ItemStack displayStack) {
        this.secretName = secretName;
        this.serverName = serverName;
        this.worldName = worldName;
        this.secretLocation = secretLocation;
        this.displayStack = displayStack;
    }

    public String getSecretName() {
        return secretName;
    }

    public String getServerName() {
        return serverName;
    }

    public Location getSecretLocation() {
        return secretLocation;
    }

    public ItemStack getDisplayStack() {
        return displayStack;
    }

    public void setDisplayStack(ItemStack itemStack) {
        this.displayStack = itemStack;
    }
}
