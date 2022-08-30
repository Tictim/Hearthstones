package tictim.hearthstones.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.blockentity.ShabbyTavernBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class ShabbyTavernBlock extends TavernBlock{
	@Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new ShabbyTavernBlockEntity(pos, state);
	}

	@Override protected void addTipInformation(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag){
		super.addTipInformation(stack, level, tooltip, flag);
		tooltip.add(Component.translatable("info.hearthstones.tavern.shabby.tooltip"));
	}

	@Override public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items){
		if(!ModCfg.easyMode()) items.add(new ItemStack(this));
	}
}
