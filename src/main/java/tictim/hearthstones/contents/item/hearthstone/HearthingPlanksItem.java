package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModTags;
import tictim.hearthstones.hearthstone.HearthingPlanksHearthstone;

import javax.annotation.Nullable;
import java.util.List;

public class HearthingPlanksItem extends HearthstoneItem{
	public HearthingPlanksItem(Properties properties){
		super(properties, new HearthingPlanksHearthstone());
	}

	@Override public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair){
		return ModTags.has(ItemTags.PLANKS, repair.getItem());
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items){
		if(this.allowedIn(group)&&!ModCfg.easyMode()) items.add(new ItemStack(this));
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("info.hearthstones.hearthing_planks.tooltip.0"));
		tooltip.add(Component.translatable("info.hearthstones.hearthing_planks.tooltip.1"));
	}
}
