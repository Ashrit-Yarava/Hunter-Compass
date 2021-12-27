package io.ashrity.huntercompass;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public final class HunterCompass extends JavaPlugin implements Listener {

    Logger logger = Bukkit.getLogger();
    Trackers trackers = new Trackers();
    JoinEvent event = new JoinEvent(trackers, getConfig());

    @Override
    public void onEnable() {

        Objects.requireNonNull(this.getCommand("compass")).setExecutor(new CommandKit());
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            trackers.updateCompass();
        }, 0L, 20);
        this.getServer().getPluginManager().registerEvents(event, this);

    }

    @Override
    public void onDisable() {

        setPreviousPlayers(trackers.getPlayerList());
        setPreviousLocations(trackers.getLocations());
        saveConfig();
        reloadConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        logger.info(player.getName() + " has joined.");
        ConfigurationSection playerIds = getConfig().getConfigurationSection("playerIDs");

        assert playerIds != null;
        if(playerIds.contains(String.valueOf(player.getUniqueId()))) {
            String target = Objects.requireNonNull(playerIds.get(player.getUniqueId().toString())).toString();
            UUID id = UUID.fromString(target);
            trackers.addPlayerList(player.getUniqueId(), id);
        }

        ConfigurationSection locations = getConfig().getConfigurationSection("locations");
        assert locations != null;
        if(locations.contains(String.valueOf(player.getUniqueId()))) {
            String loc = (String) locations.get(player.getUniqueId().toString());
            LocationStr str = new LocationStr();
            assert loc != null;
            trackers.addPlayerLoc(player.getUniqueId(), str.fromString(loc));
        }
    }

    /*
    * Create section playerIDs.
    * Add each id pair to the section.
    * */
    private void setPreviousPlayers(@NotNull HashMap<UUID, UUID> playerList) {
        ConfigurationSection section = getConfig().createSection("playerIDs");
        for(UUID id: playerList.keySet()) {
            UUID otherId = playerList.get(id);
            section.set(id.toString(), otherId.toString());
        }
    }

    private void setPreviousLocations(@NotNull HashMap<UUID, Location> locations) {

        ConfigurationSection section = getConfig().createSection("locations");
        for(UUID id: locations.keySet()) {
            Location loc = locations.get(id);
            LocationStr str = new LocationStr(loc);
            section.set(id.toString(), str.toString());
        }

    }

    public class CommandKit implements CommandExecutor {

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                if(args.length == 1) { // compass give, compass <player-name>
                    if(args[0].equals("give")) { // compass give
                        giveCompass(player);
                    } else { // compass <player-name>
                        if(Bukkit.getPlayer(args[0]) != null)
                            trackers.setTracker(player, Objects.requireNonNull(Bukkit.getPlayer(args[0])));
                        else
                            player.sendMessage(ChatColor.GRAY + "Player does not exist!");
                    }
                } else { // compass
                    helpMessage(player);
                }
            }
            return true;
        }

        /*
        * Give a player a customized compass.
        * */
        private void giveCompass(Player player) {
            ItemStack compassItem = new ItemStack(Material.COMPASS);
            ItemMeta meta = compassItem.getItemMeta();
            assert meta != null;
            meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
            meta.setDisplayName(ChatColor.GOLD + "Hunter's Compass");
            meta.setLore(Collections.singletonList("When In Doubt, Be Still.\nThe Compass Of Your Soul\nWill Tell You The Way To Go."));
            compassItem.setItemMeta(meta);
            player.getInventory().addItem(compassItem);
            player.sendMessage(ChatColor.GRAY + "New Compass received.");
        }

        /*
        * Display the help message for a player.
        * */
        private void helpMessage(Player player) {
            player.sendMessage(ChatColor.GRAY + "Usage: /compass give, /compass <player-name>");
            player.sendMessage(ChatColor.GRAY + "Currently Tracking:");
            Player p = Bukkit.getPlayer(trackers.getTracker(player));
            if(p != null)
                player.sendMessage(ChatColor.RED + p.getName());
            else
                player.sendMessage(ChatColor.RED + "World Spawn");
        }
    }

}
