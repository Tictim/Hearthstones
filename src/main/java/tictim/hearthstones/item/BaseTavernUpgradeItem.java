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
		TileEntity te = context.getWorld().getTileEntity(context.getPos());
		if(te instanceof Tavern){
			Tavern tavern = (Tavern)te;
			if(isValidTarget(tavern)){
				if(!context.getWorld().isRemote){
					BlockState stateCache = context.getWorld().getBlockState(context.getPos());
					BlockState state = getStateToReplace(tavern);
					if(state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)){
						state = state.with(BlockStateProperties.HORIZONTAL_FACING, stateCache.get(BlockStateProperties.HORIZONTAL_FACING));
					}
					context.getWorld().setBlockState(context.getPos(), state);
					TileEntity te2 = context.getWorld().getTileEntity(context.getPos());
					if(te2 instanceof Tavern){
						Tavern tavern2 = (Tavern)te2;
						if(tavern.hasCustomName()) tavern2.setName(tavern.getName());
						tavern2.owner().reset(tavern.owner());
						if(tavern.tavernType()==TavernType.GLOBAL) GlobalTavernMemory.get().delete(tavern.tavernPos());
						context.getWorld().notifyBlockUpdate(context.getPos(), state, state, 0);

						if(context.getPlayer()==null||!context.getPlayer().isCreative()){
							stack.shrink(1);
							ItemStack s = tavern.createUpgradeItem();
							if(!s.isEmpty()) InventoryHelper.spawnItemStack(context.getWorld(), context.getPos().getX()+0.5, context.getPos().getY()+0.5, context.getPos().getZ()+0.5, s);
						}
						if(context.getPlayer()!=null){
							PlayerTavernMemory memory = PlayerTavernMemory.get(context.getPlayer());
							memory.add(tavern2);
							memory.sync();
						}
					}else{
						Hearthstones.LOGGER.error("TavernUpgradeItem {} replaced BlockState {} at {} to {}, but failed to retrieve Tavern.", getRegistryName(), stateCache, context.getPos(), state);

						if(context.getPlayer()!=null){
							PlayerTavernMemory memory = PlayerTavernMemory.get(context.getPlayer());
							memory.delete(tavern.tavernPos());
							memory.sync();
						}
					}
					context.getWorld().playSound(null, context.getPos().getX()+0.5, context.getPos().getY()+0.5, context.getPos().getZ()+0.5, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.BLOCKS, 0.8f, 1);
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
