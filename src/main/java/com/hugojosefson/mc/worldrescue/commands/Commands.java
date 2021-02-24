//
// Decompiled by Procyon v0.5.36
//

package com.hugojosefson.mc.worldrescue.commands;

import com.hugojosefson.mc.worldrescue.io.Io;
import com.hugojosefson.mc.worldrescue.WorldRescue;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

public class Commands implements CommandExecutor {
  public final WorldRescue plugin;

  private final SubCommandHandler[] subCommandHandlers = new SubCommandHandler[]{
    new SubCommandHandler("save", this::saveRebuild),
    new SubCommandHandler("rebuild", this::saveRebuild),
    new SubCommandHandler("delete", this::delete),
    new SubCommandHandler("list", this::listBackups),
    new SubCommandHandler("duplicate", this::duplicate),
    new SubCommandHandler("tp", this::tp)
  };

  public Commands(final WorldRescue plugin) {
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
      final String world = getWorldWithAnyMe(player, args[1]);
      final String backupIndex = args[2];

      if (Io.delete(Paths.get(world + "_" + backupIndex + "#backup"))) {
        final String playerDisplayName = player == null ? "Admin on the server console" : player.getDisplayName();
        Bukkit.getServer().broadcastMessage(playerDisplayName + ChatColor.GREEN + " deleted the backup '" + world + "' (" + backupIndex + ").");
      } else {
        sendMessage(player, ChatColor.DARK_RED + "The backup '" + world + "' (" + backupIndex + ") does not exist.");
      }
    } else {
      help(player);
    }
    return true;
  }

  public boolean duplicate(final Player player, final String[] args) {
    if (args.length <= 1) {
      help(player);
      return true;
    }

    final String world = getWorldWithAnyMe(player, args[1]);
    final String newWorld = world + "-new"; // TODO: Make sure it doesn't already exist. If so, append a number. Keep looking until an available name+number is found.

    Bukkit.getScheduler().runTask(plugin, () -> {
      sendMessage(player, ChatColor.GOLD + "Creating a copy of the world '" + world + "' with the name '" + newWorld + "'. This may take a while.");
      create(newWorld);
      unload(newWorld, true);
      if (Io.copy(Paths.get(world), Paths.get(newWorld))) {
        Io.delete(Paths.get(newWorld, "uid.dat"));
        load(newWorld);
        final String tpCommand = hasMultiverse() ? "/mvtp" : "/wr tp";
        sendMessage(player, ChatColor.GOLD + "Done. Now just type '" + ChatColor.GREEN + tpCommand + " " + newWorld + ChatColor.GOLD + "' and use this world to continue building.");
      } else {
        sendMessage(player, ChatColor.DARK_RED + "Copying world '" + world + " failed. Does it even exist?");
        delete(newWorld);
      }
    });
    return true;
  }

  private static String getWorldWithAnyMe(final Player player, final String world) {
    if ("me".equals(world) && player != null) {
      return player.getWorld().getName();
    }
    return world;
  }

  private static String getWorldFromListBackupsArgs(final Player player, final String[] args) {
    if (args.length == 1) {
      return player.getWorld().getName();
    }
    return getWorldWithAnyMe(player, args[1]);
  }

  public boolean listBackups(final Player player, final String[] args) {
    if ((args.length <= 0 || player == null) && (player != null || args.length <= 1)) {
      help(player);
      return true;
    }

    final String world = getWorldFromListBackupsArgs(player, args);
    final Optional<String[]> maybeBackupIndices = Io.listBackups(Paths.get(world));

    if (!maybeBackupIndices.isPresent()) {
      sendMessage(player, ChatColor.DARK_RED + "The world '" + world + "' does not exist.");
      return true;
    }
    final String[] backupIndices = maybeBackupIndices.get();


    if (backupIndices.length == 0) {
      sendMessage(player, "There are no backups of the world '" + world + "'.");
      return true;
    }

    sendMessage(player, ChatColor.GOLD + "There are " + backupIndices.length + " backups:");
    for (String backupIndex : backupIndices) {
      sendMessage(player, ChatColor.GREEN + "   '" + world + "' (" + backupIndex + ")");
    }
    return true;
  }

  private static boolean help(final Player player) {
    sendMessage(player, " ");
    sendMessage(player, ChatColor.GOLD + "<needed> [optional]");
    sendMessage(player, ChatColor.GOLD + "'" + ChatColor.GREEN + "/wr help" + ChatColor.GOLD + "' --> Lists all commands of WorldRescue.");
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
      Bukkit.getScheduler().runTask(plugin, () -> {
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
          Commands.sendMessage(player, ChatColor.GOLD + "Saving the world '" + world + "' (" + arg + ")...");
        } else {
          Commands.sendMessage(player, ChatColor.GOLD + "Rebuilding the world '" + world + "' (" + arg + ")...");
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
          Commands.sendMessage(player, ChatColor.RED + "The world '" + world + "' is your default world. Due to a restriction with Bukkit, WorldRescue can not create/restore a backup.");
          Commands.sendMessage(player, ChatColor.RED + "To solve this problem, type '" + ChatColor.GREEN + "/wr duplicate " + world + ChatColor.RED + "'.");
          Commands.sendMessage(player, ChatColor.RED + "This will create the new world '" + world + "-new' which will be the same as the world '" + world + "' and WorldRescue will be able to create/restore backups from '"+world+"-new'.");
          Commands.sendMessage(player, ChatColor.RED + "You can also open your server.config and change the point 'level-name' to another world.");
        } else {
          load(Bukkit.getServer().getWorlds().get(0).getName());
          for (final Player pInWorld : Bukkit.getServer().getOnlinePlayers()) {
            if (pInWorld.getWorld().getName().equalsIgnoreCase(world)) {
              playerInWorld[i] = pInWorld;
              playerInWorldLoc[i] = pInWorld.getLocation();
              playerInWorldLGM[i] = pInWorld.getGameMode();
              if (!Commands.hasMultiverse()) {
                teleport(pInWorld, Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
              }
              ++i;
            }
          }
          unload(world, true);
          final boolean isSuccess;
          if (args[0].equalsIgnoreCase("save")) {
            isSuccess = Io.copy(Paths.get(world), Paths.get(backup));
          } else {
            isSuccess = Io.copy(Paths.get(backup), Paths.get(world));
          }
          Bukkit.getScheduler().runTask(plugin, () -> {
            load(world);
            boolean loaded = false;
            while (!loaded) {
              loaded = Bukkit.getServer().getWorlds().contains(Bukkit.getWorld(world));
              if (loaded) {
                Bukkit.getScheduler().runTaskLater(plugin, () ->
                  IntStream
                    .range(0, playerInWorld.length)
                    .filter(i12 -> playerInWorld[i12] != null)
                    .forEach(i12 -> teleport(playerInWorld[i12], playerInWorldLoc[i12])), 10L);
                Bukkit.getScheduler().runTaskLater(plugin, () ->
                  IntStream
                    .range(0, playerInWorld.length)
                    .filter(i1 -> playerInWorld[i1] != null)
                    .forEach(i1 -> playerInWorld[i1].setGameMode(playerInWorldLGM[i1])), 20L);
              }
            }
          });
          if (isSuccess && player != null) {
            if (args[0].equalsIgnoreCase("save")) {
              Bukkit.getServer().broadcastMessage(player.getDisplayName() + ChatColor.GREEN + " saved the world '" + world + "' (" + arg + ").");
            } else {
              Bukkit.getServer().broadcastMessage(player.getDisplayName() + ChatColor.GREEN + " rebuilt the world '" + world + "' (" + arg + ").");
            }
          } else if (player != null) {
            if (args[0].equalsIgnoreCase("save")) {
              Commands.sendMessage(player, ChatColor.DARK_RED + "The world '" + world + "' with index '" + arg + "' does not exist.");
            } else {
              Commands.sendMessage(player, ChatColor.DARK_RED + "A backup of the world '" + world + "' with index '" + arg + "' does not exist.");
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
    final String worldName = args[1];
    final Server server = Bukkit.getServer();
    final World world = server.createWorld(new WorldCreator(worldName));
    assert world != null;
    teleport(player, world.getSpawnLocation());
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
    if (hasMultiverse() && location.getWorld() != null) {
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
      Io.delete(Paths.get(world));
    }
  }

  private static boolean hasMultiverse() {
    return Bukkit.getServer().getPluginManager().isPluginEnabled("Multiverse-Core");
  }

  public static void sendMessage(final Player receiver, final String message) {
    if (receiver == null) {
      System.out.println("[WorldRescue] " + message);
      return;
    }
    receiver.sendMessage(message);
  }

}

