package de.fanta.secrets.commands;

import de.fanta.secrets.SecretEntry;
import de.fanta.secrets.Secrets;
import de.fanta.secrets.guis.SecretsFoungGui;
import de.fanta.secrets.utils.ChatUtil;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.commands.ArgsParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SecretListCommand extends SubCommand {

    private final Secrets plugin;

    public SecretListCommand(Secrets plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        if (!(sender instanceof Player player)) {
            ChatUtil.sendErrorMessage(sender, "You are not a Player :>");
            return true;
        }

        List<SecretEntry> secretEntries = plugin.getPlayerSecrets(player);
        new SecretsFoungGui(secretEntries, player, plugin).open();

        return true;
    }

    @Override
    public String getRequiredPermission() {
        return plugin.getPermissions().getSecretListPermission();
    }
}
