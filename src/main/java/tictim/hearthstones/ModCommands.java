package tictim.hearthstones;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernMemory;
import tictim.hearthstones.tavern.TavernPos;
import tictim.hearthstones.tavern.TavernRecord;
import tictim.hearthstones.tavern.TavernType;

import java.util.Collection;
import java.util.Objects;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class ModCommands{
	private ModCommands(){}

	public static void init(CommandDispatcher<CommandSourceStack> dispatcher){
		// TODO Suggest position(maybe need to create TavernPosArgument?)
		dispatcher.register(literal("tavernMemory")
				.requires(cs -> cs.hasPermission(4))
				.then(literal("add")
						.then(argument("player", EntityArgument.player())
								.then(argument("dimension", DimensionArgument.dimension())
										.then(argument("position", BlockPosArgument.blockPos())
												.executes(ctx -> addTavernMemory(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), DimensionArgument.getDimension(ctx, "dimension"), BlockPosArgument.getSpawnablePos(ctx, "position")))
										)
								)
						)
				).then(literal("remove")
						.then(argument("player", EntityArgument.player())
								.then(argument("dimension", DimensionArgument.dimension())
										.then(argument("position", BlockPosArgument.blockPos())
												.executes(ctx -> removeTavernMemory(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), DimensionArgument.getDimension(ctx, "dimension"), BlockPosArgument.getSpawnablePos(ctx, "position")))
										)
								)
						)
				).then(literal("global")
						.then(literal("add")
								.then(argument("dimension", DimensionArgument.dimension())
										.then(argument("position", BlockPosArgument.blockPos())
												.executes(ctx -> addGlobalTavernMemory(ctx.getSource(), DimensionArgument.getDimension(ctx, "dimension"), BlockPosArgument.getSpawnablePos(ctx, "position")))
										)
								)
						).then(literal("remove")
								.then(argument("dimension", DimensionArgument.dimension())
										.then(argument("position", BlockPosArgument.blockPos())
												.executes(ctx -> removeGlobalTavernMemory(ctx.getSource(), DimensionArgument.getDimension(ctx, "dimension"), BlockPosArgument.getSpawnablePos(ctx, "position")))
										)
								)
						).executes(ctx -> listGlobalTavernMemory(ctx.getSource()))
				).then(argument("player", GameProfileArgument.gameProfile())
						.executes(ctx -> listPlayerTavernMemory(ctx.getSource(), GameProfileArgument.getGameProfiles(ctx, "player")))
				)
		);
	}

	private static int addTavernMemory(CommandSourceStack sender, Player player, ServerLevel level, BlockPos pos){
		if(!level.isInWorldBounds(pos)) sender.sendFailure(new TranslatableComponent("command.tavern_memory.add.out_of_world", new TavernPos(level, pos)));
		else if(!level.isAreaLoaded(pos, 0)) sender.sendFailure(new TranslatableComponent("command.tavern_memory.add.unloaded", new TavernPos(level, pos)));
		else if(level.getBlockEntity(pos) instanceof Tavern tavern){
			TavernMemories.player(player).addOrUpdate(tavern);
			sender.sendSuccess(new TranslatableComponent("command.tavern_memory.add.success", new TavernPos(level, pos), player.getDisplayName()), true);
			return SINGLE_SUCCESS;
		}else sender.sendFailure(new TranslatableComponent("command.tavern_memory.add.no_tavern", new TavernPos(level, pos)));
		return 0;
	}

	private static int removeTavernMemory(CommandSourceStack sender, Player player, ServerLevel dim, BlockPos pos){
		PlayerTavernMemory m = TavernMemories.player(player);
		if(m.delete(dim, pos)!=null){
			sender.sendSuccess(new TranslatableComponent("command.tavern_memory.remove.success", new TavernPos(dim, pos), player.getDisplayName()), true);
			return SINGLE_SUCCESS;
		}else{
			sender.sendFailure(new TranslatableComponent("command.tavern_memory.remove.no_memory", new TavernPos(dim, pos)));
			return 0;
		}
	}

	private static int addGlobalTavernMemory(CommandSourceStack sender, ServerLevel world, BlockPos pos){
		if(!world.isInWorldBounds(pos)) sender.sendFailure(new TranslatableComponent("command.tavern_memory.add.out_of_world", new TavernPos(world, pos)));
		else if(!world.isAreaLoaded(pos, 0)) sender.sendFailure(new TranslatableComponent("command.tavern_memory.add.unloaded", new TavernPos(world, pos)));
		else if(world.getBlockEntity(pos) instanceof Tavern tavern){
			if(tavern.type()==TavernType.GLOBAL){
				TavernMemories.global().addOrUpdate(tavern);
				sender.sendSuccess(new TranslatableComponent("command.tavern_memory.add.success.global", new TavernPos(world, pos)), true);
				return SINGLE_SUCCESS;
			}else sender.sendSuccess(new TranslatableComponent("command.tavern_memory.add.not_global", new TavernPos(world, pos)), true);
		}else sender.sendFailure(new TranslatableComponent("command.tavern_memory.add.no_tavern", new TavernPos(world, pos)));
		return 0;
	}

	private static int removeGlobalTavernMemory(CommandSourceStack sender, ServerLevel world, BlockPos pos){
		if(TavernMemories.global().delete(world, pos)!=null){
			sender.sendSuccess(new TranslatableComponent("command.tavern_memory.remove.success.global", new TavernPos(world, pos)), true);
			return SINGLE_SUCCESS;
		}else{
			sender.sendFailure(new TranslatableComponent("command.tavern_memory.remove.no_memory", new TavernPos(world, pos)));
			return 0;
		}
	}

	private static int listGlobalTavernMemory(CommandSourceStack sender){
		TavernMemory global = TavernMemories.global();
		switch(global.taverns().size()){
			case 0 -> sender.sendSuccess(new TranslatableComponent("command.tavern_memory.global.no_entry"), false);
			case 1 -> sender.sendSuccess(new TranslatableComponent("command.tavern_memory.global.entry"), false);
			default -> sender.sendSuccess(new TranslatableComponent("command.tavern_memory.global.entries", global.taverns().size()), false);
		}
		listTavernMemory(sender, global);
		return SINGLE_SUCCESS;
	}
	private static int listPlayerTavernMemory(CommandSourceStack sender, Collection<GameProfile> profiles){
		TavernMemories memories = TavernMemories.expect();
		for(GameProfile profile : profiles){
			PlayerTavernMemory player = memories.getPlayer(profile.getId());
			switch(player.taverns().size()){
				case 0 -> sender.sendSuccess(new TranslatableComponent("command.tavern_memory.player.no_entry", profile.getName(), profile.getId()), false);
				case 1 -> sender.sendSuccess(new TranslatableComponent("command.tavern_memory.player.entry", profile.getName(), profile.getId()), false);
				default -> sender.sendSuccess(new TranslatableComponent("command.tavern_memory.player.entries", profile.getName(), profile.getId(), player.taverns().size()), false);
			}
			listTavernMemory(sender, player);
		}
		return profiles.size();
	}
	private static void listTavernMemory(CommandSourceStack sender, TavernMemory tavernMemory){
		for(TavernRecord tavern : tavernMemory.taverns().values()){
			TextComponent text = new TextComponent("[");
			text.append(tavern.type().commandAppearance);
			if(tavern.isMissing()){
				text.append(" ").append(new TextComponent("M").withStyle(Style.EMPTY
						.withColor(ChatFormatting.DARK_RED)
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Missing")))));
			}
			if(tavernMemory instanceof PlayerTavernMemory p&&Objects.equals(p.getHomePos(), tavern.pos())){
				text.append(" ").append(new TextComponent("H").withStyle(Style.EMPTY
						.withColor(ChatFormatting.GOLD)
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Home")))));
			}

			text.append("] ")
					.append(new TextComponent(tavern.pos().toString()).withStyle(ChatFormatting.DARK_GRAY))
					.append(" ");
			if(tavern.name()!=null) text.append(tavern.name());
			else text.append(new TranslatableComponent("info.hearthstones.tavern.no_name"));
			if(tavern.owner().hasOwner()){
				text.append(new TextComponent(" by ").append(tavern.owner().toString()).withStyle(ChatFormatting.GREEN));
			}

			sender.sendSuccess(text, false);
		}
	}
}
