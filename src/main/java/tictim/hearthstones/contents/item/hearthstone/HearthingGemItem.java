package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import tictim.hearthstones.contents.ModOreDict;
import tictim.hearthstones.hearthstone.HearthingGemHearthstone;

import javax.annotation.Nullable;
import java.util.List;

public class HearthingGemItem extends ScreenBasedHearthstoneItem{
	public HearthingGemItem(){
		super(new HearthingGemHearthstone());
	}

	@Override public boolean getIsRepairable(ItemStack toRepair, ItemStack repair){
		return OreDictionary.containsMatch(false, OreDictionary.getOres(ModOreDict.GEM_AQUAMARINE), repair);
	}

	@Override public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add(I18n.format("info.hearthstones.hearthstone.tooltip"));
		tooltip.add(I18n.format("info.hearthstones.hearthing_gem.tooltip"));
	}

	@Override protected boolean isHearthingGem(){
		return true;
	}
}