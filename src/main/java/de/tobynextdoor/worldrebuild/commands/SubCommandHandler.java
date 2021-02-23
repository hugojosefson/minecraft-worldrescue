package de.tobynextdoor.worldrebuild.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

import static de.tobynextdoor.worldrebuild.commands.Commands.sendMessage;

/**
 * Handles one subcommand.
 */
class SubCommandHandler {
  private final String handlesSubcommand;
  private final String requiredPermission;
  private final BiFunction<Player, String[], Boolean> action;

  SubCommandHandler(final String subcommand, final BiFunction<Player, String[], Boolean> action) {
    this.handlesSubcommand = subcommand;
    this.requiredPermission = "worldrebuild." + subcommand;
    this.action = action;
  }

  public boolean handles(final String subCommand) {
    return handlesSubcommand.equalsIgnoreCase(subCommand);
  }

  public boolean handle(final Player player, final String[] args) {
    final String subCommand = args[0];
    if (!handles(subCommand)) {
      throw new IllegalStateException("SubCommandHandler for '" + handlesSubcommand + "' should not be asked to handle '" + subCommand + "'.");
    }

    if (player == null || player.hasPermission(requiredPermission)) {
      return action.apply(player, args);
    }

    sendMessage(player, ChatColor.DARK_RED + "You lack the permission " + requiredPermission + ", which is required for '/wr " + subCommand + "'.");
    return true;
  }
}
