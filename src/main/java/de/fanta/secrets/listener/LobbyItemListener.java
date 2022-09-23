package de.fanta.secrets.listener;

import de.fanta.secrets.Secrets;
import de.fanta.secrets.data.SecretsConfig;
import de.fanta.secrets.guis.SecretsFoungGui;
import de.fanta.secrets.utils.ChatUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class LobbyItemListener implements Listener {

    private final Secrets plugin;
    private final NamespacedKey namespacedKey;
    private final ItemStack secretItem;
    private final SecretsConfig config;

    public LobbyItemListener(Secrets plugin, SecretsConfig config) {
        this.plugin = plugin;
        this.namespacedKey = new NamespacedKey(plugin, "LobbyItem");
        ItemStack itemStack = new ItemStack(Material.EMERALD);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatUtil.GREEN + "Secrets");
        itemMeta.getPersistentDataContainer().set(this.namespacedKey, PersistentDataType.STRING, "LobbyItem");
        itemStack.setItemMeta(itemMeta);
        this.secretItem = itemStack;
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission(plugin.getPermissions().getSecretListPermission()) && config.getSecretItemUse() && config.getSecretItemWorlds().contains(e.getPlayer().getWorld().getName())) {
            e.getPlayer().getInventory().setItem(config.getSecretItemSlot(), secretItem);
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        if (e.getPlayer().hasPermission(plugin.getPermissions().getSecretListPermission())) {
            if (config.getSecretItemWorlds().contains(e.getPlayer().getWorld().getName()) && config.getSecretItemUse()) {
                e.getPlayer().getInventory().setItem(config.getSecretItemSlot(), secretItem);
            } else {
                ItemStack[] itemStacks = e.getPlayer().getInventory().getContents();
                for (int i = 0; i < itemStacks.length; i++) {
                    ItemStack stack = itemStacks[i];
                    if (stack != null && stack.getItemMeta() != null && stack.getItemMeta().getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING)) {
                        e.getPlayer().getInventory().setItem(i, null);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent e) {
        ItemStack stack = e.getCurrentItem();
        if (stack != null) {
            ItemMeta meta = stack.getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING) && e.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
                e.setCancelled(true);
            }
        }

        if (e.getClick() == ClickType.NUMBER_KEY) {
            int slot = e.getHotbarButton();
            if (slot >= 0 && slot < 9) {
                if (!e.getWhoClicked().getInventory().equals(e.getInventory())) {
                    ItemStack swap = e.getWhoClicked().getInventory().getItem(slot);
                    if (swap != null) {
                        ItemMeta swapItemMeta = swap.getItemMeta();
                        if (swapItemMeta != null && swapItemMeta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING) && e.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        ItemStack stack = e.getItemDrop().getItemStack();
        ItemMeta meta = stack.getItemMeta();
        if (meta != null && meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING) && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemSwitchHand(PlayerSwapHandItemsEvent e) {
        ItemStack stack = e.getOffHandItem();
        if (stack != null) {
            ItemMeta meta = stack.getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING) && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractItem(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack stack = e.getItem();
            if (stack != null) {
                ItemMeta meta = stack.getItemMeta();
                if (meta != null && meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING)) {
                    if (e.getPlayer().hasPermission(plugin.getPermissions().getSecretListPermission()) && config.getSecretItemUse()) {
                        new SecretsFoungGui(plugin.getPlayerSecrets(e.getPlayer()), e.getPlayer(), plugin).open();
                    }
                }
            }
        }
    }
}
