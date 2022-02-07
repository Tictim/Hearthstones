package tictim.hearthstones.contents.item.tavernupgrade;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.contents.block.TavernBlock;
import tictim.hearthstones.contents.tileentity.TavernTile;
import tictim.hearthstones.tavern.TavernType;

import javax.annotation.Nullable;
import java.util.List;

public class GuildTavernUpgradeItem extends TavernUpgradeItem{
	@Override protected IBlockState getStateToReplace(TavernTile tavern){
		return ModBlocks.TAVERN.getDefaultState().withProperty(TavernBlock.TAVERN_TYPE, TavernType.GLOBAL);
	}

	@Override public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add(I18n.format("info.hearthstones.blue_taverncloth.tooltip"));
	}
}
