package tictim.hearthstones.contents.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.logic.Tavern;
import tictim.hearthstones.utils.TavernType;

public abstract class BaseTavernUpgradeItem extends Item{
	public BaseTavernUpgradeItem(Properties properties){
		super(properties);
	}

	@Override public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context){
		BlockEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
		if(!(te instanceof Tavern tavern)||!isValidTarget(tavern)||context.getLevel().isClientSide)
			return InteractionResult.PASS;
		BlockState stateCache = context.getLevel().getBlockState(context.getClickedPos());
		BlockState state = getStateToReplace(tavern);
		if(state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)){
			state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, stateCache.getValue(BlockStateProperties.HORIZONTAL_FACING));
		}
		context.getLevel().setBlockAndUpdate(context.getClickedPos(), state);
		if(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof Tavern tavern2){
			if(tavern.hasCustomName()) tavern2.setName(tavern.getName());
			tavern2.owner().reset(tavern.owner());
			if(tavern.tavernType()==TavernType.GLOBAL) GlobalTavernMemory.get().delete(tavern.tavernPos());
			context.getLevel().sendBlockUpdated(context.getClickedPos(), state, state, 0);

			if(context.getPlayer()==null||!context.getPlayer().isCreative()){
				stack.shrink(1);
				ItemStack s = tavern.createUpgradeItem();
				if(!s.isEmpty()) Containers.dropItemStack(context.getLevel(), context.getClickedPos().getX()+0.5, context.getClickedPos().getY()+0.5, context.getClickedPos().getZ()+0.5, s);
			}
			if(context.getPlayer()!=null){
				PlayerTavernMemory memory = PlayerTavernMemory.get(context.getPlayer());
				memory.add(tavern2);
				memory.sync();
			}
		}else{
			Hearthstones.LOGGER.error("TavernUpgradeItem {} replaced BlockState {} at {} to {}, but failed to retrieve Tavern.", getRegistryName(), stateCache, context.getClickedPos(), state);

			if(context.getPlayer()!=null){
				PlayerTavernMemory memory = PlayerTavernMemory.get(context.getPlayer());
				memory.delete(tavern.tavernPos());
				memory.sync();
			}
		}
		context.getLevel().playSound(null, context.getClickedPos().getX()+0.5, context.getClickedPos().getY()+0.5, context.getClickedPos().getZ()+0.5, SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.BLOCKS, 0.8f, 1);
		return InteractionResult.SUCCESS;
	}

	protected boolean isValidTarget(Tavern tavern){
		return tavern.canBeUpgraded();
	}

	protected abstract BlockState getStateToReplace(Tavern tavern);
}
