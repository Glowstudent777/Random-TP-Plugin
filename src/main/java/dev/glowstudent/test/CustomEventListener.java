package dev.glowstudent.test;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CustomEventListener implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            SafeRandomTeleport.teleportPlayer(player);
            return true;
        }
        sender.sendMessage("Only players can use this command!");
        return false;
    }
}
