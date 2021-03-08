package com.hugojosefson.mc.worldrescue;

import com.hugojosefson.mc.worldrescue.commands.WorldRescueCommandExecutor;
import com.hugojosefson.mc.worldrescue.fn.BukkitFunctions;
import com.hugojosefson.mc.worldrescue.schedulable.Schedulable;
import com.hugojosefson.mc.worldrescue.schedulable.Schedulables;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import static java.lang.String.join;
import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;

public class WorldRescue extends JavaPlugin {
  private PluginDescriptionFile pd;
  private FileConfiguration config;
  private BukkitScheduler scheduler;
  private WorldRescueCommandExecutor executor;
  private Schedulable[] schedulables;

  public void onEnable() {
    initDependencies();
    initConfig();
    initPluginCommands();
    initSchedulables();
    System.out.println(format("[{0}] {1} (by {2}) enabled.", pd.getName(), pd.getVersion(), join(", ", pd.getAuthors())));
  }

  public void onDisable() {
    System.out.println(format("[{0}] {1} (by {2}) disabled.", pd.getName(), pd.getVersion(), join(", ", pd.getAuthors())));
  }

  private void initDependencies() {
    pd = getDescription();
    config = getConfig();
    scheduler = Bukkit.getScheduler();
    executor = new WorldRescueCommandExecutor(this);
    schedulables = Schedulables.getSchedulables(pd, config, getServer(), executor);
  }

  private void initConfig() {
    config.options().copyDefaults(true);
    saveConfig();
  }

  private void initPluginCommands() {
    pd.getCommands().keySet().stream()
      .map(BukkitFunctions::getPluginCommand)
      .forEach(pluginCommand -> pluginCommand.setExecutor(executor));
  }

  private void initSchedulables() {
    stream(schedulables)
      .filter(Schedulable::shouldSchedule)
      .forEach(schedulable -> scheduler.scheduleSyncRepeatingTask(
        this,
        schedulable,
        schedulable.getDelay(),
        schedulable.getInterval()
      ));
  }
}
