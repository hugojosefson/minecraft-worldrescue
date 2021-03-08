package com.hugojosefson.mc.worldrescue;

import com.hugojosefson.mc.worldrescue.commands.WorldRescueCommandExecutor;
import com.hugojosefson.mc.worldrescue.schedulable.Schedulable;
import java.util.stream.Stream;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static java.lang.String.join;
import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;

public class WorldRescue extends JavaPlugin {
  @Autowired
  private  PluginDescriptionFile pd;

  @Autowired
  private  FileConfiguration config;

  @Autowired
  private  BukkitScheduler scheduler;

  @Autowired
  private  Schedulable schedulable;
//
//  @Autowired
//  private  List<Schedulable> schedulables;

  @Autowired
  private  WorldRescueCommandExecutor executor;

  @Autowired
  private  PluginCommand[] pluginCommands;

  public void onEnable() {
    final AnnotationConfigApplicationContext diContext = new AnnotationConfigApplicationContext();
    diContext.registerBean(WorldRescue.class, bd -> {
      bd.setLazyInit(true);
      ((AbstractBeanDefinition)bd).setInstanceSupplier(() -> WorldRescue.this);
    });
    diContext.register(BukkitBeans.class);
    diContext.scan(WorldRescue.class.getPackage().getName() + "commands");
    diContext.scan(WorldRescue.class.getPackage().getName() + "fn");
    diContext.scan(WorldRescue.class.getPackage().getName() + "io");
    diContext.scan(WorldRescue.class.getPackage().getName() + "schedulable");
    diContext.refresh();

    final AutowireCapableBeanFactory autowireCapableBeanFactory = diContext.getAutowireCapableBeanFactory();

    autowireCapableBeanFactory.autowireBean(this);


    // TODO: interface PluginCommand {CommandExecutor getExecutor(); TabCompleter getTabCompleter()} ?
    initPluginCommands(pluginCommands);
    initConfig();
    initSchedulables();

    System.out.println(format("[{0}] {1} (by {2}) enabled.", pd.getName(), pd.getVersion(), join(", ", pd.getAuthors())));
  }

  public void onDisable() {
    System.out.println(format("[{0}] {1} (by {2}) disabled.", pd.getName(), pd.getVersion(), join(", ", pd.getAuthors())));
  }

  private void initPluginCommands(final PluginCommand[] pluginCommands) {
    stream(pluginCommands)
      .forEach(pluginCommand -> pluginCommand.setExecutor(executor));
  }

  private void initConfig() {
    config.options().copyDefaults(true);
    saveConfig();
  }

  private void initSchedulables() {
//    schedulables.stream()
    Stream.of(schedulable)
      .filter(Schedulable::shouldSchedule)
      .forEach(schedulable -> scheduler.scheduleSyncRepeatingTask(
        this,
        schedulable,
        schedulable.getDelay(),
        schedulable.getInterval()
      ));
  }
}
