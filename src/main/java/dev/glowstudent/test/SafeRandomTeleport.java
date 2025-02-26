package dev.glowstudent.test;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class SafeRandomTeleport {
    private static final Random random = new Random();
    private static final List<Material> harmfulBlocks = Arrays.asList(
            // Pressure Plates
            Material.STONE_PRESSURE_PLATE,
            Material.OAK_PRESSURE_PLATE,
            Material.SPRUCE_PRESSURE_PLATE,
            Material.BIRCH_PRESSURE_PLATE,
            Material.JUNGLE_PRESSURE_PLATE,
            Material.ACACIA_PRESSURE_PLATE,
            Material.DARK_OAK_PRESSURE_PLATE,
            Material.CRIMSON_PRESSURE_PLATE,
            Material.WARPED_PRESSURE_PLATE,
            Material.POLISHED_BLACKSTONE_PRESSURE_PLATE,
            Material.LIGHT_WEIGHTED_PRESSURE_PLATE, // Gold
            Material.HEAVY_WEIGHTED_PRESSURE_PLATE, // Iron

            // Tripwire and Triggers
            Material.TRIPWIRE_HOOK,
            Material.TRIPWIRE,

            // Explosives
            Material.TNT,

            // Fire and Lava
            Material.FIRE,
            Material.SOUL_FIRE,
            Material.LAVA,
            Material.MAGMA_BLOCK,
            Material.CAMPFIRE,
            Material.SOUL_CAMPFIRE
    );

    public static void teleportPlayer(Player player) {
        World world = player.getWorld();
        Location originalLoc = player.getLocation();

        int startX = originalLoc.getBlockX() + random.nextInt(201) - 100;
        int startZ = originalLoc.getBlockZ() + random.nextInt(201) - 100;
        int startY = world.getHighestBlockYAt(startX, startZ);

        Location startLocation = new Location(world, startX + 0.5, startY + 1, startZ + 0.5);

        if (isSafe(startLocation)) {
            player.teleport(startLocation);
            player.sendMessage("Teleported to a safe location!");
            return;
        }

        Location safeLoc = findNearestSafeSpot(startLocation, 150);

        if (safeLoc != null) {
            player.teleport(safeLoc);
            player.sendMessage("Teleported to a safe location!");
        } else {
            player.sendMessage("Couldn't find a safe spot nearby.");
        }
    }

    private static Location findNearestSafeSpot(Location start, int maxRadius) {
        World world = start.getWorld();
        Queue<Location> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            Location loc = queue.poll();

            if (isSafe(loc)) {
                return loc;
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        int newX = loc.getBlockX() + dx;
                        int newY = loc.getBlockY() + dy;
                        int newZ = loc.getBlockZ() + dz;

                        if (newY < world.getMinHeight() || newY > world.getMaxHeight()) continue;

                        Location newLoc = new Location(world, newX + 0.5, newY, newZ + 0.5);
                        String key = newX + "," + newY + "," + newZ;

                        if (!visited.contains(key) && newLoc.distance(start) <= maxRadius) {
                            queue.add(newLoc);
                            visited.add(key);
                        }
                    }
                }
            }
        }
        return null;
    }

    private static boolean isSafe(Location loc) {
        World world = loc.getWorld();
        boolean safe = false;

        Block feet = world.getBlockAt(loc);
        Block head = world.getBlockAt(loc.clone().add(0, 1, 0));
        Block below = world.getBlockAt(loc.clone().add(0, -1, 0));

        // Instant Returns
        if ((!feet.getType().isAir() && !feet.isPassable()) || harmfulBlocks.contains(feet.getType())) return false;
        if ((!head.getType().isAir() && !head.isPassable()) || harmfulBlocks.contains(head.getType()) || head.getType() == Material.WATER) return false;
        if (below.getType().isAir() || below.isPassable() || harmfulBlocks.contains(below.getType())) return false;

        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};

        for (int[] dir : directions) {
            int dx = dir[0];
            int dz = dir[1];

            if (world.getBlockAt(loc.clone().add(dx, 0, dz)).getType().isAir() || world.getBlockAt(loc.clone().add(dx, 1, dz)).getType().isAir()) safe = true;
        }

        return safe;
    }
}
