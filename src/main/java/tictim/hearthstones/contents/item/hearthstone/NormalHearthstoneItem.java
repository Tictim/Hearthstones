package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModTags;
import tictim.hearthstones.hearthstone.SelectionHearthstone;

import javax.annotation.Nullable;
import java.util.List;

public class NormalHearthstoneItem extends ScreenBasedHearthstoneItem{
	public NormalHearthstoneItem(Properties properties){
		super(properties, new SelectionHearthstone(ModCfg.hearthstone()));
	}

	@Override public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair){
		return ModTags.has(ModTags.HEARTHSTONE_MATERIAL, repair.getItem());
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("info.hearthstones.hearthstone.tooltip"));
	}
}
