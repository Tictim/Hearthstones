package tictim.hearthstones.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.logic.Tavern;

import javax.annotation.Nullable;
import java.util.List;

public class ShabbyTavernUpgradeItem extends BaseTavernUpgradeItem{
	public ShabbyTavernUpgradeItem(Properties properties){
		super(properties);
	}

	@Override protected BlockState getStateToReplace(Tavern tavern){
		return ModBlocks.SHABBY_TAVERN.get().defaultBlockState();
	}

	@Override public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items){
		if(this.allowdedIn(group)&&!ModCfg.easyMode()) items.add(new ItemStack(this));
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("info.hearthstones.tattered_taverncloth.tooltip"));
	}
}
