package de.fanta.secrets.utils.guiutils;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public abstract class AbstractWindow implements Window {
    private final Player player;

    private final Inventory inventory;

    private Window parentWindow;

    public AbstractWindow(Player player, Inventory inventory) {
        this.player = player;
        this.inventory = inventory;
    }

    public AbstractWindow(Window parentWindow, Inventory inventory) {
        this(parentWindow.getPlayer(), inventory);
        this.parentWindow = parentWindow;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public Window getParent() {
        return this.parentWindow;
    }

    public void onItemClicked(InventoryClickEvent event) {
        if (mayAffectThisInventory(event))
            event.setCancelled(true);
    }

    protected boolean mayAffectThisInventory(InventoryClickEvent event) {
        if (getInventory().equals(event.getClickedInventory()))
            return (event.getAction() != InventoryAction.NOTHING);
        return switch (event.getAction()) {
            case COLLECT_TO_CURSOR, MOVE_TO_OTHER_INVENTORY, UNKNOWN -> true;
            default -> false;
        };
    }

    public void onItemDraged(InventoryDragEvent event) {
        if (mayAffectThisInventory(event))
            event.setCancelled(true);
    }

    protected boolean mayAffectThisInventory(InventoryDragEvent event) {
        for (Integer slot : event.getRawSlots()) {
            if (slot < getInventory().getSize())
                return true;
        }
        return false;
    }

    public void open() {
        rebuildInventory();
        getPlayer().openInventory(getInventory());
        WindowManager.getInstance().registerOpenWindow(this);
    }

    protected abstract void rebuildInventory();
}
