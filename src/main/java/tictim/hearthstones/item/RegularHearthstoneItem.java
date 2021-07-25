package tictim.hearthstones.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModTags;
import tictim.hearthstones.logic.GuiHearthstone;

import javax.annotation.Nullable;
import java.util.List;

public class RegularHearthstoneItem extends GuiHearthstoneItem{
	public RegularHearthstoneItem(Properties properties){
		super(properties, new GuiHearthstone(ModCfg.hearthstone));
	}

	@Override public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair){
		return ModTags.HEARTHSTONE_MATERIAL.contains(repair.getItem());
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("info.hearthstones.hearthstone.tooltip"));
	}
}
