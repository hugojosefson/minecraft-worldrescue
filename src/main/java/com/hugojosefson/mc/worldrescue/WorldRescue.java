package com.hugojosefson.mc.worldrescue;

import com.helospark.lightdi.LightDiContext;
import com.hugojosefson.mc.worldrescue.commands.Commands;
import com.hugojosefson.mc.worldrescue.schedulable.Schedulable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import static com.helospark.lightdi.LightDi.initContextByPackage;

import com.hugojosefson.mc.worldrescue.fn.BukkitFunctions;

import static java.lang.String.join;
import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;

public class WorldRescue extends JavaPlugin {
  private final PluginDescriptionFile pd;
  private final FileConfiguration config;
  private final BukkitScheduler scheduler;
  private final Schedulable[] schedulables;
  private final Commands commands;

  public WorldRescue() {
    final String packageName = getClass().getPackage().getName();
    final LightDiContext diContext = initContextByPackage(packageName);
    diContext.registerSingleton(this);

    this.pd = diContext.getBean(PluginDescriptionFile.class);
    this.scheduler = diContext.getBean(BukkitScheduler.class);
    this.schedulables = diContext.getBean(Schedulable[].class);
    this.commands = diContext.getBean(Commands.class);
    this.config = diContext.getBean(FileConfiguration.class);
  }

  public void onEnable() {
    pd.getCommands().keySet()
      .stream()
      .map(BukkitFunctions::getPluginCommand)
      .forEach(pluginCommand -> pluginCommand.setExecutor(commands));
    // TODO: split up Commands into each PluginCommand (1, worldrescue), and each of those (1) into Command's
    // TODO: interface PluginCommand {CommandExecutor getExecutor(); TabCompleter getTabCompleter()}

    config.options().copyDefaults(true);
    saveConfig();

    stream(schedulables)
      .filter(Schedulable::shouldSchedule)
      .forEach(schedulable -> scheduler.scheduleSyncRepeatingTask(
        WorldRescue.this,
        schedulable,
        schedulable.getDelay(),
        schedulable.getInterval()
      ));

    System.out.println(format("[{0}] {1} (by {2}) enabled.", pd.getName(), pd.getVersion(), join(", ", pd.getAuthors())));
  }

  public void onDisable() {
    System.out.println(format("[{0}] {1} (by {2}) disabled.", pd.getName(), pd.getVersion(), join(", ", pd.getAuthors())));
  }
}
