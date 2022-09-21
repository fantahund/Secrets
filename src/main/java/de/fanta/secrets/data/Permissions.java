package de.fanta.secrets.data;

public class Permissions {

    private String createSecretPermission;

    public Permissions() {
        loadPermissions();
    }

    public void loadPermissions() {
        createSecretPermission = "secrets.create";
    }

    public String getCreateSecretPermission() {
        return createSecretPermission;
    }
}
