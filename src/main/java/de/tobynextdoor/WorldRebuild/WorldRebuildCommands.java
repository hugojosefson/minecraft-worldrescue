//
// Decompiled by Procyon v0.5.36
//

package de.tobynextdoor.WorldRebuild;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WorldRebuildCommands implements CommandExecutor {
  public WorldRebuild plugin;
  WorldRebuildIO IO;
  static String tempWorld;
  boolean erfolg;
  static Player sPlayer;
  static String[] sArgs;
  static Player[] playerInWorldn;
  static Location[] playerInWorldLocn;
  static GameMode[] playerInWorldGMn;

  public WorldRebuildCommands(final WorldRebuild plugin) {
    this.IO = new WorldRebuildIO();
    this.plugin = plugin;
  }

  public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLable, final String[] args) {
    Player player;
    if (sender instanceof Player) {
      player = (Player) sender;
    } else {
      player = null;
    }
    if (args.length == 0) {
      return this.help(player);
    }
    if (args[0].equalsIgnoreCase("save")) {
      if (player == null || player.hasPermission("worldrebuild.save")) {
        return this.saveRebuild(player, args);
      }
      this.sendMessage(player, ChatColor.DARK_RED + "You don't have the permissions to perform this action!");
    } else if (args[0].equalsIgnoreCase("rebuild")) {
      if (player == null || player.hasPermission("worldrebuild.rebuild")) {
        return this.saveRebuild(player, args);
      }
      this.sendMessage(player, ChatColor.DARK_RED + "You don't have the permissions to perform this action!");
    } else if (args[0].equalsIgnoreCase("delete")) {
      if (player == null || player.hasPermission("worldrebuild.delete")) {
        return this.delete(player, args);
      }
      this.sendMessage(player, ChatColor.DARK_RED + "You don't have the permissions to perform this action!");
    } else if (args[0].equalsIgnoreCase("list")) {
      if (player == null || player.hasPermission("worldrebuild.list")) {
        return this.list(player, args);
      }
      this.sendMessage(player, ChatColor.DARK_RED + "You don't have the permissions to perform this action!");
    } else if (args[0].equalsIgnoreCase("duplicate")) {
      if (player == null || player.hasPermission("worldrebuild.duplicate")) {
        return this.duplicate(player, args);
      }
      this.sendMessage(player, ChatColor.DARK_RED + "You don't have the permissions to perform this action!");
    } else if (args[0].equalsIgnoreCase("help")) {
      if (player == null || player.hasPermission("worldrebuild.help")) {
        return this.help(player);
      }
      this.sendMessage(player, ChatColor.DARK_RED + "You don't have the permissions to perform this action!");
    } else if (args[0].equalsIgnoreCase("tp")) {
      if (player == null || player.hasPermission("worldrebuild.tp")) {
        return this.tp(player, args);
      }
      this.sendMessage(player, ChatColor.DARK_RED + "You don't have the permissions to perform this action!");
    } else {
      this.help(player);
    }
    return true;
  }

  public boolean delete(final Player player, final String[] args) {
    if (args.length == 3) {
      if (args[1].equals("me") && player != null) {
        args[1] = player.getWorld().getName();
      }
      final String world = args[1] + "_" + args[2] + "#backup";
      this.erfolg = this.IO.delete(world);
      if (this.erfolg) {
        Bukkit.getServer().broadcastMessage(player.getDisplayName() + ChatColor.GREEN + " deleted the backup '" + args[1] + "' (" + args[2] + ").");
      } else {
        this.sendMessage(player, ChatColor.DARK_RED + "The backup '" + args[1] + "' (" + args[2] + ") does not exist.");
      }
    } else {
      this.help(player);
    }
    return true;
  }

  public boolean duplicate(final Player player, final String[] args) {
    if (args.length > 1) {
      if (args[1].equals("me") && player != null) {
        args[1] = player.getWorld().getName();
      }
      Bukkit.getScheduler().runTask((Plugin) this.plugin, (Runnable) new Runnable() {
        @Override
        public void run() {
          final String world = args[1];
          final String nWorld = args[1] + "-new";
          WorldRebuildCommands.this.sendMessage(player, ChatColor.GOLD + "Creating a copy of the world '" + world + "' with the name '" + nWorld + "'. This may take a while.");
          WorldRebuildCommands.this.create(nWorld);
          WorldRebuildCommands.this.unload(nWorld, true);
          final WorldRebuildIO IO = new WorldRebuildIO();
          WorldRebuildCommands.this.erfolg = IO.copy(world, nWorld);
          if (WorldRebuildCommands.this.erfolg) {
            IO.delete(nWorld + "/uid.dat");
            WorldRebuildCommands.this.load(nWorld);
            if (WorldRebuildCommands.this.MVinstalled()) {
              WorldRebuildCommands.this.sendMessage(player, ChatColor.GOLD + "Done. Now just type '" + ChatColor.GREEN + "/mvtp " + nWorld + ChatColor.GOLD + "' and use this world to continue building.");
            } else {
              WorldRebuildCommands.this.sendMessage(player, ChatColor.GOLD + "Done. Now just type '" + ChatColor.GREEN + "/wr tp " + nWorld + ChatColor.GOLD + "' and use this world to continue building.");
            }
          } else {
            WorldRebuildCommands.this.sendMessage(player, ChatColor.DARK_RED + "The world '" + args[1] + " does not exist.");
            WorldRebuildCommands.this.delete(nWorld);
          }
        }
      });
    } else {
      this.help(player);
    }
    return true;
  }

  public boolean list(final Player player, final String[] args) {
    if ((args.length > 0 && player != null) || (player == null && args.length > 1)) {
      String listWorld;
      if (args.length == 1 && player != null) {
        listWorld = player.getWorld().getName();
      } else {
        if (args[1].equals("me") && player != null) {
          args[1] = player.getWorld().getName();
        }
        listWorld = args[1];
      }
      int reqworldex = 0;
      String[] welten = new String[100];
      final String[] reqworldbackex = new String[100];
      welten = this.IO.list(welten, listWorld);
      if (welten[0].equals("#")) {
        this.sendMessage(player, ChatColor.DARK_RED + "The world '" + listWorld + "' does not exist.");
        return true;
      }
      for (int i = 0; i < welten.length && welten[i] != null; ++i) {
        if (welten[i].split("'")[1].equals(listWorld)) {
          reqworldbackex[reqworldex] = welten[i];
          ++reqworldex;
        }
      }
      this.sendMessage(player, ChatColor.GOLD + "There are " + reqworldex + " backups:");
      for (int i = 0; i < reqworldex; ++i) {
        this.sendMessage(player, ChatColor.GREEN + reqworldbackex[i]);
      }
      if (reqworldex == 0) {
        this.sendMessage(player, "There are no backups of the world '" + listWorld + "'.");
      }
    } else {
      this.help(player);
    }
    return true;
  }

  public boolean help(final Player player) {
    this.sendMessage(player, " ");
    this.sendMessage(player, ChatColor.GOLD + "<needed> [optional]");
    this.sendMessage(player, ChatColor.GOLD + "'" + ChatColor.GREEN + "/wr <help>" + ChatColor.GOLD + "' --> Lists all commands of WorldRebuild.");
    this.sendMessage(player, ChatColor.GOLD + "'" + ChatColor.GREEN + "/wr <list> [world]" + ChatColor.GOLD + "' --> Lists all backups of a world.");
    this.sendMessage(player, ChatColor.GOLD + "'" + ChatColor.GREEN + "/wr <save/rebuild> [world] [index]" + ChatColor.GOLD + "' --> Saves/Rebuilds your chosen world.");
    this.sendMessage(player, ChatColor.GOLD + "'" + ChatColor.GREEN + "/wr <delete> <world> <index>" + ChatColor.GOLD + "' --> Delets the chosen backup.");
    this.sendMessage(player, ChatColor.GOLD + "'" + ChatColor.GREEN + "/wr <duplicate> <world>" + ChatColor.GOLD + "' --> Duplicate the chosen world.");
    this.sendMessage(player, ChatColor.GOLD + "'" + ChatColor.GREEN + "/wr <tp> <world>" + ChatColor.GOLD + "' --> Teleport to a world.");
    this.sendMessage(player, ChatColor.GOLD + "If you dont know the name of a world type 'me' instead.");
    return true;
  }

  public boolean saveRebuild(final Player player, final String[] args) {
    if ((args.length > 0 && player != null) || (player == null && args.length > 1)) {
      WorldRebuildCommands.sPlayer = player;
      WorldRebuildCommands.sArgs = args;
      Bukkit.getScheduler().runTask((Plugin) this.plugin, (Runnable) new Runnable() {
        String[] args = WorldRebuildCommands.sArgs;
        Player player = WorldRebuildCommands.sPlayer;

        @Override
        public void run() {
          String world = "";
          String arg = "";
          String backup = "";
          if (this.args.length == 1) {
            arg = "default";
            world = this.player.getWorld().getName();
            backup = this.player.getWorld().getName() + "_default" + "#backup";
          } else if (this.args.length == 2) {
            if (this.args[1].equals("me") && this.player != null) {
              this.args[1] = this.player.getWorld().getName();
            }
            arg = "default";
            world = this.args[1];
            backup = this.args[1] + "_default" + "#backup";
          } else {
            if (this.args[1].equals("me") && this.player != null) {
              this.args[1] = this.player.getWorld().getName();
            }
            arg = this.args[2];
            world = this.args[1];
            backup = this.args[1] + "_" + arg + "#backup";
          }
          if (this.args[0].equalsIgnoreCase("save")) {
            WorldRebuildCommands.this.sendMessage(this.player, ChatColor.GOLD + "Saving the world '" + world + "' (" + arg + ")...");
          } else {
            WorldRebuildCommands.this.sendMessage(this.player, ChatColor.GOLD + "Rebuilding the world '" + world + "' (" + arg + ")...");
          }
          final Player[] playerInWorld = new Player[Bukkit.getOnlinePlayers().length];
          final Location[] playerInWorldLoc = new Location[Bukkit.getOnlinePlayers().length];
          final GameMode[] playerInWorldLGM = new GameMode[Bukkit.getOnlinePlayers().length];
          int i = 0;
          boolean isDefault = false;
          if (Bukkit.getServer().getWorld(world) == Bukkit.getServer().getWorlds().get(0)) {
            isDefault = true;
          }
          if (isDefault) {
            WorldRebuildCommands.this.sendMessage(this.player, ChatColor.RED + "The world '" + world + "' is your default world and due to a Bukkit restrictment WorldRebuild can not create/restore a backup.");
            WorldRebuildCommands.this.sendMessage(this.player, ChatColor.RED + "To solve this problem, type '" + ChatColor.GREEN + "/wr duplicate <world name>" + ChatColor.RED + "'.");
            WorldRebuildCommands.this.sendMessage(this.player, ChatColor.RED + "This will create the new world '" + world + "-new' which will be the same as the world '" + world + "' and WorldRebuild will be abled to create/restore backups from this world.");
            WorldRebuildCommands.this.sendMessage(this.player, ChatColor.RED + "You can also open your server.config and change the point 'level-name' to another world.");
          } else {
            WorldRebuildCommands.this.load(Bukkit.getServer().getWorlds().get(0).getName());
            for (final Player pInWorld : Bukkit.getServer().getOnlinePlayers()) {
              if (pInWorld.getWorld().getName().equalsIgnoreCase(world)) {
                playerInWorld[i] = pInWorld;
                playerInWorldLoc[i] = pInWorld.getLocation();
                playerInWorldLGM[i] = pInWorld.getGameMode();
                if (!WorldRebuildCommands.this.MVinstalled()) {
                  WorldRebuildCommands.this.teleport(pInWorld, Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
                }
                ++i;
              }
            }
            WorldRebuildCommands.this.unload(world, true);
            if (this.args[0].equalsIgnoreCase("save")) {
              WorldRebuildCommands.this.erfolg = WorldRebuildCommands.this.IO.copy(world, backup);
            } else {
              WorldRebuildCommands.this.erfolg = WorldRebuildCommands.this.IO.copy(backup, world);
            }
            WorldRebuildCommands.tempWorld = world;
            WorldRebuildCommands.playerInWorldLocn = playerInWorldLoc;
            WorldRebuildCommands.playerInWorldn = playerInWorld;
            WorldRebuildCommands.playerInWorldGMn = playerInWorldLGM;
            Bukkit.getScheduler().runTask((Plugin) WorldRebuildCommands.this.plugin, (Runnable) new Runnable() {
              String world = WorldRebuildCommands.tempWorld;
              Player[] playerInWorld = WorldRebuildCommands.playerInWorldn;
              Location[] playerInWorldLoc = WorldRebuildCommands.playerInWorldLocn;
              GameMode[] playerInWorldGM = WorldRebuildCommands.playerInWorldGMn;

              @Override
              public void run() {
                WorldRebuildCommands.this.load(this.world);
                Boolean loaded = false;
                while (!loaded) {
                  loaded = Bukkit.getServer().getWorlds().contains(Bukkit.getWorld(this.world));
                  if (loaded) {
                    WorldRebuildCommands.playerInWorldLocn = this.playerInWorldLoc;
                    WorldRebuildCommands.playerInWorldn = this.playerInWorld;
                    WorldRebuildCommands.playerInWorldGMn = this.playerInWorldGM;
                    Bukkit.getScheduler().runTaskLater((Plugin) WorldRebuildCommands.this.plugin, (Runnable) new Runnable() {
                      Player[] playerInWorld = WorldRebuildCommands.playerInWorldn;
                      Location[] playerInWorldLoc = WorldRebuildCommands.playerInWorldLocn;

                      @Override
                      public void run() {
                        for (int i = 0; i < this.playerInWorld.length; ++i) {
                          if (this.playerInWorld[i] != null) {
                            WorldRebuildCommands.this.teleport(this.playerInWorld[i], this.playerInWorldLoc[i]);
                          }
                        }
                      }
                    }, 10L);
                    Bukkit.getScheduler().runTaskLater((Plugin) WorldRebuildCommands.this.plugin, (Runnable) new Runnable() {
                      Player[] playerInWorld = WorldRebuildCommands.playerInWorldn;
                      GameMode[] playerInWorldGM = WorldRebuildCommands.playerInWorldGMn;

                      @Override
                      public void run() {
                        for (int i = 0; i < this.playerInWorld.length; ++i) {
                          if (this.playerInWorld[i] != null) {
                            this.playerInWorld[i].setGameMode(this.playerInWorldGM[i]);
                          }
                        }
                      }
                    }, 20L);
                  }
                }
              }
            });
            if (WorldRebuildCommands.this.erfolg && this.player != null) {
              if (this.args[0].equalsIgnoreCase("save")) {
                Bukkit.getServer().broadcastMessage(this.player.getDisplayName() + ChatColor.GREEN + " saved the world '" + world + "' (" + arg + ").");
              } else {
                Bukkit.getServer().broadcastMessage(this.player.getDisplayName() + ChatColor.GREEN + " rebuilded the world '" + world + "' (" + arg + ").");
              }
            } else if (this.player != null) {
              if (this.args[0].equalsIgnoreCase("save")) {
                WorldRebuildCommands.this.sendMessage(this.player, ChatColor.DARK_RED + "The world '" + world + "' with the index '" + arg + "' does not exist.");
              } else {
                WorldRebuildCommands.this.sendMessage(this.player, ChatColor.DARK_RED + "The backup of the world '" + world + "' with the index '" + arg + "' does not exist.");
              }
            }
          }
        }
      });
    } else {
      this.help(player);
    }
    return true;
  }

  public boolean tp(final Player player, final String[] args) {
    Bukkit.getServer().createWorld(new WorldCreator(args[1]));
    this.teleport(player, Bukkit.getServer().getWorld(args[1]).getSpawnLocation());
    return true;
  }

  void create(final String world) {
    if (this.MVinstalled()) {
      Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getConsoleSender(), "mv create " + world + " normal");
    } else {
      Bukkit.getServer().createWorld(new WorldCreator(world));
    }
  }

  void load(final String world) {
    if (this.MVinstalled()) {
      Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getConsoleSender(), "mv load " + world);
    } else {
      Bukkit.getServer().createWorld(new WorldCreator(world));
    }
  }

  void unload(final String world, final boolean save) {
    if (this.MVinstalled()) {
      Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getConsoleSender(), "mv unload " + world);
    } else {
      Bukkit.getServer().unloadWorld(world, save);
    }
  }

  void teleport(final Player p, final Location l) {
    if (this.MVinstalled()) {
      Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getConsoleSender(), "mvtp " + p.getName() + " e:" + l.getWorld().getName() + ":" + l.getX() + "," + l.getY() + "," + l.getZ());
    } else {
      p.teleport(l);
    }
  }

  void delete(final String world) {
    if (this.MVinstalled()) {
      Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getConsoleSender(), "mv load " + world);
      Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getConsoleSender(), "mv delete " + world);
      Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getConsoleSender(), "mv confirm");
    } else {
      this.IO.delete(world);
    }
  }

  boolean MVinstalled() {
    return Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core") != null;
  }

  public void sendMessage(final Player receiver, final String message) {
    if (receiver != null) {
      receiver.sendMessage(message);
    } else {
      System.out.println("[WorldRebuild] " + message);
    }
  }

  static {
    WorldRebuildCommands.tempWorld = "";
  }
}
