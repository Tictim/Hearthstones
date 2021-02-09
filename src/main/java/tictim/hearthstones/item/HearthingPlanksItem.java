package tictim.hearthstones.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.logic.HearthingPlanksHearthstone;

import javax.annotation.Nullable;
import java.util.List;

public class HearthingPlanksItem extends BaseHearthstoneItem{
	public HearthingPlanksItem(Properties properties){
		super(properties, new HearthingPlanksHearthstone());
	}

	@Override public boolean getIsRepairable(ItemStack toRepair, ItemStack repair){
		return ItemTags.PLANKS.contains(repair.getItem());
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items){
		if(this.isInGroup(group)&&!ModCfg.easyMode()) items.add(new ItemStack(this));
	}

	@Override public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("info.hearthstones.hearthing_planks.tooltip.0"));
		tooltip.add(new TranslationTextComponent("info.hearthstones.hearthing_planks.tooltip.1"));
	}
}
