package tictim.hearthstones.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.logic.Tavern;

import javax.annotation.Nullable;
import java.util.List;

public class RegularTavernUpgradeItem extends BaseTavernUpgradeItem{
	public RegularTavernUpgradeItem(Properties properties){
		super(properties);
	}

	@Override protected BlockState getStateToReplace(Tavern tavern){
		return ModBlocks.TAVERN.get().defaultBlockState();
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent(ModCfg.easyMode() ? "info.hearthstones.taverncloth.tooltip.easyMode" : "info.hearthstones.taverncloth.tooltip"));
	}
}
