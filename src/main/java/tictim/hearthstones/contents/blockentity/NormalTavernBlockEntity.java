package tictim.hearthstones.contents.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.contents.ModBlockEntities;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.tavern.TavernType;

public class NormalTavernBlockEntity extends TavernBlockEntity{
	public NormalTavernBlockEntity(BlockPos pos, BlockState state){
		super(ModBlockEntities.TAVERN.get(), pos, state);
	}

	@Override protected ItemStack getUpgradeItem(){
		return new ItemStack(ModItems.TAVERNCLOTH.get());
	}
	@Override public TavernType type(){
		return TavernType.NORMAL;
	}
}
