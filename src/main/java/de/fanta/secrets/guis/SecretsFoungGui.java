package de.fanta.secrets.guis;

import de.fanta.secrets.SecretEntry;
import de.fanta.secrets.Secrets;
import de.fanta.secrets.utils.ChatUtil;
import de.fanta.secrets.utils.guiutils.AbstractWindow;
import de.fanta.secrets.utils.guiutils.GUIUtils;
import de.iani.cubesideutils.bukkit.items.CustomHeads;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SecretsFoungGui extends AbstractWindow {
    private static final int NUM_OF_COLUMNS = 9;
    private static final int NUM_OF_ITEM_COLUMNS = NUM_OF_COLUMNS - 1;
    private static final int NUM_OF_ROWS = 6;
    private static final int WINDOW_SIZE = NUM_OF_COLUMNS * NUM_OF_ROWS;
    private static final int END_OF_FIRST_ROW_INDEX = NUM_OF_COLUMNS - 1;
    private static final int END_OF_LAST_ROW_INDEX = WINDOW_SIZE - 1;

    private static final int CLOSE_INDEX = 0;
    private static final int SECRET_SCORE_INDEX = 1;

    private static final HashMap<UUID, List<SecretEntry>> secretsListByPlayer = new HashMap<>();
    private final int numOfItemRows;
    private int scrollAmount;
    private final Secrets plugin;

    public SecretsFoungGui(List<SecretEntry> secretList, Player player, Secrets plugin) {
        super(player, Bukkit.createInventory(player, WINDOW_SIZE, ChatUtil.GREEN + "Secrets"));
        this.plugin = plugin;
        secretsListByPlayer.put(player.getUniqueId(), secretList);
        this.numOfItemRows = (int) (Math.ceil(secretList.size() / (double) NUM_OF_ITEM_COLUMNS));
        this.scrollAmount = 0;
    }

    protected void rebuildInventory() {
        ItemStack[] content = new ItemStack[WINDOW_SIZE];

        // build scroll buttons
        ItemStack scrollUpHead = this.scrollAmount <= 0 ? CustomHeads.RAINBOW_BLANK.getHead() : CustomHeads.RAINBOW_ARROW_UP.getHead();
        ItemMeta scrollUpMeta = scrollUpHead.getItemMeta();
        scrollUpMeta.setDisplayName((this.scrollAmount <= 0 ? ChatColor.GRAY : ChatColor.WHITE) + "Nach oben scrollen");
        scrollUpHead.setItemMeta(scrollUpMeta);
        content[NUM_OF_COLUMNS - 1] = scrollUpHead;

        ItemStack scrollDownHead = this.scrollAmount >= this.numOfItemRows - NUM_OF_ROWS ? CustomHeads.RAINBOW_BLANK.getHead() : CustomHeads.RAINBOW_ARROW_DOWN.getHead();
        ItemMeta scrollDownMeta = scrollDownHead.getItemMeta();
        scrollDownMeta.setDisplayName((this.scrollAmount >= this.numOfItemRows - NUM_OF_ROWS ? ChatColor.GRAY : ChatColor.WHITE) + "Nach unten scrollen");
        scrollDownHead.setItemMeta(scrollDownMeta);
        content[WINDOW_SIZE - 1] = scrollDownHead;

        // build scrollbar
        for (int i = 1; i < NUM_OF_ROWS - 1; i++) {
            content[(i + 1) * NUM_OF_COLUMNS - 1] = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        }
        int numOfScrollbarRows = NUM_OF_ROWS - 2;
        int currentScrollbarRow =
                (int) Math.round(this.scrollAmount * numOfScrollbarRows / (double) this.numOfItemRows);
        content[(currentScrollbarRow + 2) * NUM_OF_COLUMNS - 1] = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);

        // fill in items
        int index = this.scrollAmount * NUM_OF_ITEM_COLUMNS;
        List<SecretEntry> secrets = secretsListByPlayer.get(getPlayer().getUniqueId());
        for (int row = 1; row < NUM_OF_ROWS && index < secrets.size(); row++) {
            for (int column = 0; column < NUM_OF_ITEM_COLUMNS && index < secrets.size(); column++) {
                SecretEntry secretEntry = new ArrayList<>(secrets).get(index++);
                ItemStack stack = secretEntry.getDisplayStack().clone();
                content[row * NUM_OF_COLUMNS + column] = stack;
            }
        }

        content[CLOSE_INDEX] = CustomHeads.RAINBOW_X.getHead(ChatUtil.GREEN + "Menü schlißen");
        content[SECRET_SCORE_INDEX] = GUIUtils.createGuiItem(Material.EMERALD, ChatUtil.GREEN + "Anzahl gefundener Secrets", "" + ChatUtil.BLUE + secrets.size() + ChatUtil.GREEN + "/" + ChatUtil.BLUE + plugin.getSecretEntries().size());
        getInventory().setContents(content);
    }

    public void onItemClicked(InventoryClickEvent event) {
        if (!mayAffectThisInventory(event)) {
            return;
        }

        event.setCancelled(true);

        if (!getInventory().equals(event.getClickedInventory())) {
            return;
        }

        int slot = event.getSlot();

        if (slot == CLOSE_INDEX) {
            getPlayer().closeInventory();
            return;
        }

        if (slot == END_OF_FIRST_ROW_INDEX) {
            attempScrollUp();
        } else if (slot == END_OF_LAST_ROW_INDEX) {
            attempScrollDown();
        }
    }

    private void attempScrollUp() {
        if (this.scrollAmount <= 0) {
            return;
        }
        this.scrollAmount--;
        rebuildInventory();
    }

    private void attempScrollDown() {
        if (this.scrollAmount >= this.numOfItemRows - NUM_OF_ROWS) {
            return;
        }
        this.scrollAmount++;
        rebuildInventory();
    }
}
