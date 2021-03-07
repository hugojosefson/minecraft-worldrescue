package com.hugojosefson.mc.worldrescue.commands;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportablePlayer {

  public final Player player;
  public final Location location;
  public final GameMode gameMode;

  public static TeleportablePlayer of(final Player player) {
    return new TeleportablePlayer(player, player.getLocation(), player.getGameMode());
  }

  public TeleportablePlayer(Player player, Location location, GameMode gameMode) {
    this.player = player;
    this.location = location;
    this.gameMode = gameMode;
  }

  public Player getPlayer() {
    return player;
  }

  public Location getLocation() {
    return location;
  }

  public GameMode getGameMode() {
    return gameMode;
  }
}
