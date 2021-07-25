package tictim.hearthstones.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

	@Override public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent(ModCfg.easyMode() ? "info.hearthstones.taverncloth.tooltip.easyMode" : "info.hearthstones.taverncloth.tooltip"));
	}
}
