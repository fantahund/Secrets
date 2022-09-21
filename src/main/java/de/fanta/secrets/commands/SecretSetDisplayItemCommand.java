package de.fanta.secrets.commands;

import de.fanta.secrets.SecretEntry;
import de.fanta.secrets.Secrets;
import de.fanta.secrets.utils.ChatUtil;
import de.fanta.secrets.utils.ItemStackUtil;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.commands.ArgsParser;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;

public class SecretSetDisplayItemCommand extends SubCommand {
    private final Secrets plugin;

    public SecretSetDisplayItemCommand(Secrets plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        if (!(sender instanceof Player player)) {
            ChatUtil.sendErrorMessage(sender, "You are not a Player :>");
            return true;
        }

        if (args.hasNext()) {
            ItemStack displayItem = player.getInventory().getItemInMainHand();
            if (displayItem.getType() != Material.AIR) {
                String secretName = args.getNext();
                SecretEntry secretEntry = plugin.getSecretEntrybyName(secretName);
                if (secretEntry != null) {
                    secretEntry.setDisplayStack(displayItem);
                    try {
                        plugin.getDatabase().updateSecretDisplayItem(ItemStackUtil.createDisplayItemString(displayItem), secretEntry.getSecretName());
                    } catch (SQLException ex) {
                        plugin.getLogger().log(Level.SEVERE, "Display item for Secret " + secretEntry.getSecretName() + " could not be set.", ex);
                    }
                    ChatUtil.sendNormalMessage(player, "Du hast das DisplayItem für Secret " + ChatUtil.BLUE + secretEntry.getSecretName() + ChatUtil.GREEN + " erfolgreich gesetzt.");
                } else {
                    ChatUtil.sendErrorMessage(player, "Das Secret " + secretName + " existiert nicht.");
                }
            } else {
                ChatUtil.sendErrorMessage(player, "Du musst ein DisplayItem in dir hand nehmen.");
                return true;
            }
        } else {
            ChatUtil.sendErrorMessage(player, "/secrets setdisplayitem <secret>.");
            return true;
        }


        return true;
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        return plugin.getSecretEntries().keySet();
    }

    @Override
    public String getRequiredPermission() {
        return plugin.getPermissions().getSetDisplayItemPermission();
    }
}


