package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import tictim.hearthstones.contents.ModTags;
import tictim.hearthstones.hearthstone.HearthingGemHearthstone;

import javax.annotation.Nullable;
import java.util.List;

public class HearthingGemItem extends ScreenBasedHearthstoneItem{
	public HearthingGemItem(Properties properties){
		super(properties, new HearthingGemHearthstone());
	}

	@Override public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair){
		return ModTags.has(ModTags.GEMS_AQUAMARINE, repair.getItem());
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(new TranslatableComponent("info.hearthstones.hearthstone.tooltip"));
		tooltip.add(new TranslatableComponent("info.hearthstones.hearthing_gem.tooltip"));
	}

	@Override protected boolean isHearthingGem(){
		return true;
	}
}
