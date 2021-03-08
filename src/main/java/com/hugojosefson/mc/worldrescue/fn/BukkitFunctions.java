package com.hugojosefson.mc.worldrescue.fn;

import com.hugojosefson.mc.worldrescue.commands.PluginCommandNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitFunctions {
  public static String displayName(final Player player) {
    return player == null ? "Admin on the server console" : player.getDisplayName();
  }

  public static String resolveWorldName(final Player player, final String world) {
    if ("me".equals(world) && player != null) {
      return player.getWorld().getName();
    }
    return world;
  }

  @NotNull
  public static World resolveWorld(final Player player, final String worldName) {
    if (worldName != null && !"me".equals(worldName)) {
      return Objects.requireNonNull(Bukkit.getServer().getWorld(worldName));
    }

    if (player == null) {
      throw new IllegalArgumentException("Either player or worldName must be non-null to resolve a valid worldName.");
    }

    return player.getWorld();
  }

  @NotNull
  public static PluginCommand getPluginCommand(final String command) throws PluginCommandNotFoundException {
    final PluginCommand pluginCommand = Bukkit.getPluginCommand(command);
    if (pluginCommand == null) throw new PluginCommandNotFoundException(command);
    return pluginCommand;
  }

}
