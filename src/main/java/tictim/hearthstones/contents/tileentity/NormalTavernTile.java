package tictim.hearthstones.contents.tileentity;

import net.minecraft.item.ItemStack;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.tavern.TavernType;

public class NormalTavernTile extends TavernTile{
	@Override protected ItemStack getUpgradeItem(){
		return new ItemStack(ModItems.TAVERNCLOTH);
	}
	@Override public TavernType type(){
		return TavernType.NORMAL;
	}
}
