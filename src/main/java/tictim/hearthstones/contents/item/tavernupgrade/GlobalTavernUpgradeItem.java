package tictim.hearthstones.contents.item.tavernupgrade;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.contents.blockentity.TavernBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class GlobalTavernUpgradeItem extends TavernUpgradeItem{
	public GlobalTavernUpgradeItem(Properties properties){
		super(properties);
	}

	@Override protected BlockState getStateToReplace(TavernBlockEntity tavern){
		return ModBlocks.GLOBAL_TAVERN.get().defaultBlockState();
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("info.hearthstones.blue_taverncloth.tooltip"));
	}
}
