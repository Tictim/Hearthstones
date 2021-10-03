package tictim.hearthstones.contents.item;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
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

	@Override public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("info.hearthstones.blue_taverncloth.tooltip"));
	}
}
