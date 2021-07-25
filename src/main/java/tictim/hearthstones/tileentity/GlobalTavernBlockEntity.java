package tictim.hearthstones.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.contents.ModBlockEntities;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.utils.TavernType;

public class GlobalTavernBlockEntity extends TavernBlockEntity{
	public GlobalTavernBlockEntity(BlockPos pos, BlockState state){
		super(ModBlockEntities.GLOBAL_TAVERN.get(), pos, state);
	}

	@Override public TavernType tavernType(){
		return TavernType.GLOBAL;
	}
	@Override public boolean canBeUpgraded(){
		return true;
	}
	@Override public ItemStack createUpgradeItem(){
		return new ItemStack(ModItems.BLUE_TAVERNCLOTH.get());
	}

	@Override public void onLoad(){
		GlobalTavernMemory.get().add(this);
	}
}
