//
// Decompiled by Procyon v0.5.36
//

package de.tobynextdoor.worldrebuild;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class WorldRebuildCommands implements CommandExecutor {
  public final WorldRebuild plugin;
  final WorldRebuildIo io;
  boolean isSuccess;

  private final SubCommandHandler[] subCommandHandlers = new SubCommandHandler[]{
    new SubCommandHandler("save", this::saveRebuild),
    new SubCommandHandler("rebuild", this::saveRebuild),
    new SubCommandHandler("delete", this::delete),
    new SubCommandHandler("list", this::list),
    new SubCommandHandler("duplicate", this::duplicate),
    new SubCommandHandler("tp", this::tp)
  };

  public WorldRebuildCommands(final WorldRebuild plugin) {
    this.io = new WorldRebuildIo();
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String commandLabel, final String[] args) {
    final Player player = sender instanceof Player
      ? (Player) sender
      : null;

    if (args.length == 0) {
      return help(player);
    }

    final String subCommand = args[0];

    return Arrays.stream(subCommandHandlers)
      .filter(handler -> handler.handles(subCommand))
      .findFirst()
      .map(handler -> handler.handle(player, args))
      .orElseGet(() -> help(player));
  }

  public boolean delete(final Player player, final String[] args) {
    if (args.length == 3) {
      if (args[1].equals("me") && player != null) {
        args[1] = player.getWorld().getName();
      }
      final String world = args[1] + "_" + args[2] + "#backup";
      this.isSuccess = this.io.delete(world);
      if (this.isSuccess) {
        Bukkit.getServer().broadcastMessage(player.getDisplayName() + ChatColor.GREEN + " deleted the backup '" + args[1] + "' (" + args[2] + ").");
      } else {
        sendMessage(player, ChatColor.DARK_RED + "The backup '" + args[1] + "' (" + args[2] + ") does not exist.");
      }
    } else {
      help(player);
    }
    return true;
  }

  public boolean duplicate(final Player player, final String[] args) {
    if (args.length > 1) {
      if (args[1].equals("me") && player != null) {
        args[1] = player.getWorld().getName();
      }
      Bukkit.getScheduler().runTask(this.plugin, () -> {
        final String world = args[1];
        final String nWorld = args[1] + "-new";
        sendMessage(player, ChatColor.GOLD + "Creating a copy of the world '" + world + "' with the name '" + nWorld + "'. This may take a while.");
        create(nWorld);
        unload(nWorld, true);
        final WorldRebuildIo IO = new WorldRebuildIo();
        WorldRebuildCommands.this.isSuccess = IO.copy(world, nWorld);
        if (WorldRebuildCommands.this.isSuccess) {
          IO.delete(nWorld + "/uid.dat");
          load(nWorld);
          if (hasMultiverse()) {
            sendMessage(player, ChatColor.GOLD + "Done. Now just type '" + ChatColor.GREEN + "/mvtp " + nWorld + ChatColor.GOLD + "' and use this world to continue building.");
          } else {
            sendMessage(player, ChatColor.GOLD + "Done. Now just type '" + ChatColor.GREEN + "/wr tp " + nWorld + ChatColor.GOLD + "' and use this world to continue building.");
          }
        } else {
          sendMessage(player, ChatColor.DARK_RED + "The world '" + args[1] + " does not exist.");
          delete(nWorld);
        }
      });
    } else {
      help(player);
    }
    return true;
  }

  public boolean list(final Player player, final String[] args) {
    if ((args.length > 0 && player != null) || (player == null && args.length > 1)) {
      String listWorld;
      if (args.length == 1) {
        listWorld = player.getWorld().getName();
      } else {
        if (args[1].equals("me") && player != null) {
          args[1] = player.getWorld().getName();
        }
        listWorld = args[1];
      }
      int reqworldex = 0;
      String[] worlds = new String[100];
      final String[] reqworldbackex = new String[100];
      worlds = this.io.list(worlds, listWorld);
      if (worlds[0].equals("#")) {
        sendMessage(player, ChatColor.DARK_RED + "The world '" + listWorld + "' does not exist.");
        return true;
      }
      for (int i = 0; i < worlds.length && worlds[i] != null; ++i) {
        if (worlds[i].split("'")[1].equals(listWorld)) {
          reqworldbackex[reqworldex] = worlds[i];
          ++reqworldex;
        }
      }
      sendMessage(player, ChatColor.GOLD + "There are " + reqworldex + " backups:");
      for (int i = 0; i < reqworldex; ++i) {
        sendMessage(player, ChatColor.GREEN + reqworldbackex[i]);
      }
      if (reqworldex == 0) {
        sendMessage(player, "There are no backups of the world '" + listWorld + "'.");
      }
    } else {
      help(player);
    }
    return true;
  }

  private static boolean help(final Player player) {
    sendMessage(player, " ");
    sendMessage(player, ChatColor.GOLD + "<needed> [optional]");
    sendMessage(player, ChatColor.GOLD + "'" + ChatColor.GREEN + "/wr help" + ChatColor.GOLD + "' --> Lists all commands of WorldRebuild.");
    sendMessage(player, ChatColor.GOLD + "'" + ChatColor.GREEN + "/wr list [world]" + ChatColor.GOLD + "' --> Lists all backups of a world.");
    sendMessage(player, ChatColor.GOLD + "'" + ChatColor.GREEN + "/wr save [world] [index]" + ChatColor.GOLD + "' --> Saves your chosen world.");
    sendMessage(player, ChatColor.GOLD + "'" + ChatColor.GREEN + "/wr rebuild [world] [index]" + ChatColor.GOLD + "' --> Rebuilds your chosen world.");
    sendMessage(player, ChatColor.GOLD + "'" + ChatColor.GREEN + "/wr delete <world> <index>" + ChatColor.GOLD + "' --> Deletes the chosen backup.");
    sendMessage(player, ChatColor.GOLD + "'" + ChatColor.GREEN + "/wr duplicate <world>" + ChatColor.GOLD + "' --> Duplicate the chosen world.");
    sendMessage(player, ChatColor.GOLD + "'" + ChatColor.GREEN + "/wr tp <world>" + ChatColor.GOLD + "' --> Teleport to a world.");
    sendMessage(player, ChatColor.GOLD + "If you don't know the name of a world, type 'me' instead.");
    return true;
  }

  public boolean saveRebuild(final Player player, final String[] args) {
    if ((args.length > 0 && player != null) || (player == null && args.length > 1)) {
      Bukkit.getScheduler().runTask(this.plugin, () -> {
        String world;
        String arg;
        String backup;
        if (args.length == 1) {
          arg = "default";
          world = player.getWorld().getName();
          backup = player.getWorld().getName() + "_default" + "#backup";
        } else if (args.length == 2) {
          if (args[1].equals("me") && player != null) {
            args[1] = player.getWorld().getName();
          }
          arg = "default";
          world = args[1];
          backup = args[1] + "_default" + "#backup";
        } else {
          if (args[1].equals("me") && player != null) {
            args[1] = player.getWorld().getName();
          }
          arg = args[2];
          world = args[1];
          backup = args[1] + "_" + arg + "#backup";
        }
        if (args[0].equalsIgnoreCase("save")) {
          WorldRebuildCommands.sendMessage(player, ChatColor.GOLD + "Saving the world '" + world + "' (" + arg + ")...");
        } else {
          WorldRebuildCommands.sendMessage(player, ChatColor.GOLD + "Rebuilding the world '" + world + "' (" + arg + ")...");
        }
        final Player[] playerInWorld = new Player[Bukkit.getOnlinePlayers().size()];
        final Location[] playerInWorldLoc = new Location[Bukkit.getOnlinePlayers().size()];
        final GameMode[] playerInWorldLGM = new GameMode[Bukkit.getOnlinePlayers().size()];
        int i = 0;
        boolean isDefault = false;
        if (Bukkit.getServer().getWorld(world) == Bukkit.getServer().getWorlds().get(0)) {
          isDefault = true;
        }
        if (isDefault) {
          WorldRebuildCommands.sendMessage(player, ChatColor.RED + "The world '" + world + "' is your default world and due to a Bukkit restrictment WorldRebuild can not create/restore a backup.");
          WorldRebuildCommands.sendMessage(player, ChatColor.RED + "To solve this problem, type '" + ChatColor.GREEN + "/wr duplicate <world name>" + ChatColor.RED + "'.");
          WorldRebuildCommands.sendMessage(player, ChatColor.RED + "This will create the new world '" + world + "-new' which will be the same as the world '" + world + "' and WorldRebuild will be abled to create/restore backups from this world.");
          WorldRebuildCommands.sendMessage(player, ChatColor.RED + "You can also open your server.config and change the point 'level-name' to another world.");
        } else {
          load(Bukkit.getServer().getWorlds().get(0).getName());
          for (final Player pInWorld : Bukkit.getServer().getOnlinePlayers()) {
            if (pInWorld.getWorld().getName().equalsIgnoreCase(world)) {
              playerInWorld[i] = pInWorld;
              playerInWorldLoc[i] = pInWorld.getLocation();
              playerInWorldLGM[i] = pInWorld.getGameMode();
              if (!WorldRebuildCommands.hasMultiverse()) {
                teleport(pInWorld, Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
              }
              ++i;
            }
          }
          unload(world, true);
          if (args[0].equalsIgnoreCase("save")) {
            WorldRebuildCommands.this.isSuccess = WorldRebuildCommands.this.io.copy(world, backup);
          } else {
            WorldRebuildCommands.this.isSuccess = WorldRebuildCommands.this.io.copy(backup, world);
          }
          Bukkit.getScheduler().runTask(WorldRebuildCommands.this.plugin, () -> {
            load(world);
            boolean loaded = false;
            while (!loaded) {
              loaded = Bukkit.getServer().getWorlds().contains(Bukkit.getWorld(world));
              if (loaded) {
                Bukkit.getScheduler().runTaskLater(WorldRebuildCommands.this.plugin, () ->
                  IntStream
                    .range(0, playerInWorld.length)
                    .filter(i12 -> playerInWorld[i12] != null)
                    .forEach(i12 -> teleport(playerInWorld[i12], playerInWorldLoc[i12])), 10L);
                Bukkit.getScheduler().runTaskLater(WorldRebuildCommands.this.plugin, () ->
                  IntStream
                    .range(0, playerInWorld.length)
                    .filter(i1 -> playerInWorld[i1] != null)
                    .forEach(i1 -> playerInWorld[i1].setGameMode(playerInWorldLGM[i1])), 20L);
              }
            }
          });
          if (WorldRebuildCommands.this.isSuccess && player != null) {
            if (args[0].equalsIgnoreCase("save")) {
              Bukkit.getServer().broadcastMessage(player.getDisplayName() + ChatColor.GREEN + " saved the world '" + world + "' (" + arg + ").");
            } else {
              Bukkit.getServer().broadcastMessage(player.getDisplayName() + ChatColor.GREEN + " rebuilt the world '" + world + "' (" + arg + ").");
            }
          } else if (player != null) {
            if (args[0].equalsIgnoreCase("save")) {
              WorldRebuildCommands.sendMessage(player, ChatColor.DARK_RED + "The world '" + world + "' with index '" + arg + "' does not exist.");
            } else {
              WorldRebuildCommands.sendMessage(player, ChatColor.DARK_RED + "A backup of the world '" + world + "' with index '" + arg + "' does not exist.");
            }
          }
        }
      });
    } else {
      help(player);
    }
    return true;
  }

  public boolean tp(final Player player, final String[] args) {
    Bukkit.getServer().createWorld(new WorldCreator(args[1]));
    teleport(player, Bukkit.getServer().getWorld(args[1]).getSpawnLocation());
    return true;
  }

  static void create(final String world) {
    if (hasMultiverse()) {
      Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mv create " + world + " normal");
    } else {
      Bukkit.getServer().createWorld(new WorldCreator(world));
    }
  }

  void load(final String world) {
    if (hasMultiverse()) {
      Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mv load " + world);
    } else {
      Bukkit.getServer().createWorld(new WorldCreator(world));
    }
  }

  void unload(final String world, final boolean saveChunksBeforeUnloading) {
    if (hasMultiverse()) {
      Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mv unload " + world);
    } else {
      Bukkit.getServer().unloadWorld(world, saveChunksBeforeUnloading);
    }
  }

  void teleport(final Player player, final Location location) {
    if (hasMultiverse()) {
      Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mvtp " + player.getName() + " e:" + location.getWorld().getName() + ":" + location.getX() + "," + location.getY() + "," + location.getZ());
    } else {
      player.teleport(location);
    }
  }

  void delete(final String world) {
    if (hasMultiverse()) {
      Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mv load " + world);
      Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mv delete " + world);
      Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mv confirm");
    } else {
      this.io.delete(world);
    }
  }

  private static boolean hasMultiverse() {
    return Bukkit.getServer().getPluginManager().isPluginEnabled("Multiverse-Core");
  }

  static void sendMessage(final Player receiver, final String message) {
    if (receiver == null) {
      System.out.println("[WorldRebuild] " + message);
      return;
    }
    receiver.sendMessage(message);
  }

}

class SubCommandHandler {
  private final String subCommandToHandle;
  private final String requiredPermission;
  private final BiFunction<Player, String[], Boolean> action;

  SubCommandHandler(final String subCommandToHandle, final BiFunction<Player, String[], Boolean> action) {
    this.subCommandToHandle = subCommandToHandle;
    this.requiredPermission = "worldrebuild." + subCommandToHandle;
    this.action = action;
  }

  public boolean handles(final String subCommand) {
    return subCommandToHandle.equalsIgnoreCase(subCommand);
  }

  public boolean handle(final Player player, final String[] args) {
    if (player == null || player.hasPermission(requiredPermission)) {
      return action.apply(player, args);
    }

    WorldRebuildCommands.sendMessage(player, ChatColor.DARK_RED + "You lack permission " + requiredPermission + ", which is required for this action.");
    return true;
  }
}
