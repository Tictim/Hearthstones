package tictim.hearthstones.contents.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.contents.ModBlockEntities;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.hearthstone.WarpContext;
import tictim.hearthstones.tavern.TavernType;

public class ShabbyTavernBlockEntity extends TavernBlockEntity{
	public ShabbyTavernBlockEntity(BlockPos pos, BlockState state){
		super(ModBlockEntities.SHABBY_TAVERN.get(), pos, state);
	}

	@Override protected ItemStack getUpgradeItem(){
		return new ItemStack(ModItems.TATTERED_TAVERNCLOTH.get());
	}
	@Override public TavernType type(){
		return TavernType.SHABBY;
	}

	@Override public boolean canTeleportTo(WarpContext context){
		return super.canTeleportTo(context)&&context.getStack().getItem()==ModItems.HEARTHING_PLANKS.get();
	}
}
