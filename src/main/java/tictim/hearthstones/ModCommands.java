package tictim.hearthstones;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.data.TavernMemory;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.data.TavernRecord;
import tictim.hearthstones.logic.Tavern;
import tictim.hearthstones.utils.TavernType;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public final class ModCommands{
	private ModCommands(){}

	public static void init(CommandDispatcher<CommandSource> dispatcher){
		// TODO Suggest position(maybe need to create TavernPosArgument?)
		dispatcher.register(literal("tavernMemory")
				.requires(cs -> cs.hasPermission(4))
				.then(literal("add")
						.then(argument("player", EntityArgument.player())
								.then(argument("dimension", DimensionArgument.dimension())
										.then(argument("position", BlockPosArgument.blockPos())
												.executes(ctx -> addTavernMemory(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), DimensionArgument.getDimension(ctx, "dimension"), BlockPosArgument.getOrLoadBlockPos(ctx, "position")))
										)
								)
						)
				).then(literal("remove")
						.then(argument("player", EntityArgument.player())
								.then(argument("dimension", DimensionArgument.dimension())
										.then(argument("position", BlockPosArgument.blockPos())
												.executes(ctx -> removeTavernMemory(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), DimensionArgument.getDimension(ctx, "dimension"), BlockPosArgument.getOrLoadBlockPos(ctx, "position")))
										)
								)
						)
				).then(literal("global")
						.then(literal("add")
								.then(argument("dimension", DimensionArgument.dimension())
										.then(argument("position", BlockPosArgument.blockPos())
												.executes(ctx -> addGlobalTavernMemory(ctx.getSource(), DimensionArgument.getDimension(ctx, "dimension"), BlockPosArgument.getOrLoadBlockPos(ctx, "position")))
										)
								)
						).then(literal("remove")
								.then(argument("dimension", DimensionArgument.dimension())
										.then(argument("position", BlockPosArgument.blockPos())
												.executes(ctx -> removeGlobalTavernMemory(ctx.getSource(), DimensionArgument.getDimension(ctx, "dimension"), BlockPosArgument.getOrLoadBlockPos(ctx, "position")))
										)
								)
						).executes(ctx -> listTavernMemory(ctx.getSource(), GlobalTavernMemory.get()))
				).then(argument("player", EntityArgument.player())
						.executes(ctx -> listTavernMemory(ctx.getSource(), PlayerTavernMemory.get(EntityArgument.getPlayer(ctx, "player"))))
				)
		);
	}

	private static int addTavernMemory(CommandSource sender, PlayerEntity player, ServerWorld world, BlockPos pos){
		if(!ServerWorld.isInWorldBounds(pos)) sender.sendFailure(new TranslationTextComponent("command.tavern_memory.add.out_of_world", new TavernPos(world, pos)));
		else if(!world.isAreaLoaded(pos, 0)) sender.sendFailure(new TranslationTextComponent("command.tavern_memory.add.unloaded", new TavernPos(world, pos)));
		else{
			TileEntity te = world.getBlockEntity(pos);
			if(te instanceof Tavern){
				PlayerTavernMemory m = PlayerTavernMemory.get(player);
				m.add((Tavern)te);
				m.sync();
				sender.sendSuccess(new TranslationTextComponent("command.tavern_memory.add.success", new TavernPos(world, pos), player.getDisplayName()), true);
				return SINGLE_SUCCESS;
			}else sender.sendFailure(new TranslationTextComponent("command.tavern_memory.add.no_tavern", new TavernPos(world, pos)));
		}
		return 0;
	}

	private static int removeTavernMemory(CommandSource sender, PlayerEntity player, ServerWorld dim, BlockPos pos){
		PlayerTavernMemory m = PlayerTavernMemory.get(player);
		if(m.delete(dim, pos)!=null){
			m.sync();
			sender.sendSuccess(new TranslationTextComponent("command.tavern_memory.remove.success", new TavernPos(dim, pos), player.getDisplayName()), true);
			return SINGLE_SUCCESS;
		}else{
			sender.sendFailure(new TranslationTextComponent("command.tavern_memory.remove.no_memory", new TavernPos(dim, pos)));
			return 0;
		}
	}

	private static int addGlobalTavernMemory(CommandSource sender, ServerWorld world, BlockPos pos){
		if(!ServerWorld.isInWorldBounds(pos)) sender.sendFailure(new TranslationTextComponent("command.tavern_memory.add.out_of_world", new TavernPos(world, pos)));
		else if(!world.isAreaLoaded(pos, 0)) sender.sendFailure(new TranslationTextComponent("command.tavern_memory.add.unloaded", new TavernPos(world, pos)));
		else{
			TileEntity te = world.getBlockEntity(pos);
			if(te instanceof Tavern){
				Tavern teTavern = (Tavern)te;
				if(teTavern.tavernType()==TavernType.GLOBAL){
					GlobalTavernMemory.get().add(teTavern);
					sender.sendSuccess(new TranslationTextComponent("command.tavern_memory.add.success.global", new TavernPos(world, pos)), true);
					return SINGLE_SUCCESS;
				}else sender.sendSuccess(new TranslationTextComponent("command.tavern_memory.add.not_global", new TavernPos(world, pos)), true);
			}else sender.sendFailure(new TranslationTextComponent("command.tavern_memory.add.no_tavern", new TavernPos(world, pos)));
		}
		return 0;
	}

	private static int removeGlobalTavernMemory(CommandSource sender, ServerWorld world, BlockPos pos){
		if(GlobalTavernMemory.get().delete(world, pos)!=null){
			sender.sendSuccess(new TranslationTextComponent("command.tavern_memory.remove.success.global", new TavernPos(world, pos)), true);
			return SINGLE_SUCCESS;
		}else{
			sender.sendFailure(new TranslationTextComponent("command.tavern_memory.remove.no_memory", new TavernPos(world, pos)));
			return 0;
		}
	}

	private static int listTavernMemory(CommandSource sender, TavernMemory tavernMemory){
		switch(tavernMemory.memories().size()){
			case 0:
				sender.sendSuccess(new TranslationTextComponent("command.tavern_memory.tavern.no_entry"), false);
				return 0;
			case 1:
				sender.sendSuccess(new TranslationTextComponent("command.tavern_memory.tavern.entry"), false);
				break;
			default:
				sender.sendSuccess(new TranslationTextComponent("command.tavern_memory.tavern.entries", tavernMemory.memories().size()), false);
		}
		for(TavernRecord memory : tavernMemory.memories()){
			ITextComponent access = new StringTextComponent(memory.getOwner().getAccessModifier().name());
			ITextComponent type = new StringTextComponent(memory.getTavernType().name.toUpperCase());

			sender.sendSuccess(new TranslationTextComponent(memory.isMissing() ? "command.tavern_memory.tavern.missing" : "command.tavern_memory.tavern",
					memory.getName()!=null ? memory.getName() : new TranslationTextComponent("info.hearthstones.tavern.noName"),
					access,
					type,
					memory.getOwner(),
					memory.getTavernPos()), false);
		}
		return tavernMemory.memories().size();
	}
}
