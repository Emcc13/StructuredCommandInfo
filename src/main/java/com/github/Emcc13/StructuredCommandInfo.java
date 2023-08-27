package com.github.Emcc13;

import com.github.Emcc13.Commands.InfoCommand;
import com.github.Emcc13.Commands.ReloadConfig;
import com.github.Emcc13.Commands.SetLine;
import com.github.Emcc13.Config.CommandConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

        for (Map.Entry<String, InfoCommand> entry : this.commands.entrySet()) {
            Bukkit.getLogger().log(Level.INFO, "[SCI] Register command: " + entry.getKey());
            Bukkit.getServer().getCommandMap().register(entry.getValue().command_name,"scinfo", entry.getValue());
            entry.getValue().register(Bukkit.getServer().getCommandMap());
        }
    }

    public static StructuredCommandInfo get_instance() {
        return instance;
    }

    public void add_new_command(InfoCommand command) {
//        try {
//            final Field bukkit_commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
//            bukkit_commandMap.setAccessible(true);
//            CommandMap commandMap = (CommandMap) bukkit_commandMap.get(Bukkit.getServer());
//            final Field plugin_manager_commandMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
//            plugin_manager_commandMap.setAccessible(true);
//            CommandMap commandMap = (CommandMap) plugin_manager_commandMap.get(Bukkit.getPluginManager());
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }

        Bukkit.getLogger().log(Level.INFO, "[SCI] Register command: " + command.command_name);
        this.commands.put(command.command_name, command);
        Bukkit.getServer().getCommandMap().register(command.command_name, "scinfo", command);
        command.register(Bukkit.getServer().getCommandMap());
//        ((SimpleCommandMap)Bukkit.getServer().getCommandMap()).registerServerAliases();

        final Method syncCommands;
        try {
            syncCommands = Bukkit.getServer().getClass().getDeclaredMethod("syncCommands");
            syncCommands.setAccessible(true);
            syncCommands.invoke(Bukkit.getServer());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Bukkit.getLogger().log(Level.WARNING, "[SCI] Failed to sync commands for players!");
        }

//        https://github.com/PaperMC/Paper/blob/0c0a480d82132dca0e4bb7a2275c9363430e544b/patches/server/0066-Default-loading-permissions.yml-before-plugins.patch#L31
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
