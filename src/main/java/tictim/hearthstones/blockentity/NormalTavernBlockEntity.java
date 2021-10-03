package tictim.hearthstones.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.contents.ModBlockEntities;
import tictim.hearthstones.contents.ModItems;

public class NormalTavernBlockEntity extends TavernBlockEntity{
	public NormalTavernBlockEntity(BlockPos pos, BlockState state){
		super(ModBlockEntities.TAVERN.get(), pos, state);
	}

	@Override public boolean canBeUpgraded(){
		return true;
	}
	@Override public ItemStack createUpgradeItem(){
		return new ItemStack(ModItems.TAVERNCLOTH.get());
	}
}
