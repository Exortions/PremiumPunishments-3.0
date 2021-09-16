package com.exortions.premiumpunishments.commands.core.ban;

import com.exortions.premiumpunishments.PremiumPunishments;
import com.exortions.premiumpunishments.objects.command.SubCommand;
import com.exortions.premiumpunishments.objects.minecraftplayer.MinecraftPlayer;
import com.exortions.premiumpunishments.objects.minecraftplayer.MinecraftPlayerRepository;
import com.exortions.premiumpunishments.util.MojangAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class UnbanCommand implements SubCommand {
    @Override
    public String name() {
        return "unban";
    }

    @Override
    public String permission() {
        return "premiumpunishments.unban";
    }

    @Override
    public String usage() {
        return "/premiumpunishments unban <player>";
    }

    @Override
    public String description() {
        return "Unbans a specified player.";
    }

    @Override
    public List<String> tabcompletion() {
        return null;
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public void execute(Player player, String[] args) {

    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            boolean banned = checkBanned(args[0]);

            if (!banned) { sender.sendMessage(prefix() + ChatColor.RED + "That player is not currently banned!"); return; }

            MinecraftPlayer mp = MinecraftPlayerRepository.getPlayerByUuid(Objects.requireNonNull(MojangAPI.getUuidByName(args[0])));

            database().execute("DELETE from players where uuid='" + UUID.fromString(MojangAPI.getUuidByName(args[0])) + "'");
            database().insertPlayer(mp.getUuid(), mp.getUsername(), false, mp.getBanexpirydate(), mp.getMuted(), mp.getMuteexpirydate(), mp.getWarns(), mp.getKicks());
            database().execute("DELETE FROM banned_ips where uuid='" + UUID.fromString(MojangAPI.getUuidByName(args[0])) + "'");

            sender.sendMessage(prefix() + ChatColor.WHITE + "Successfully unbanned " + args[0] + "!");
        } else { if (sender instanceof Player) ((Player) sender).performCommand("premiumpunishments help unban"); else Bukkit.dispatchCommand(sender, "premiumpunishments help unban"); }
    }

    public boolean checkBanned(String name) {
        ResultSet set = database().query("SELECT username FROM bans");

        boolean isBanned = false;

        try {
            while (set.next()) {
                if (set.getString("username").equalsIgnoreCase(name)) isBanned = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isBanned;
    }

}
