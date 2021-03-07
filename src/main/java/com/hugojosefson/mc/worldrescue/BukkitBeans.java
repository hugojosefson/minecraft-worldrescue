package com.hugojosefson.mc.worldrescue;

import com.helospark.lightdi.annotation.Bean;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public class BukkitBeans {

  @Bean
  public Server getServer() {
    return Bukkit.getServer();
  }
}
