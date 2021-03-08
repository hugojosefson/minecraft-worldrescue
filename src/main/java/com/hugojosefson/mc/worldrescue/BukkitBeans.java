package com.hugojosefson.mc.worldrescue;

import com.hugojosefson.mc.worldrescue.fn.BukkitFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BukkitBeans {
  @Bean
  public Server server() {
    return Bukkit.getServer();
  }

  @Bean
  public BukkitScheduler scheduler() {
    return Bukkit.getScheduler();
  }

  @Bean
  @Autowired
  public FileConfiguration config(@Autowired final WorldRescue plugin) {
    return plugin.getConfig();
  }

  @Bean
  @Autowired
  public PluginDescriptionFile pluginDescriptionFile(@Autowired final WorldRescue plugin) {
    return plugin.getDescription();
  }

  @Bean
  @Autowired
  public PluginCommand[] pluginCommands(@Autowired final PluginDescriptionFile pd) {
    return pd.getCommands().keySet()
      .stream()
      .map(BukkitFunctions::getPluginCommand).toArray(PluginCommand[]::new);
  }
}
