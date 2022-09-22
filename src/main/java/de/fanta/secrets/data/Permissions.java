package de.fanta.secrets.data;

public class Permissions {

    private String createSecretPermission;
    private String setDisplayItemPermission;
    private String secretListPermission;
    private String loadDatabaseSecretsPermission;
    private String deleteSecretPermission;
    private String deletePlayerSecretsPermission;

    public Permissions() {
        loadPermissions();
    }

    public void loadPermissions() {
        createSecretPermission = "secrets.create";
        setDisplayItemPermission = "secrets.setdislayitem";
        secretListPermission = "secrets.list";
        loadDatabaseSecretsPermission = "secrets.loaddatabase";
        deleteSecretPermission = "secrets.deletesecret";
        deletePlayerSecretsPermission = "secrets.deleteplayersecrets";
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

    public String getDeleteSecretPermission() {
        return deleteSecretPermission;
    }

    public String getDeletePlayerSecretsPermission() {
        return deletePlayerSecretsPermission;
    }
}
