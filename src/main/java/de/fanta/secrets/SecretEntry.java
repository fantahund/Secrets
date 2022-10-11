package de.fanta.secrets;

import org.bukkit.inventory.ItemStack;

public class SecretEntry {
    private final String secretName;
    private ItemStack displayStack;

    public SecretEntry(String secretName, ItemStack displayStack) {
        this.secretName = secretName;
        this.displayStack = displayStack;
    }

    public String getSecretName() {
        return secretName;
    }

    public ItemStack getDisplayStack() {
        return displayStack;
    }

    public void setDisplayStack(ItemStack itemStack) {
        this.displayStack = itemStack;
    }
}
