package com.github.Emcc13.Commands;

import com.github.Emcc13.Config.CommandConfig;
import com.github.Emcc13.StructuredCommandInfo;
import com.github.Emcc13.Util.Tuple;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InfoCommand extends BukkitCommand {
    public String command_name = null;
    private ArrayList<TextComponent> info = null;

    public InfoCommand(String command, List<TextComponent> info) {
        super(command);
        this.setAliases(new ArrayList<String>() {{
            add(command);
        }});
        this.command_name = command;
        this.info = new ArrayList<>(info);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        for (TextComponent tc : this.info) {
            sender.spigot().sendMessage(tc);
        }
        return false;
    }

    public List<String> get_text() {
        List<String> result = new LinkedList<String>();
        char colorChar = (char) StructuredCommandInfo.get_instance().getCachedConfig().get(CommandConfig.altColor.key());
        for (TextComponent tc : this.info) {
            result.add(tc.toPlainText().replace('§', colorChar));
        }
        return result;
    }

    public List<TextComponent> get_info(){
        return this.info;
    }

    public void set_info(List<TextComponent> text){
        this.info = new ArrayList<>(text);
    }

    public void set_line(int index, String line){
        Map<String, Object> cachedConfig = StructuredCommandInfo.get_instance().getCachedConfig();
        char colorChar = (char) cachedConfig.get(CommandConfig.altColor.key());
        String confPrefix = (String) cachedConfig.get(CommandConfig.prefix.key());
        TextComponent new_line = new TextComponent(ChatColor.translateAlternateColorCodes(colorChar,
                CommandConfig.formatString(line, new Tuple<>("%PREFIX%", confPrefix))));
        while (info.size()<index)
            info.add(new TextComponent());
        info.set(index-1, new_line);
        for (int idx=info.size()-1; idx>=0; idx--){
            if (info.get(idx).getText().isEmpty()) {
                info.remove(idx);
                continue;
            }
            break;
        }
    }
}
