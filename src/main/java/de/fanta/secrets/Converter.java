package de.fanta.secrets;

import de.fanta.secrets.utils.ItemStackUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

public record Converter(Secrets plugin) {

    public void convert() throws SQLException {
        HashMap<String, SecretEntry> secrets = plugin.getDatabase().loadSecrets();
        secrets.forEach((s, secretEntry) -> {
            try {
                plugin.getDatabase().updateSecretDisplayItem(ItemStackUtil.createDisplayItemString(secretEntry.getDisplayStack()), secretEntry.getSecretName());
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while convert SecretDisplay Item", e);
            }
        });
    }
}
