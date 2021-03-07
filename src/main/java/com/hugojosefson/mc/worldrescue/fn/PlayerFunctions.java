package com.hugojosefson.mc.worldrescue.fn;

import org.bukkit.entity.Player;

public class PlayerFunctions {
  public static String displayName(final Player player) {
    return player == null ? "Admin on the server console" : player.getDisplayName();
  }
}
