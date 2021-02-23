//
// Decompiled by Procyon v0.5.36
//

package de.tobynextdoor.worldrebuild;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class WorldRebuild extends JavaPlugin {
  public final WorldRebuildCommands commands;

  public WorldRebuild() {
    this.commands = new WorldRebuildCommands(this);
  }

  public void onEnable() {
    getPluginCommand("worldrebuild").setExecutor(this.commands);
    getPluginCommand("wr").setExecutor(this.commands);

    final FileConfiguration config = this.getConfig();

    if (config.getBoolean("Autosave.Enabled")) {
      scheduleAutosave(config);
    }

    config.options().copyDefaults(true);
    this.saveConfig();

    System.out.println("[" + this.getDescription().getName() + "] " + this.getDescription().getVersion() + " (by tobynextdoor) enabled.");
  }

  private void scheduleAutosave(final FileConfiguration config) {
    final long delay = 0L;
    final long period = 20L * 60 * config.getInt("Autosave.Frequency (in min)");
    Bukkit.getScheduler().scheduleSyncRepeatingTask(
      this,
      new AutosaveRunnable(),
      delay,
      period
    );
  }

  @NotNull
  private static PluginCommand getPluginCommand(final String command) throws PluginCommandNotFoundException {
    final PluginCommand pluginCommand = Bukkit.getPluginCommand(command);
    if (pluginCommand == null) throw new PluginCommandNotFoundException(command);
    return pluginCommand;
  }

  public void onDisable() {
    System.out.println("[" + this.getDescription().getName() + "] " + this.getDescription().getVersion() + " (by tobynextdoor) disabled.");
  }
}
