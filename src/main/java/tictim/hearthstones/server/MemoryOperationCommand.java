package tictim.hearthstones.server;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernMemory;
import tictim.hearthstones.tavern.TavernPos;
import tictim.hearthstones.tavern.TavernType;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MemoryOperationCommand extends CommandBase{
	private final boolean add, global;

	public MemoryOperationCommand(boolean add, boolean global){
		this.add = add;
		this.global = global;
	}

	@Override public String getName(){
		return add ? "add" : "remove";
	}
	@Override public String getUsage(ICommandSender sender){
		return translationKey("usage");
	}
	@Override public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
		int arguments = global ? 2 : 3;
		int argi = 0;

		if(args.length<arguments) throw new WrongUsageException(getUsage(sender));

		TavernMemory memory;
		String username;

		if(global){
			username = null;
			memory = TavernMemories.global();
		}else{
			username = args[argi++];
			GameProfile profile = HearthstoneCmd.parseProfile(server, username);
			memory = TavernMemories.player(profile.getId());
		}

		int dim = parseInt(args[argi++]);
		if(!DimensionManager.isDimensionRegistered(dim))
			throw new CommandException("command.tavern_memory.add.no_dimension", dim);
		BlockPos pos = parseBlockPos(sender, args, argi, false);
		TavernPos tavernPos = new TavernPos(dim, pos);

		WorldServer world = server.getWorld(dim);
		if(add){
			if(!world.isBlockLoaded(pos))
				throw new CommandException("command.tavern_memory.add.unloaded", tavernPos);
			TileEntity te = world.getTileEntity(pos);
			if(!(te instanceof Tavern))
				throw new CommandException("command.tavern_memory.add.no_tavern", tavernPos);
			Tavern tavern = (Tavern)te;
			if(global&&tavern.type()!=TavernType.GLOBAL)
				throw new CommandException("command.tavern_memory.add.not_global", tavernPos);
			memory.addOrUpdate(tavern);
		}else{
			if(memory.delete(tavernPos)==null)
				throw new CommandException("command.tavern_memory.remove.no_memory", tavernPos);
		}
		notifyCommandListener(sender, this, translationKey("success"), tavernPos, username);
	}

	private String translationKey(String substring){
		return (add ? "command.tavern_memory.add." : "command.tavern_memory.remove.")+substring+(global ? ".global" : "");
	}

	@Override public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos){
		int usernameIdx = global ? 0 : 1;
		int dimIdx = global ? 1 : 2;
		int posIdxStart = global ? 2 : 3;
		if(args.length==usernameIdx){
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}else if(args.length==dimIdx){
			return getListOfStringsMatchingLastWord(args, Arrays.asList(DimensionManager.getIDs()));
		}else if(args.length<=posIdxStart+2){
			return getTabCompletionCoordinate(args, 0, targetPos);
		}else return Collections.emptyList();
	}
}
