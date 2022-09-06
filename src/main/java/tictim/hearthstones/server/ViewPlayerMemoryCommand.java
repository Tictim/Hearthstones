package tictim.hearthstones.server;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernMemory;
import tictim.hearthstones.tavern.TavernRecord;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static net.minecraft.util.text.TextFormatting.*;

public class ViewPlayerMemoryCommand extends CommandBase{
	@Override public String getName(){
		return "player";
	}
	@Override public String getUsage(ICommandSender sender){
		return "command.tavern_memory.player.usage";
	}
	@Override public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
		if(args.length<1) throw new WrongUsageException(getUsage(sender));
		GameProfile profile = HearthstoneCmd.parseProfile(server, args[0]);
		TavernMemory memory = TavernMemories.player(profile.getId());

		switch(memory.taverns().size()){
			case 0:
				notifyCommandListener(sender, this, "command.tavern_memory.player.no_entry", profile.getName(), profile.getId());
				break;
			case 1:
				notifyCommandListener(sender, this, "command.tavern_memory.player.entry", profile.getName(), profile.getId());
				break;
			default:
				notifyCommandListener(sender, this, "command.tavern_memory.player.entries", profile.getName(), profile.getId(), memory.taverns().size());
		}
		listTavernMemory(sender, this, memory);
	}

	@Override public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos){
		return args.length==1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.emptyList();
	}

	public static void listTavernMemory(ICommandSender sender, ICommand command, TavernMemory tavernMemory){
		for(TavernRecord tavern : tavernMemory.taverns().values()){
			ITextComponent text = new TextComponentString("[");
			text.appendText(tavern.type().commandAppearance);
			if(tavern.isMissing()){
				text.appendText(" ")
						.appendSibling(t("M", DARK_RED, new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Missing"))));
			}
			if(tavernMemory instanceof PlayerTavernMemory){
				PlayerTavernMemory p = (PlayerTavernMemory)tavernMemory;
				if(Objects.equals(p.getHomePos(), tavern.pos())){
					text.appendText(" ")
							.appendSibling(t("H", GOLD, new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Home"))));
				}
			}

			text.appendText("] ")
					.appendSibling(t(tavern.pos().toString(), DARK_GRAY))
					.appendText(" ");
			if(tavern.name()!=null) text.appendText(tavern.name());
			else text.appendSibling(new TextComponentTranslation("info.hearthstones.tavern.no_name"));
			if(tavern.owner().hasOwner()){
				text.appendSibling(t(" by ", GREEN).appendText(tavern.owner().toString()));
			}

			notifyCommandListener(sender, command, "%s", text);
		}
	}

	private static ITextComponent t(String s, TextFormatting f){
		return t(s, f, null);
	}
	private static ITextComponent t(String s, TextFormatting formatting, @Nullable HoverEvent hoverEvent){
		Style style = new Style();
		style.setColor(formatting);
		if(hoverEvent!=null) style.setHoverEvent(hoverEvent);
		return new TextComponentString(s).setStyle(style);
	}
}
