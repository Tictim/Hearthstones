package tictim.hearthstones;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.data.TavernMemory;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.data.TavernRecord;
import tictim.hearthstones.logic.Tavern;
import tictim.hearthstones.utils.TavernType;

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
						).executes(ctx -> listTavernMemory(ctx.getSource(), GlobalTavernMemory.get()))
				).then(argument("player", EntityArgument.player())
						.executes(ctx -> listTavernMemory(ctx.getSource(), PlayerTavernMemory.get(EntityArgument.getPlayer(ctx, "player"))))
				)
		);
	}

	private static int addTavernMemory(CommandSourceStack sender, Player player, ServerLevel world, BlockPos pos){
		if(!world.isInWorldBounds(pos)) sender.sendFailure(new TranslatableComponent("command.tavern_memory.add.out_of_world", new TavernPos(world, pos)));
		else if(!world.isAreaLoaded(pos, 0)) sender.sendFailure(new TranslatableComponent("command.tavern_memory.add.unloaded", new TavernPos(world, pos)));
		else{
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof Tavern){
				PlayerTavernMemory m = PlayerTavernMemory.get(player);
				m.add((Tavern)te);
				m.sync();
				sender.sendSuccess(new TranslatableComponent("command.tavern_memory.add.success", new TavernPos(world, pos), player.getDisplayName()), true);
				return SINGLE_SUCCESS;
			}else sender.sendFailure(new TranslatableComponent("command.tavern_memory.add.no_tavern", new TavernPos(world, pos)));
		}
		return 0;
	}

	private static int removeTavernMemory(CommandSourceStack sender, Player player, ServerLevel dim, BlockPos pos){
		PlayerTavernMemory m = PlayerTavernMemory.get(player);
		if(m.delete(dim, pos)!=null){
			m.sync();
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
			if(tavern.tavernType()==TavernType.GLOBAL){
				GlobalTavernMemory.get().add(tavern);
				sender.sendSuccess(new TranslatableComponent("command.tavern_memory.add.success.global", new TavernPos(world, pos)), true);
				return SINGLE_SUCCESS;
			}else sender.sendSuccess(new TranslatableComponent("command.tavern_memory.add.not_global", new TavernPos(world, pos)), true);
		}else sender.sendFailure(new TranslatableComponent("command.tavern_memory.add.no_tavern", new TavernPos(world, pos)));
		return 0;
	}

	private static int removeGlobalTavernMemory(CommandSourceStack sender, ServerLevel world, BlockPos pos){
		if(GlobalTavernMemory.get().delete(world, pos)!=null){
			sender.sendSuccess(new TranslatableComponent("command.tavern_memory.remove.success.global", new TavernPos(world, pos)), true);
			return SINGLE_SUCCESS;
		}else{
			sender.sendFailure(new TranslatableComponent("command.tavern_memory.remove.no_memory", new TavernPos(world, pos)));
			return 0;
		}
	}

	private static int listTavernMemory(CommandSourceStack sender, TavernMemory tavernMemory){
		switch(tavernMemory.memories().size()){
			case 0 -> {
				sender.sendSuccess(new TranslatableComponent("command.tavern_memory.tavern.no_entry"), false);
				return 0;
			}
			case 1 -> sender.sendSuccess(new TranslatableComponent("command.tavern_memory.tavern.entry"), false);
			default -> sender.sendSuccess(new TranslatableComponent("command.tavern_memory.tavern.entries", tavernMemory.memories().size()), false);
		}
		for(TavernRecord memory : tavernMemory.memories()){
			Component access = new TextComponent(memory.getOwner().getAccessModifier().name());
			Component type = new TextComponent(memory.getTavernType().name.toUpperCase());

			sender.sendSuccess(new TranslatableComponent(memory.isMissing() ? "command.tavern_memory.tavern.missing" : "command.tavern_memory.tavern",
					memory.getName()!=null ? memory.getName() : new TranslatableComponent("info.hearthstones.tavern.noName"),
					access,
					type,
					memory.getOwner(),
					memory.getTavernPos()), false);
		}
		return tavernMemory.memories().size();
	}
}
