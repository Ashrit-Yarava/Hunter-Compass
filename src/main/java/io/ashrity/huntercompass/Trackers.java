package io.ashrity.huntercompass;

import it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Trackers {

    private final HashMap<UUID, UUID> playerList;
    private final HashMap<UUID, Location> locations;

    public Trackers() {
        playerList = new HashMap<>();
        locations = new HashMap<>();
    }

    /*
    * Update locations and change player compass targets.
    * */
    public void updateCompass() {
        updateList();
        for(UUID id: locations.keySet())
            if(Bukkit.getPlayer(id) != null)
                Objects.requireNonNull(Bukkit.getPlayer(id)).setCompassTarget(locations.get(id));
    }

    public HashMap<UUID, UUID> getPlayerList() { return playerList; }

    public void addPlayerList(UUID p, UUID t) { playerList.put(p, t); }

    public HashMap<UUID, Location> getLocations() { return locations; }

    public void addPlayerLoc(UUID p, Location loc) { locations.put(p, loc); }

    /*
    * Check through playerList, if both players are online and in the same world, update locations.
    * */
    private void updateList() {
        for(UUID id: playerList.keySet()) {
            Player player = Bukkit.getPlayer(id);
            Player target = Bukkit.getPlayer(playerList.get(id));
            if(player == null || target == null)
                continue;
            if(!player.getLocation().getWorld().equals(target.getLocation().getWorld()))
                continue;
            locations.put(id, target.getLocation());
        }
    }

    /*
    * Ensure that both players are active and then add target to playerList.
    * */
    public void setTracker(Player player, Player target) { playerList.put(player.getUniqueId(), target.getUniqueId()); }

    public UUID getTracker(Player player) {
        return playerList.getOrDefault(player.getUniqueId(), null);
    }
}