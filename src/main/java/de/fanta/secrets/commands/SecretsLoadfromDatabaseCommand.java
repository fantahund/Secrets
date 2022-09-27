package de.fanta.secrets.commands;

import de.fanta.secrets.Secrets;
import de.fanta.secrets.utils.ChatUtil;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.commands.ArgsParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SecretsLoadfromDatabaseCommand extends SubCommand {

    private final Secrets plugin;

    public SecretsLoadfromDatabaseCommand(Secrets plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        plugin.loadSecretsfromDatabase();
        ChatUtil.sendNormalMessage(sender, plugin.getMessages().getReloadDatabase());
        return true;
    }

    @Override
    public String getRequiredPermission() {
        return plugin.getPermissions().getLoadDatabaseSecretsPermission();
    }
}
