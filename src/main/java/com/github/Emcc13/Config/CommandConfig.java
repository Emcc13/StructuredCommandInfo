package com.github.Emcc13.Config;

import com.github.Emcc13.Commands.InfoCommand;
import com.github.Emcc13.StructuredCommandInfo;
import com.github.Emcc13.Util.Tuple;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.logging.Level;

public enum CommandConfig implements ConfigInterface {
    altColor("&"),
    prefix("[PXFL-Info]");

    public enum Commands implements ConfigInterface {
        commands_scinfo(new ArrayList<String>() {{
            add("%PREFIX% This is the structured command plugin providing commands which prints information to users.");
            add("%PREFIX% If you want to add new commands you can do that via editing the config.");
        }});
        private final Object value;
        private final String key_;

        Commands(Object value) {
            this.value = value;
            this.key_ = this.name().replace('_', '.');
        }

        Commands(String key, Object value) {
            this.value = value;
            this.key_ = key;
        }

        public Object value() {
            return this.value;
        }

        public String key() {
            return this.key_;
        }
    }

    private final Object value;
    private final String key_;

    CommandConfig(Object value) {
        this.value = value;
        this.key_ = this.name().replace('_', '.');
    }

    CommandConfig(String key, Object value) {
        this.value = value;
        this.key_ = key;
    }

    public static Map<String, Object> getConfig(StructuredCommandInfo main) {
        Map<String, Object> cachedConfig = new HashMap<>();
        main.reloadConfig();
        Configuration config = main.getConfig();
        String key;
        Object value;

//        global configs: altColorChar, prefix, etc.
        for (CommandConfig entry : CommandConfig.values()) {
            key = entry.key();
            value = config.get(key);
            if (value == null) {
                value = entry.value();
            }
            cachedConfig.put(key, value);
        }
        cachedConfig.put(altColor.key(), ((String) cachedConfig.get(altColor.key())).charAt(0));

        config.addDefaults(cachedConfig);
        config.options().copyDefaults(true);
        main.saveConfig();
        return cachedConfig;
    }

    public static Map<String, InfoCommand> getCommands(StructuredCommandInfo main) {
        char altColor_char = (char) main.getCachedConfig().get(altColor.key());
        String confPrefix = (String) main.getCachedConfig().get(prefix.key());
        Map<String, InfoCommand> commands = new HashMap<>();
        main.reloadConfig();
        Configuration config = main.getConfig();
        String key;
        Object value;
        LinkedList<TextComponent> info_text = new LinkedList<>();

//        get predefined commands
        for (Commands entry : Commands.values()) {
            key = entry.key();
            String key_it = key.split("\\.")[1];
            value = config.get(key);
            if (value == null) {
                value = entry.value();
                config.addDefault(key, value);
            }
            try {
                info_text.clear();
                for (String line : ((List<String>) value)) {
                    info_text.add(new TextComponent(ChatColor.translateAlternateColorCodes(altColor_char,
                            formatString(line, new Tuple<>("%PREFIX%", confPrefix))
                    )));
                }
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "[SCI] Failed to load config for key: " + key);
                continue;
            }
            commands.put(key_it,
                    new InfoCommand(key_it, info_text));
        }
        ConfigurationSection commandSection = config.getConfigurationSection("commands");
        try {
            for (String key_it : commandSection.getKeys(false)) {
                key = key_it;
                if (commands.containsKey(key))
                    continue;
                try {
                    info_text.clear();
                    for (String line : commandSection.getStringList(key)) {
                        info_text.add(new TextComponent(ChatColor.translateAlternateColorCodes(altColor_char,
                                formatString(line, new Tuple<>("%PREFIX%", confPrefix)))));
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().log(Level.WARNING, "[SCI] Failed to load config for key: " + key);
                    continue;
                }
                commands.put(key,
                        new InfoCommand(key, info_text));
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "[SCI] Failed to load commands! Check the config!");
        }
        config.options().copyDefaults(true);
        main.saveConfig();
        return commands;
    }

    public Object value() {
        return this.value;
    }

    public String key() {
        return this.key_;
    }

    public static boolean updateConfig(String command_name, int index, String line) {
        StructuredCommandInfo main = StructuredCommandInfo.get_instance();
        Configuration config = main.getConfig();
        try {
            ConfigurationSection commandSection = config.getConfigurationSection("commands");
            ArrayList<String> command_config = new ArrayList<>(commandSection.getStringList(command_name));
            while (command_config.size()<index)
                command_config.add("");
            command_config.set(index-1, line);
            for (int idx=command_config.size()-1; idx>=0; idx--){
                if (command_config.get(idx).isEmpty()){
                    command_config.remove(idx);
                    continue;
                }
                break;
            }
            commandSection.set(command_name, command_config);
        }catch (NullPointerException e){
            Bukkit.getLogger().log(Level.WARNING, "[SCI] No 'commands' section in the config. " +
                    "Add that section and try again.");
            return false;
        }
        config.options().copyDefaults(true);
        main.saveConfig();
        return true;
    }

    public static String formatString(String template, Tuple<String, String>... replacements) {
        String result = template;
        for (Tuple<String, String> replacement : replacements) {
            result = result.replace(replacement.t1, replacement.t2);
        }
        return result;
    }
}
