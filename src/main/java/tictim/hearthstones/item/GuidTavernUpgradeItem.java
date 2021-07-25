package tictim.hearthstones.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.logic.Tavern;

import javax.annotation.Nullable;
import java.util.List;

public class GuidTavernUpgradeItem extends BaseTavernUpgradeItem{
	public GuidTavernUpgradeItem(Properties properties){
		super(properties);
	}

	@Override protected BlockState getStateToReplace(Tavern tavern){
		return ModBlocks.GLOBAL_TAVERN.get().defaultBlockState();
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("info.hearthstones.blue_taverncloth.tooltip"));
	}
}
