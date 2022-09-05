package tictim.hearthstones.contents.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import tictim.hearthstones.config.ModCfg;

public class EasyModeItem extends RareItem{
	@Override public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items){
		if(this.isInCreativeTab(tab)&&!ModCfg.easyMode) items.add(new ItemStack(this));
	}
}
