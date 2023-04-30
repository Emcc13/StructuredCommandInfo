package com.github.Emcc13;

import com.github.Emcc13.Commands.InfoCommand;
import com.github.Emcc13.Commands.ReloadConfig;
import com.github.Emcc13.Commands.SetLine;
import com.github.Emcc13.Config.CommandConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;

public class StructuredCommandInfo extends JavaPlugin {
    private static StructuredCommandInfo instance;
    private Map<String, Object> cachedConfig;

    public Map<String, InfoCommand> commands;

    public StructuredCommandInfo() {
        instance = this;
    }

    public void onEnable() {
        this.cachedConfig = CommandConfig.getConfig(this);
        this.commands = CommandConfig.getCommands(this);
        enable_commands();
    }

    public void onDisable() {

    }

    public Map<String, Object> getCachedConfig() {
        return this.cachedConfig;
    }

    public void enable_commands() {
        getCommand("sci_setline").setExecutor(new SetLine(this));
        getCommand("sci_reload").setExecutor(new ReloadConfig(this));

        try {
            final Field bukkit_commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkit_commandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkit_commandMap.get(Bukkit.getServer());

            for (Map.Entry<String, InfoCommand> entry : this.commands.entrySet()) {
                Bukkit.getLogger().log(Level.INFO, "[SCI] Register command: " + entry.getKey());
                commandMap.register("scinfo", entry.getValue());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static StructuredCommandInfo get_instance() {
        return instance;
    }

    public void add_new_command(InfoCommand command) {
        try {
            final Field bukkit_commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkit_commandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkit_commandMap.get(Bukkit.getServer());
            Bukkit.getLogger().log(Level.INFO, "[SCI] Register command: " + command.command_name);
            commandMap.register("scinfo", command);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void reload(){
        reloadConfig();
        this.cachedConfig = CommandConfig.getConfig(this);
        Map<String, InfoCommand> new_commands = CommandConfig.getCommands(this);
        for (Map.Entry<String, InfoCommand> entry : new_commands.entrySet()){
            if (this.commands.containsKey(entry.getKey())){
                this.commands.get(entry.getKey()).set_info(entry.getValue().get_info());
            }else{
                this.add_new_command(entry.getValue());
            }
        }
    }
}
