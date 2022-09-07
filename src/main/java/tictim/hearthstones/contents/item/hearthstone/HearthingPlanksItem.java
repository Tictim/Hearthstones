package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModOreDict;
import tictim.hearthstones.hearthstone.HearthingPlanksHearthstone;

import javax.annotation.Nullable;
import java.util.List;

public class HearthingPlanksItem extends HearthstoneItem{
	public HearthingPlanksItem(){
		super(new HearthingPlanksHearthstone());
	}

	@Override public boolean getIsRepairable(ItemStack toRepair, ItemStack repair){
		return ModOreDict.matches(repair, ModOreDict.LOG_WOOD, false);
	}

	@Override public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items){
		if(this.isInCreativeTab(tab)&&!ModCfg.easyMode()) items.add(new ItemStack(this));
	}

	@Override public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add(I18n.format("info.hearthstones.hearthing_planks.tooltip.0"));
		tooltip.add(I18n.format("info.hearthstones.hearthing_planks.tooltip.1"));
	}
}
