package tictim.hearthstones.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.contents.blockentity.GlobalTavernBlockEntity;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernPos;

import javax.annotation.Nullable;
import java.util.List;

public class GlobalTavernBlock extends TavernBlock{
	@Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new GlobalTavernBlockEntity(pos, state);
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flagIn){
		super.appendHoverText(stack, level, tooltip, flagIn);
		tooltip.add(Component.translatable("info.hearthstones.tavern.global.tooltip.1"));
	}

	@Override protected void addTipInformation(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag){
		super.addTipInformation(stack, level, tooltip, flag);
		tooltip.add(Component.translatable("info.hearthstones.tavern.global.tooltip.0"));
	}

	@Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack){
		super.setPlacedBy(level, pos, state, placer, stack);
		if(!level.isClientSide&&level.getBlockEntity(pos) instanceof GlobalTavernBlockEntity tavern)
			TavernMemories.global().addOrUpdate(tavern);
	}

	@SuppressWarnings("deprecation") @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving){
		if((!state.is(newState.getBlock())||!newState.hasBlockEntity())){
			if(level.getBlockEntity(pos) instanceof GlobalTavernBlockEntity globalTavern)
				TavernMemories.global().delete(new TavernPos(globalTavern));
		}
		super.onRemove(state, level, pos, newState, moving);
	}
}
