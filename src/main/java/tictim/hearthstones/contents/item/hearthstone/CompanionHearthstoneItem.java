package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import tictim.hearthstones.contents.ModTags;
import tictim.hearthstones.hearthstone.CompanionHearthstone;

import javax.annotation.Nullable;
import java.util.List;

public class CompanionHearthstoneItem extends ScreenBasedHearthstoneItem{
	public CompanionHearthstoneItem(Properties properties){
		super(properties, new CompanionHearthstone());
	}

	@Override public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair){
		return ModTags.has(ModTags.HEARTHSTONE_MATERIAL, repair.getItem());
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("info.hearthstones.hearthstone.tooltip"));
		tooltip.add(new TranslatableComponent("info.hearthstones.companion_hearthstone.tooltip.0"));
		tooltip.add(new TranslatableComponent("info.hearthstones.companion_hearthstone.tooltip.1"));
	}
}
