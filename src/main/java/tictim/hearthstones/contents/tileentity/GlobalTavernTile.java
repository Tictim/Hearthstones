package tictim.hearthstones.contents.tileentity;

import net.minecraft.item.ItemStack;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernType;

public class GlobalTavernTile extends TavernTile{
	@Override protected ItemStack getUpgradeItem(){
		return new ItemStack(ModItems.BLUE_TAVERNCLOTH);
	}
	@Override public TavernType type(){
		return TavernType.GLOBAL;
	}

	@Override public void onLoad(){
		TavernMemories m = TavernMemories.get();
		if(m!=null) m.getGlobal().addOrUpdate(this);
	}
}