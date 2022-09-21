package de.fanta.secrets;

import org.bukkit.Location;

public class SecretEntry {
    private final String secretName;
    private final String serverName;
    private final String worldName;
    private final Location secretLocation;

    public SecretEntry(String secretName, String serverName, String worldName, Location secretLocation) {
        this.secretName = secretName;
        this.serverName = serverName;
        this.worldName = worldName;
        this.secretLocation = secretLocation;
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
}
