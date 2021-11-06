package tictim.hearthstones.contents.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.contents.ModBlockEntities;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernType;

public class GlobalTavernBlockEntity extends TavernBlockEntity{
	public GlobalTavernBlockEntity(BlockPos pos, BlockState state){
		super(ModBlockEntities.GLOBAL_TAVERN.get(), pos, state);
	}

	@Override protected ItemStack getUpgradeItem(){
		return new ItemStack(ModItems.BLUE_TAVERNCLOTH.get());
	}
	@Override public TavernType type(){
		return TavernType.GLOBAL;
	}

	@Override public void onLoad(){
		TavernMemories.global().addOrUpdate(this);
	}
}
