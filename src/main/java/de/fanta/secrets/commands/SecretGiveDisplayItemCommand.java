package de.fanta.secrets.commands;

import de.fanta.secrets.SecretEntry;
import de.fanta.secrets.Secrets;
import de.fanta.secrets.utils.ChatUtil;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.commands.ArgsParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;

public class SecretGiveDisplayItemCommand extends SubCommand {

    private final Secrets plugin;

    public SecretGiveDisplayItemCommand(Secrets plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        if (!(sender instanceof Player player)) {
            ChatUtil.sendErrorMessage(sender, "You are not a Player :>");
            return true;
        }

        if (args.hasNext()) {
            String secretName = args.getAll("");
            SecretEntry secretEntry = plugin.getSecretEntrybyName(secretName);
            if (secretEntry != null) {
                ItemStack stack = secretEntry.getDisplayStack();
                HashMap<Integer, ItemStack> itemMap = player.getInventory().addItem(stack);
                if (itemMap.isEmpty()) {
                    ChatUtil.sendNormalMessage(player, plugin.getMessages().getGiveDisplayItemSuccessful(secretEntry.getSecretName()));
                } else {
                    ChatUtil.sendErrorMessage(player, plugin.getMessages().getGiveDisplayItemError(secretEntry.getSecretName()));
                }

            } else {
                ChatUtil.sendErrorMessage(player, plugin.getMessages().getSecretNotExist(secretName));
            }
        } else {
            ChatUtil.sendErrorMessage(player, "/secrets givedisplayitem <secret>.");
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
        return plugin.getPermissions().getGiveDisplayItemPermission();
    }
}
