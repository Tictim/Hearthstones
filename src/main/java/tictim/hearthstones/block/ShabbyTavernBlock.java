package tictim.hearthstones.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.tileentity.ShabbyTavernBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class ShabbyTavernBlock extends BaseTavernBlock{
	@Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new ShabbyTavernBlockEntity(pos, state);
	}

	@Override protected void addTipInformation(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		super.addTipInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslatableComponent("info.hearthstones.tavern.shabby.tooltip"));
	}

	@Override public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items){
		if(!ModCfg.easyMode()) items.add(new ItemStack(this));
	}
}
