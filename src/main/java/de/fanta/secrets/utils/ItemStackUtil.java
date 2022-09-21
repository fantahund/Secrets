package de.fanta.secrets.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class ItemStackUtil {

    public static String createDisplayItemString(ItemStack stack) {
        YamlConfiguration conf = new YamlConfiguration();
            if (stack != null) {
                conf.set("displayItem", stack);
            }
        return conf.saveToString();
    }

    public static ItemStack getDisplayItemfromString(String itemString) {
        ItemStack displayItem = null;
        if (itemString != null) {
            YamlConfiguration conf = new YamlConfiguration();
            try {
                conf.loadFromString(itemString);
                displayItem = conf.getItemStack("displayItem");
            } catch (InvalidConfigurationException ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Display Item could not be loaded.", ex);
            }
        }

        return displayItem;
    }
}
