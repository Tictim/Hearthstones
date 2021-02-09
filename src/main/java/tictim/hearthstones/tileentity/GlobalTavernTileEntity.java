package tictim.hearthstones.tileentity;

import net.minecraft.item.ItemStack;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.contents.ModTileEntities;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.utils.TavernType;

public class GlobalTavernTileEntity extends BaseTavernTileEntity{
	public GlobalTavernTileEntity(){
		super(ModTileEntities.GLOBAL_TAVERN.get());
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
