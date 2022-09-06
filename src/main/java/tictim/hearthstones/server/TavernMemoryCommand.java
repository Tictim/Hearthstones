package tictim.hearthstones.server;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

public class TavernMemoryCommand extends CommandTreeBase{
	public TavernMemoryCommand(){
		addSubcommand(new MemoryOperationCommand(true, false));
		addSubcommand(new MemoryOperationCommand(false, false));
		addSubcommand(new GlobalMemoryCommand());
		addSubcommand(new ViewPlayerMemoryCommand());
	}

	@Override public String getName(){
		return "tavernMemory";
	}
	@Override public String getUsage(ICommandSender sender){
		return "command.tavern_memory.usage";
	}
}
