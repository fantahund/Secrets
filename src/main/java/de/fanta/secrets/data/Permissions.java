package de.fanta.secrets.data;

public class Permissions {

    private String createSecretPermission;
    private String setDisplayItemPermission;
    private String secretListPermission;
    private String loadDatabaseSecretsPermission;

    public Permissions() {
        loadPermissions();
    }

    public void loadPermissions() {
        createSecretPermission = "secrets.create";
        setDisplayItemPermission = "secrets.setdislayitem";
        secretListPermission = "secrets.list";
        loadDatabaseSecretsPermission = "secrets.loaddatabase";
    }

    public String getCreateSecretPermission() {
        return createSecretPermission;
    }

    public String getSetDisplayItemPermission() {
        return setDisplayItemPermission;
    }

    public String getSecretListPermission() {
        return secretListPermission;
    }

    public String getLoadDatabaseSecretsPermission() {
        return loadDatabaseSecretsPermission;
    }
}
