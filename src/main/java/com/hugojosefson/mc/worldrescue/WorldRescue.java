package com.hugojosefson.mc.worldrescue;

import com.helospark.lightdi.LightDiContext;
import com.helospark.lightdi.annotation.Autowired;
import com.hugojosefson.mc.worldrescue.scheduled.AutoSave;
import com.hugojosefson.mc.worldrescue.commands.Commands;
import com.hugojosefson.mc.worldrescue.commands.PluginCommandNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static com.helospark.lightdi.LightDi.initContextByPackage;
import static java.lang.String.join;

public class WorldRescue extends JavaPlugin {

  @Autowired
  public final Commands commands;

  public WorldRescue() {
    final LightDiContext diContext = initContextByPackage(this.getClass().getPackage().getName());
    diContext.registerSingleton(this);

    this.commands = diContext.getBean(Commands.class);
  }

  public void onEnable() {
    getPluginCommand("worldrescue").setExecutor(this.commands);
    getPluginCommand("wr").setExecutor(this.commands);

    final FileConfiguration config = this.getConfig();

    if (config.getBoolean("Autosave.Enabled")) {
      scheduleAutosave(config);
    }

    config.options().copyDefaults(true);
    this.saveConfig();

    final PluginDescriptionFile pluginYml = this.getDescription();
    System.out.println("[" + pluginYml.getName() + "] " + pluginYml.getVersion() + " (by " + join(", ", pluginYml.getAuthors()) + ") enabled.");
  }

  private void scheduleAutosave(final FileConfiguration config) {
    final long delay = 0L;
    final long period = 20L * 60 * config.getInt("Autosave.Frequency (in min)");
    Bukkit.getScheduler().scheduleSyncRepeatingTask(
      this,
      new AutoSave(commands, pluginDescription, server),
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
    final PluginDescriptionFile pluginYml = this.getDescription();
    System.out.println("[" + pluginYml.getName() + "] " + pluginYml.getVersion() + " (by " + join(", ", pluginYml.getAuthors()) + ") disabled.");
  }
}
