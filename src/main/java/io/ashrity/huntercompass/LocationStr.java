package io.ashrity.huntercompass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationStr {

    double x, y, z;
    float yaw, pitch;
    String world;

    public LocationStr() {

    }

    public LocationStr(Location loc) {
        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();
        yaw = loc.getYaw();
        pitch = loc.getPitch();
        world = loc.getWorld().getName();
    }

    public Location fromString(String str) {
        String[] temp = str.split(" ");
        x = Double.parseDouble(temp[0]);
        y = Double.parseDouble(temp[1]);
        z = Double.parseDouble(temp[2]);
        yaw = Float.parseFloat(temp[3]);
        pitch = Float.parseFloat(temp[4]);
        world = temp[5];
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public String toString() {
        return x + " " + y + " " + z + " " + yaw + " " + pitch;
    }

}
