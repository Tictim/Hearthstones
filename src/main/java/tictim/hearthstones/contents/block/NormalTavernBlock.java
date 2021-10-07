package tictim.hearthstones.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.contents.blockentity.NormalTavernBlockEntity;

public class NormalTavernBlock extends TavernBlock{
	@Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new NormalTavernBlockEntity(pos, state);
	}
}
