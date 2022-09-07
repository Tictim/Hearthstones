package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModOreDict;
import tictim.hearthstones.hearthstone.SelectionHearthstone;

import javax.annotation.Nullable;
import java.util.List;

public class NormalHearthstoneItem extends ScreenBasedHearthstoneItem{
	public NormalHearthstoneItem(){
		super(new SelectionHearthstone(ModCfg.hearthstones.hearthstone));
	}

	@Override public boolean getIsRepairable(ItemStack toRepair, ItemStack repair){
		return ModOreDict.matches(repair, ModOreDict.HEARTHSTONE_MATERIAL, false);
	}

	@Override public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add(I18n.format("info.hearthstones.hearthstone.tooltip"));
	}
}
