package tictim.hearthstones.item;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.logic.Tavern;
import tictim.hearthstones.utils.TavernType;

public abstract class BaseTavernUpgradeItem extends Item{
	public BaseTavernUpgradeItem(Properties properties){
		super(properties);
	}

	@Override public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context){
		TileEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
		if(te instanceof Tavern){
			Tavern tavern = (Tavern)te;
			if(isValidTarget(tavern)){
				if(!context.getLevel().isClientSide){
					BlockState stateCache = context.getLevel().getBlockState(context.getClickedPos());
					BlockState state = getStateToReplace(tavern);
					if(state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)){
						state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, stateCache.getValue(BlockStateProperties.HORIZONTAL_FACING));
					}
					context.getLevel().setBlockAndUpdate(context.getClickedPos(), state);
					TileEntity te2 = context.getLevel().getBlockEntity(context.getClickedPos());
					if(te2 instanceof Tavern){
						Tavern tavern2 = (Tavern)te2;
						if(tavern.hasCustomName()) tavern2.setName(tavern.getName());
						tavern2.owner().reset(tavern.owner());
						if(tavern.tavernType()==TavernType.GLOBAL) GlobalTavernMemory.get().delete(tavern.tavernPos());
						context.getLevel().sendBlockUpdated(context.getClickedPos(), state, state, 0);

						if(context.getPlayer()==null||!context.getPlayer().isCreative()){
							stack.shrink(1);
							ItemStack s = tavern.createUpgradeItem();
							if(!s.isEmpty()) InventoryHelper.dropItemStack(context.getLevel(), context.getClickedPos().getX()+0.5, context.getClickedPos().getY()+0.5, context.getClickedPos().getZ()+0.5, s);
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
					context.getLevel().playSound(null, context.getClickedPos().getX()+0.5, context.getClickedPos().getY()+0.5, context.getClickedPos().getZ()+0.5, SoundEvents.ARMOR_EQUIP_LEATHER, SoundCategory.BLOCKS, 0.8f, 1);
					return ActionResultType.SUCCESS;
				}
			}
		}
		return ActionResultType.PASS;
	}

	protected boolean isValidTarget(Tavern tavern){
		return tavern.canBeUpgraded();
	}

	protected abstract BlockState getStateToReplace(Tavern tavern);
}
