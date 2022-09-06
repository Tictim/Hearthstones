package tictim.hearthstones.server;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernMemory;

public class GlobalMemoryCommand extends CommandTreeBase{
	@Override public String getName(){
		return "global";
	}
	@Override public String getUsage(ICommandSender sender){
		return "command.tavern_memory.global.usage";
	}

	@Override public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
		if(args.length==0){
			TavernMemory memory = TavernMemories.global();

			switch(memory.taverns().size()){
				case 0:
					notifyCommandListener(sender, this, "command.tavern_memory.global.no_entry");
					break;
				case 1:
					notifyCommandListener(sender, this, "command.tavern_memory.global.entry");
					break;
				default:
					notifyCommandListener(sender, this, "command.tavern_memory.global.entries", memory.taverns().size());
			}
			ViewPlayerMemoryCommand.listTavernMemory(sender, this, memory);
		}else super.execute(server, sender, args);
	}
}
