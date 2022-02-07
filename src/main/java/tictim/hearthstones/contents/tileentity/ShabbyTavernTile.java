package tictim.hearthstones.contents.tileentity;

import net.minecraft.item.ItemStack;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.hearthstone.WarpContext;
import tictim.hearthstones.tavern.TavernType;

public class ShabbyTavernTile extends TavernTile{
	@Override protected ItemStack getUpgradeItem(){
		return new ItemStack(ModItems.TATTERED_TAVERNCLOTH);
	}
	@Override public TavernType type(){
		return TavernType.SHABBY;
	}

	@Override public boolean canTeleportTo(WarpContext context){
		return super.canTeleportTo(context)&&context.getStack().getItem()==ModItems.SHABBY_HEARTHSTONE;
	}
}
