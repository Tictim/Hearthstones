package tictim.hearthstones.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.contents.ModBlockEntities;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.utils.HearthingContext;
import tictim.hearthstones.utils.TavernType;

public class ShabbyTavernBlockEntity extends TavernBlockEntity{
	public ShabbyTavernBlockEntity(BlockPos pos, BlockState state){
		super(ModBlockEntities.SHABBY_TAVERN.get(), pos, state);
	}

	@Override public TavernType tavernType(){
		return TavernType.SHABBY;
	}
	@Override public boolean canBeUpgraded(){
		return true;
	}
	@Override public ItemStack createUpgradeItem(){
		return new ItemStack(ModItems.TATTERED_TAVERNCLOTH.get());
	}

	@Override public boolean canTeleportTo(HearthingContext ctx){
		return super.canTeleportTo(ctx)&&ctx.getStack().getItem()==ModItems.HEARTHING_PLANKS.get();
	}
}
