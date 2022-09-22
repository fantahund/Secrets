package de.fanta.secrets.commands;

import de.fanta.secrets.SecretEntry;
import de.fanta.secrets.Secrets;
import de.fanta.secrets.utils.ChatUtil;
import de.fanta.secrets.utils.ItemStackUtil;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.commands.ArgsParser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public class SecretDeleteSecretCommand extends SubCommand {

    private final Secrets plugin;

    public SecretDeleteSecretCommand(Secrets plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {

        if (args.hasNext()) {
            String secretName = args.getNext();
            SecretEntry secretEntry = plugin.getSecretEntrybyName(secretName);
            if (secretEntry != null) {
                try {
                    plugin.getDatabase().deleteSecret(secretEntry.getSecretName());
                } catch (SQLException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Secret " + secretEntry.getSecretName() + " could not be deleted.", ex);
                    ChatUtil.sendErrorMessage(sender, "Fehler beim löschen von Secret " + ChatUtil.BLUE + secretEntry.getSecretName());
                    return true;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    plugin.getPlayerSecrets(player).remove(secretEntry);
                }

                plugin.removeSecrets(secretEntry);
                plugin.setUpdateTime();
                plugin.loadSecretsfromDatabase();
                ChatUtil.sendNormalMessage(sender, "Du hast das Secret " + ChatUtil.BLUE + secretEntry.getSecretName() + ChatUtil.GREEN + " erfolgreich gelöscht.");
            } else {
                ChatUtil.sendErrorMessage(sender, "Das Secret " + secretName + " existiert nicht.");
            }
        } else {
            ChatUtil.sendErrorMessage(sender, "/secrets deletesecret <secret>.");
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
        return plugin.getPermissions().getDeleteSecretPermission();
    }
}
