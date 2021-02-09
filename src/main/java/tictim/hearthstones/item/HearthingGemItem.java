package tictim.hearthstones.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import tictim.hearthstones.contents.ModTags;
import tictim.hearthstones.logic.HearthingGemHearthstone;

import javax.annotation.Nullable;
import java.util.List;

public class HearthingGemItem extends GuiHearthstoneItem{
	public HearthingGemItem(Properties properties){
		super(properties, new HearthingGemHearthstone());
	}

	@Override public boolean getIsRepairable(ItemStack toRepair, ItemStack repair){
		return ModTags.GEMS_AQUAMARINE.contains(repair.getItem());
	}

	@Override public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("info.hearthstones.hearthstone.tooltip"));
		tooltip.add(new TranslationTextComponent("info.hearthstones.hearthing_gem.tooltip"));
	}
}
