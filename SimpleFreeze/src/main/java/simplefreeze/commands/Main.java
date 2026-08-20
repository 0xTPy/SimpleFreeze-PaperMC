package simplefreeze.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {

    public static final Set<UUID> frozenPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        this.getCommand("freeze").setExecutor(this);
        this.getCommand("unfreeze").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        frozenPlayers.clear();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (frozenPlayers.contains(player.getUniqueId())) {
            if (event.getFrom().getX() != event.getTo().getX() 
                    || event.getFrom().getY() < event.getTo().getY() 
                    || event.getFrom().getZ() != event.getTo().getZ()) {
                event.setTo(event.getFrom());
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage("§cTu n'as pas la permission d'utiliser cette commande !");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage correct : /" + label + " <joueur>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage("§cJoueur introuvable ou hors-ligne.");
            return true;
        }


        if (command.getName().equalsIgnoreCase("freeze")) {
            
            frozenPlayers.add(target.getUniqueId());
            target.setWalkSpeed(0.0f);
            target.setFlySpeed(0.0f);

            target.sendTitle("§c§lATTENTION", "", 10, 70, 20);
            target.sendMessage(" §c§lTU AS ÉTÉ GELÉ ! §fUn admin va venir vérifier.");
            player.sendMessage("§aTu as gelé le joueur §e" + target.getName());
            return true;
        }

        if (command.getName().equalsIgnoreCase("unfreeze")) {

            frozenPlayers.remove(target.getUniqueId());
            target.setWalkSpeed(0.2f);
            target.setFlySpeed(0.1f);

            target.sendTitle("§c§lATTENTION", "", 10, 70, 20);
            target.sendMessage("§a§lTU AS ÉTÉ DÉGELÉ ! §fFin de la vérification.");
            player.sendMessage("§aTu as dégelé le joueur §e" + target.getName());
            return true;
        }

        return false;
    }
}