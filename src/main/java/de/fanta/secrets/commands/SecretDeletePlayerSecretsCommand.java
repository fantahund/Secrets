package de.fanta.secrets.commands;

import de.fanta.secrets.Secrets;
import de.fanta.secrets.utils.ChatUtil;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.commands.ArgsParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;

public class SecretDeletePlayerSecretsCommand extends SubCommand {

    private final Secrets plugin;

    public SecretDeletePlayerSecretsCommand(Secrets plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {

        if (args.hasNext()) {
            String secretName = args.getAll("");
                try {
                    plugin.getDatabase().deletePlayerSecret(secretName);
                } catch (SQLException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Secret " + secretName + " could not be deleted.", ex);
                    ChatUtil.sendErrorMessage(sender, plugin.getMessages().getDeleteSecretfromPlayersError(secretName));
                    return true;
                }
                plugin.setUpdateTime();
                plugin.loadSecretsfromDatabase();
                ChatUtil.sendNormalMessage(sender, plugin.getMessages().getDeleteSecretfromPlayersSuccessful(secretName));
        } else {
            ChatUtil.sendErrorMessage(sender, "/secrets deleteplayersecrets <secret>.");
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
        return plugin.getPermissions().getDeletePlayerSecretsPermission();
    }
}
