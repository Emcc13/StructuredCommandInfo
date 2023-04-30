package com.github.Emcc13.Commands;

import com.github.Emcc13.StructuredCommandInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ReloadConfig implements TabExecutor {
    public final String command_name = "sci_reload";
    public final String permission = "sci.reload";
    private StructuredCommandInfo main;

    public ReloadConfig(StructuredCommandInfo main){
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !(sender.hasPermission(permission) || sender.isOp())) {
            return false;
        }
        StructuredCommandInfo.get_instance().reload();
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                return new ArrayList<String>() {{
                    add(command_name);
                }};
        }
        return null;
    }
}
