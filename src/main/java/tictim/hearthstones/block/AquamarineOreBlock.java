package tictim.hearthstones.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class AquamarineOreBlock extends Block{
	public AquamarineOreBlock(Properties properties){
		super(properties);
	}

	@Override
	public int getExpDrop(BlockState state, LevelReader world, BlockPos pos, int fortune, int silktouch){
		return Mth.nextInt(world instanceof Level ? ((Level)world).random : new Random(), 3, 7);
	}
}
