package org.bhop.bhopPlugin;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BhopPlugin extends JavaPlugin implements Listener {

    private double speedBoost;
    private double maxSpeed;

    private final Map<UUID, Double> playerSpeed = new HashMap<>();
    private final Map<UUID, Long> groundTime = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("BhopPlugin enabled!");
    }

    @Override
    public void onDisable() {
        playerSpeed.clear();
        groundTime.clear();
        getLogger().info("BhopPlugin disabled!");
    }

    private void loadConfigValues() {
        speedBoost = getConfig().getDouble("bhop.speed-boost");
        maxSpeed   = getConfig().getDouble("bhop.max-speed");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        reloadConfig();
        loadConfigValues();
        sender.sendMessage("§aBhop config reloaded!");
        return true;
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!player.hasPermission("bhop.use")) return;

        groundTime.remove(id);

        getServer().getScheduler().runTaskLater(this, () -> {
            Vector current = player.getVelocity();
            double horizSpeed = Math.sqrt(current.getX() * current.getX() + current.getZ() * current.getZ());

            double storedSpeed = playerSpeed.getOrDefault(id, horizSpeed);
            double newSpeed = Math.min(storedSpeed * speedBoost, maxSpeed);
            playerSpeed.put(id, newSpeed);

            if (horizSpeed > 0.01) {
                double scale = newSpeed / horizSpeed;
                player.setVelocity(new Vector(
                        current.getX() * scale,
                        current.getY(),
                        current.getZ() * scale
                ));
            }
        }, 1L);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (player.isInWater()) {
            playerSpeed.remove(id);
            groundTime.remove(id);
            return;
        }

        if (!playerSpeed.containsKey(id)) return;

        if (player.isOnGround()) {
            groundTime.putIfAbsent(id, System.currentTimeMillis());
            long timeOnGround = System.currentTimeMillis() - groundTime.getOrDefault(id, System.currentTimeMillis());
            if (timeOnGround > 500) {
                playerSpeed.remove(id);
                groundTime.remove(id);
            }
        } else {
            groundTime.remove(id);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        playerSpeed.remove(id);
        groundTime.remove(id);
    }
}