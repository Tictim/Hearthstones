package tictim.hearthstones.tileentity;

import net.minecraft.item.ItemStack;
import tictim.hearthstones.contents.ModItems;

public class NormalTavernTileEntity extends BaseTavernTileEntity{
	@Override public boolean canBeUpgraded(){
		return true;
	}
	@Override public ItemStack createUpgradeItem(){
		return new ItemStack(ModItems.TAVERNCLOTH.get());
	}
}
