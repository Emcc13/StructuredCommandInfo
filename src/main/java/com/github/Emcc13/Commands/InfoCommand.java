package com.github.Emcc13.Commands;

import com.github.Emcc13.Config.CommandConfig;
import com.github.Emcc13.StructuredCommandInfo;
import com.github.Emcc13.Util.Tuple;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfoCommand extends BukkitCommand implements CommandExecutor {
    public String command_name = null;
    private ArrayList<String> info = null;

    public InfoCommand(String command, List<String> info) {
        super(command);
        this.setAliases(new ArrayList<String>() {{
            add(command);
        }});
        this.command_name = command;
        this.info = new ArrayList<>(info);
    }

    @Override
    public @Nullable String getPermission() {
        return this.command_name;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        for (String tc : this.info) {
            sender.sendRichMessage(CommandConfig.formatString(
                    tc,
                    new Tuple<String,String>("%PLAYER%", sender.getName())
            ));
        }
        return false;
    }

    public List<String> get_info(){
        return this.info;
    }

    public void set_info(List<String> text){
        this.info = new ArrayList<>(text);
    }

    public boolean set_line(int index, String line){
        Map<String, Object> cachedConfig = StructuredCommandInfo.get_instance().getCachedConfig();
        String confPrefix = (String) cachedConfig.get(CommandConfig.prefix.key());
        String new_line = line.replace("%PREFIX%", confPrefix);
//        todo: replace player name
        while (info.size()<index)
            info.add("");
        info.set(index-1, new_line);
        for (int idx=info.size()-1; idx>=0; idx--){
            if (info.get(idx).isEmpty()) {
                info.remove(idx);
                continue;
            }
            break;
        }
        return info.size() > 0;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        this.execute(commandSender, s, strings);
        return false;
    }


}
