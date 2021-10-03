package tictim.hearthstones.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.contents.blockentity.GlobalTavernBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class GlobalTavernBlock extends BaseTavernBlock{
	@Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new GlobalTavernBlockEntity(pos, state);
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslatableComponent("info.hearthstones.tavern.global.tooltip.1"));
	}

	@Override protected void addTipInformation(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		super.addTipInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslatableComponent("info.hearthstones.tavern.global.tooltip.0"));
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player){
		super.playerWillDestroy(world, pos, state, player);
		if(!world.isClientSide){
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof GlobalTavernBlockEntity){
				GlobalTavernMemory.get().delete(new TavernPos(te));
			}
		}
	}
}
