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
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
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
				.requires(cs -> cs.hasPermissionLevel(4))
				.then(literal("add")
						.then(argument("player", EntityArgument.player())
								.then(argument("dimension", DimensionArgument.getDimension())
										.then(argument("position", BlockPosArgument.blockPos())
												.executes(ctx -> addTavernMemory(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), DimensionArgument.getDimensionArgument(ctx, "dimension"), BlockPosArgument.getBlockPos(ctx, "position")))
										)
								)
						)
				).then(literal("remove")
						.then(argument("player", EntityArgument.player())
								.then(argument("dimension", DimensionArgument.getDimension())
										.then(argument("position", BlockPosArgument.blockPos())
												.executes(ctx -> removeTavernMemory(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), DimensionArgument.getDimensionArgument(ctx, "dimension"), BlockPosArgument.getBlockPos(ctx, "position")))
										)
								)
						)
				).then(literal("global")
						.then(literal("add")
								.then(argument("dimension", DimensionArgument.getDimension())
										.then(argument("position", BlockPosArgument.blockPos())
												.executes(ctx -> addGlobalTavernMemory(ctx.getSource(), DimensionArgument.getDimensionArgument(ctx, "dimension"), BlockPosArgument.getBlockPos(ctx, "position")))
										)
								)
						).then(literal("remove")
								.then(argument("dimension", DimensionArgument.getDimension())
										.then(argument("position", BlockPosArgument.blockPos())
												.executes(ctx -> removeGlobalTavernMemory(ctx.getSource(), DimensionArgument.getDimensionArgument(ctx, "dimension"), BlockPosArgument.getBlockPos(ctx, "position")))
										)
								)
						).executes(ctx -> listTavernMemory(ctx.getSource(), GlobalTavernMemory.get()))
				).then(argument("player", EntityArgument.player())
						.executes(ctx -> listTavernMemory(ctx.getSource(), PlayerTavernMemory.get(EntityArgument.getPlayer(ctx, "player"))))
				)
		);
	}

	private static int addTavernMemory(CommandSource sender, PlayerEntity player, DimensionType dim, BlockPos pos){
		World world = DimensionManager.getWorld(sender.getServer(), dim, false, false);
		if(world!=null){
			if(!ServerWorld.isValid(pos)) sender.sendErrorMessage(new TranslationTextComponent("command.tavern_memory.add.out_of_world", new TavernPos(dim, pos)));
			else if(!world.isAreaLoaded(pos, 0)) sender.sendErrorMessage(new TranslationTextComponent("command.tavern_memory.add.unloaded", new TavernPos(dim, pos)));
			else{
				TileEntity te = world.getTileEntity(pos);
				if(te instanceof Tavern){
					PlayerTavernMemory m = PlayerTavernMemory.get(player);
					m.add((Tavern)te);
					m.sync();
					sender.sendFeedback(new TranslationTextComponent("command.tavern_memory.add.success", new TavernPos(dim, pos), player.getDisplayName()), true);
					return SINGLE_SUCCESS;
				}else sender.sendErrorMessage(new TranslationTextComponent("command.tavern_memory.add.no_tavern", new TavernPos(dim, pos)));
			}
		}else sender.sendErrorMessage(new TranslationTextComponent("command.tavern_memory.add.unloaded", new TavernPos(dim, pos)));
		return 0;
	}

	private static int removeTavernMemory(CommandSource sender, PlayerEntity player, DimensionType dim, BlockPos pos){
		PlayerTavernMemory m = PlayerTavernMemory.get(player);
		if(m.delete(dim, pos)!=null){
			m.sync();
			sender.sendFeedback(new TranslationTextComponent("command.tavern_memory.remove.success", new TavernPos(dim, pos), player.getDisplayName()), true);
			return SINGLE_SUCCESS;
		}else{
			sender.sendErrorMessage(new TranslationTextComponent("command.tavern_memory.remove.no_memory", new TavernPos(dim, pos)));
			return 0;
		}
	}

	private static int addGlobalTavernMemory(CommandSource sender, DimensionType dim, BlockPos pos){
		World world = DimensionManager.getWorld(sender.getServer(), dim, false, false);
		if(world!=null){
			if(!ServerWorld.isValid(pos)) sender.sendErrorMessage(new TranslationTextComponent("command.tavern_memory.add.out_of_world", new TavernPos(dim, pos)));
			else if(!world.isAreaLoaded(pos, 0)) sender.sendErrorMessage(new TranslationTextComponent("command.tavern_memory.add.unloaded", new TavernPos(dim, pos)));
			else{
				TileEntity te = world.getTileEntity(pos);
				if(te instanceof Tavern){
					Tavern teTavern = (Tavern)te;
					if(teTavern.tavernType()==TavernType.GLOBAL){
						GlobalTavernMemory.get().add(teTavern);
						sender.sendFeedback(new TranslationTextComponent("command.tavern_memory.add.success.global", new TavernPos(dim, pos)), true);
						return SINGLE_SUCCESS;
					}else sender.sendFeedback(new TranslationTextComponent("command.tavern_memory.add.not_global", new TavernPos(dim, pos)), true);
				}else sender.sendErrorMessage(new TranslationTextComponent("command.tavern_memory.add.no_tavern", new TavernPos(dim, pos)));
			}
		}else sender.sendErrorMessage(new TranslationTextComponent("command.tavern_memory.add.unloaded", new TavernPos(dim, pos)));
		return 0;
	}

	private static int removeGlobalTavernMemory(CommandSource sender, DimensionType dim, BlockPos pos){
		if(GlobalTavernMemory.get().delete(dim, pos)!=null){
			sender.sendFeedback(new TranslationTextComponent("command.tavern_memory.remove.success.global", new TavernPos(dim, pos)), true);
			return SINGLE_SUCCESS;
		}else{
			sender.sendErrorMessage(new TranslationTextComponent("command.tavern_memory.remove.no_memory", new TavernPos(dim, pos)));
			return 0;
		}
	}

	private static int listTavernMemory(CommandSource sender, TavernMemory tavernMemory){
		switch(tavernMemory.memories().size()){
			case 0:
				sender.sendFeedback(new TranslationTextComponent("command.tavern_memory.tavern.no_entry"), false);
				return 0;
			case 1:
				sender.sendFeedback(new TranslationTextComponent("command.tavern_memory.tavern.entry"), false);
				break;
			default:
				sender.sendFeedback(new TranslationTextComponent("command.tavern_memory.tavern.entries", tavernMemory.memories().size()), false);
		}
		for(TavernRecord memory : tavernMemory.memories()){
			ITextComponent access = new StringTextComponent(memory.getOwner().getAccessModifier().name());
			ITextComponent type = new StringTextComponent(memory.getTavernType().name.toUpperCase());

			sender.sendFeedback(new TranslationTextComponent(memory.isMissing() ? "command.tavern_memory.tavern.missing" : "command.tavern_memory.tavern",
					memory.getName()!=null ? memory.getName() : new TranslationTextComponent("info.hearthstones.tavern.noName"),
					access,
					type,
					memory.getOwner(),
					memory.getTavernPos()), false);
		}
		return tavernMemory.memories().size();
	}
}
