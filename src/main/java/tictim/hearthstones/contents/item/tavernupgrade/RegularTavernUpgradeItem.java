package tictim.hearthstones.contents.item.tavernupgrade;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.contents.block.TavernBlock;
import tictim.hearthstones.contents.tileentity.TavernTile;
import tictim.hearthstones.tavern.TavernType;

import javax.annotation.Nullable;
import java.util.List;

public class RegularTavernUpgradeItem extends TavernUpgradeItem{
	@Nullable @Override protected IBlockState getStateToReplace(TavernTile tavern){
		return ModBlocks.TAVERN.getDefaultState().withProperty(TavernBlock.TAVERN_TYPE, TavernType.NORMAL);
	}

	@Override public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add(I18n.format(ModCfg.easyMode() ?
				"info.hearthstones.taverncloth.tooltip.easyMode" :
				"info.hearthstones.taverncloth.tooltip"));
	}
}
