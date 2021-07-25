package tictim.hearthstones.item;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import tictim.hearthstones.contents.ModTags;
import tictim.hearthstones.logic.HearthingGemHearthstone;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

public class HearthingGemItem extends GuiHearthstoneItem{
	public HearthingGemItem(Properties properties){
		super(properties, new HearthingGemHearthstone());
	}

	@Override public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair){
		return ModTags.GEMS_AQUAMARINE.contains(repair.getItem());
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("info.hearthstones.hearthstone.tooltip"));
		tooltip.add(new TranslatableComponent("info.hearthstones.hearthing_gem.tooltip"));
	}
}
