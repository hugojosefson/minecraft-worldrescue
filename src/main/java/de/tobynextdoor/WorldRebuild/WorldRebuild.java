// 
// Decompiled by Procyon v0.5.36
// 

package de.tobynextdoor.WorldRebuild;

import org.bukkit.command.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldRebuild extends JavaPlugin
{
    public Plugin plugin;
    public WorldRebuildCommands commands;
    
    public WorldRebuild() {
        this.commands = new WorldRebuildCommands(this);
    }
    
    public void onEnable() {
        Bukkit.getPluginCommand("worldrebuild").setExecutor((CommandExecutor)this.commands);
        Bukkit.getPluginCommand("wr").setExecutor((CommandExecutor)this.commands);
        if (this.getConfig().getBoolean("Autosave.Enabled")) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new WorldRebuildAutosaveRunnable(), 0L, this.getConfig().getInt("Autosave.Frequency (in min)") * 60 * 20L);
        }
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        System.out.println("[" + this.getDescription().getName() + "] " + this.getDescription().getVersion() + " (by tobynextdoor) enabled.");
    }
    
    public void onDisable() {
        System.out.println("[" + this.getDescription().getName() + "] " + this.getDescription().getVersion() + " (by tobynextdoor) disabled.");
    }
}
