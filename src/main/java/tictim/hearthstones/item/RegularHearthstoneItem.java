package tictim.hearthstones.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModTags;
import tictim.hearthstones.logic.GuiHearthstone;

import javax.annotation.Nullable;
import java.util.List;

public class RegularHearthstoneItem extends GuiHearthstoneItem{
	public RegularHearthstoneItem(Properties properties){
		super(properties, new GuiHearthstone(ModCfg.hearthstone));
	}

	@Override public boolean getIsRepairable(ItemStack toRepair, ItemStack repair){
		return ModTags.HEARTHSTONE_MATERIAL.contains(repair.getItem());
	}

	@Override public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("info.hearthstones.hearthstone.tooltip"));
	}
}
