package io.ashrity.huntercompass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class JoinEvent implements Listener {

    Trackers trackers;
    Logger logger;
    FileConfiguration config;

    public JoinEvent(Trackers trackers, FileConfiguration config) {
        this.trackers = trackers;
        this.logger = Bukkit.getLogger();
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        logger.info(player.getName() + " has joined.");
        ConfigurationSection playerIds = config.getConfigurationSection("playerIDs");

        assert playerIds != null;
        if(playerIds.contains(String.valueOf(player.getUniqueId()))) {
            String target = Objects.requireNonNull(playerIds.get(player.getUniqueId().toString())).toString();
            UUID id = UUID.fromString(target);
            trackers.addPlayerList(player.getUniqueId(), id);
        }

        ConfigurationSection locations = config.getConfigurationSection("locations");
        assert locations != null;
        if(locations.contains(String.valueOf(player.getUniqueId()))) {
            Object loc = locations.get(player.getUniqueId().toString());
            trackers.addPlayerLoc(player.getUniqueId(), (Location) loc);
        }
    }

}
