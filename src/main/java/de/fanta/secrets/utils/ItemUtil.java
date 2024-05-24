package de.fanta.secrets.utils;

import de.iani.cubesideutils.bukkit.MinecraftVersion;
import de.iani.cubesideutils.bukkit.items.ItemBuilder;
import de.iani.cubesideutils.bukkit.plugin.CubesideUtilsBukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {
    public static final ItemStack EMPTY_ICON = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ", true, false);

    public static ItemStack createGuiItem(Material material, String name, String... lore) {
        return createGuiItem(material, name, false, lore);
    }

    public static ItemStack createGuiItem(Material material, String name, boolean glowing, boolean showTooltip, String... lore) {
        ItemBuilder builder = ItemBuilder.fromMaterial(material).displayName(name).lore(lore);
        if (glowing) {
            builder.enchantment(Enchantment.MENDING, 1, true).flag(ItemFlag.HIDE_ENCHANTS);
        }
        if (!showTooltip && MinecraftVersion.isAboveOrEqual(1, 20, 6)) {
            builder.hideTooltip(true);
        }
        return builder.build();

    }

    public static ItemStack createGuiItem(Material material, String name, boolean glowing, String... lore) {
        return createGuiItem(material, name, glowing, true, lore);
    }
}
