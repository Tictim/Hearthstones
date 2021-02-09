package tictim.hearthstones.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import tictim.hearthstones.contents.ModTags;
import tictim.hearthstones.logic.CompanionHearthstone;

import javax.annotation.Nullable;
import java.util.List;

public class CompanionHearthstoneItem extends GuiHearthstoneItem{
	public CompanionHearthstoneItem(Properties properties){
		super(properties, new CompanionHearthstone());
	}

	@Override public boolean getIsRepairable(ItemStack toRepair, ItemStack repair){
		return ModTags.HEARTHSTONE_MATERIAL.contains(repair.getItem());
	}

	@Override public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("info.hearthstones.hearthstone.tooltip"));
		tooltip.add(new TranslationTextComponent("info.hearthstones.companion_hearthstone.tooltip.0"));
		tooltip.add(new TranslationTextComponent("info.hearthstones.companion_hearthstone.tooltip.1"));
	}
}
