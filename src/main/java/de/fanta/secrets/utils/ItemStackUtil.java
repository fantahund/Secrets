package de.fanta.secrets.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;

public class ItemStackUtil {

    public static String createDisplayItemString(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(stack.serializeAsBytes());
    }

    public static ItemStack getDisplayItemfromString(String itemString) {
        ItemStack displayItem = null;
        if (itemString != null) {

            YamlConfiguration conf = new YamlConfiguration();
            try {
                conf.loadFromString(itemString);
                displayItem = conf.getItemStack("displayItem");
            } catch (InvalidConfigurationException ex) {
                byte[] itemBytes = Base64.getDecoder().decode(itemString);
                displayItem = ItemStack.deserializeBytes(itemBytes);
            }
        }

        return displayItem;
    }
}
