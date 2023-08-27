package com.github.Emcc13.Commands;

import com.github.Emcc13.Config.CommandConfig;
import com.github.Emcc13.StructuredCommandInfo;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SetLine implements TabExecutor{
    public final String command_name = "sci_setline";
    public final String permission = "sci.set_line";
    private StructuredCommandInfo main;

    public SetLine(StructuredCommandInfo main){
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !(sender.hasPermission(permission) || sender.isOp())) {
            return false;
        }
        if (args.length < 2) {
            sender.sendMessage("Not enough arguments!");
            return false;
        }
        String command_name = args[0];
        int line_index=0;
        try {
            line_index = Integer.parseInt(args[1]);
        } catch (NumberFormatException e){
            sender.sendMessage("Wrong line number!");
            return false;
        }
        String line_text = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        InfoCommand commandHandler;
        if (!main.commands.containsKey(command_name))
            commandHandler = new InfoCommand(command_name, new LinkedList<String>());
        else
            commandHandler = main.commands.get(command_name);
        if (!CommandConfig.updateConfig(command_name, line_index, line_text)) {
            sender.sendMessage("Failed to update config! Check your config and try again!");
            return false;
        }
        commandHandler.set_line(line_index, line_text);
        if (main.commands.containsKey(command_name))
            return false;
        main.add_new_command(commandHandler);
        ((Player) sender).updateCommands();
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length){
            case 0:
                return new ArrayList<String>(){{
                    add(command_name);
                }};
            case 1:
                return new ArrayList<>(this.main.commands.keySet());
        }
        return null;
    }
}
