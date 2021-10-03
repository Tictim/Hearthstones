package tictim.hearthstones.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.blockentity.NormalTavernBlockEntity;

public class NormalTavernBlock extends BaseTavernBlock{
	@Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new NormalTavernBlockEntity(pos, state);
	}
}
